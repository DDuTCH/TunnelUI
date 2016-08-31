package com.michielvanderlee.tools.tunnels;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinSession;

public class TunnelService
{
	//****************************************************************************************
	// Constructors
	//****************************************************************************************
	
	//****************************************************************************************
	// Methods
	//****************************************************************************************
	public static void startTunnel( Tunnel tunnel )
	{
		try
		{
			tunnel.getProcess().execute( tunnel );
		}
		catch( IOException e )
		{
			setErrorMsg( e );
		}
	}
	
	public static void addTunnel( Tunnel tunnel )
	{		
		try {
			TunnelProcess process = new TunnelProcess();
			process.execute( tunnel );
			
			tunnel.setProcess( process );
			container.addBean( tunnel);
			saveToFile( new File( "tunnels.json" ) );
		}
		catch( IOException e )
		{
			setErrorMsg( e );
		}
	}
	
	public static void removeTunnel( Tunnel tunnel )
	{
		try {
			tunnel.getProcess().stop();
			
			container.removeItem( tunnel );
			
			saveToFile( new File( "tunnels.json" ) );
		}
		catch( IOException e )
		{
			setErrorMsg( e );
		}
	}
	
	public static void removeAll()
	{
		Iterator<Tunnel> iter = container.getItemIds().iterator();
		if( iter.hasNext() )
		{
			Tunnel tunnel = iter.next();
			removeTunnel( tunnel );
		}
	}
	
	
	public static void saveToFile( File file ) throws IOException
	{
		FileWriter writer = null;
		try
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			writer = new FileWriter( file );
			writer.write( gson.toJson( container.getItemIds() ) );
		}
		finally
		{
			if ( writer != null )
			{
				writer.close();
			}
		}
	}
	
	public static void loadFromFile( File file ) throws IOException
	{
		FileReader reader = null;
		try
		{
			Gson gson = new Gson();
			reader = new FileReader( file );

			List<Tunnel> list = gson.fromJson( reader, new TypeToken<List<Tunnel>>(){}.getType() );
			Iterator<Tunnel> iter = list.iterator();
			Tunnel tunnel;
			while( iter.hasNext() )
			{
				tunnel = iter.next();
				if( tunnel.getProcess() == null )
				{
					iter.remove();
				}
			}
			
			container.addAll( list );
		}
		finally
		{
			if ( reader != null )
			{
				reader.close();
			}
		}
		
		/* Overwrite with updated list. 
		 * It's possible that we found tunnels in the file that no longer exist, this removes them 
		 */ 
		saveToFile( file );
	}
	
	public static void setErrorMsg( Exception e )
	{
		if( errorProperty == null )
		{
			e.printStackTrace();
		}
		else
		{
			VaadinSession.getCurrent().getLockInstance().lock();
			try{
				errorProperty.setValue( e.getMessage() );
			}
			finally{
				VaadinSession.getCurrent().getLockInstance().unlock();
			}
		}
		
	}
	
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************
	public static BeanItemContainer<Tunnel> getContainer()
	{
		return container;
	}
	
	public static ObjectProperty<String> getErrorProperty()
	{
		return errorProperty;
	}
	
	public static void setErrorProperty( ObjectProperty<String> errorPropertyIn )
	{
		errorProperty = errorPropertyIn;
	}

		
	//****************************************************************************************
	// Properties
	//****************************************************************************************
	private static BeanItemContainer<Tunnel> container = new BeanItemContainer<Tunnel>( Tunnel.class );	
	private static ObjectProperty<String> errorProperty = null;
	
}
