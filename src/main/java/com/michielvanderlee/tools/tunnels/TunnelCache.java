package com.michielvanderlee.tools.tunnels;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TunnelCache
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public TunnelCache( String fileName )
	{
		file = new File( fileName );
		sequence = 0L;
		cache = new HashMap<Long, Tunnel>();
		
		listeners = new ArrayList<CacheUpdateListener<Tunnel>>();
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	public void add( Tunnel tunnel )
	{
		Long id = ++sequence;
		tunnel.setId( id );

		cache.put( id, tunnel );
		broadcastAdded( tunnel );
	}

	public void remove( Long id )
	{
		Tunnel removed = cache.remove( id );
		broadcastRemoved( removed );
	}

	public Tunnel get( Long id )
	{
		return cache.get( id );
	}

	public Collection<Tunnel> getAll()
	{
		return cache.values();
	}

	public Set<Tunnel> getByHost( String host )
	{
		Set<Tunnel> tunnels = new HashSet<Tunnel>();
		for( Tunnel tunnel : cache.values() )
		{
			if( tunnel.getHost().equals( host ) )
			{
				tunnels.add( tunnel );
			}
		}

		return tunnels;
	}

	public boolean isLocalPortUsed( int port )
	{
		for( Tunnel tunnel : cache.values() )
		{
			if( tunnel.getLocalPort() == port )
			{
				return true;
			}
		}

		return false;
	}

	public boolean isRemotePortUsed( int port )
	{
		for( Tunnel tunnel : cache.values() )
		{
			if( tunnel.getRemotePort() == port )
			{
				return true;
			}
		}

		return false;
	}

	public void saveToFile() throws IOException
	{
		FileWriter writer = null;
		try
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			writer = new FileWriter( file );
			writer.write( gson.toJson( cache.values() ) );
		}
		finally
		{
			if( writer != null )
			{
				writer.close();
			}
		}
	}

	public void loadFromFile() throws IOException
	{
		FileReader reader = null;
		try
		{
			Gson gson = new Gson();
			reader = new FileReader( file );

			List<Tunnel> list = gson.fromJson( reader, new TypeToken<List<Tunnel>>() {
			}.getType() );
			for( Tunnel tunnel : list )
			{
				add( tunnel );
			}
		}
		finally
		{
			if( reader != null )
			{
				reader.close();
			}
		}

		/*
		 * Overwrite with updated list. It's possible that we found tunnels in
		 * the file that no longer exist, this removes them
		 */
		saveToFile();
	}

	public void addListener( CacheUpdateListener<Tunnel> listener )
	{
		listeners.add( listener );
	}
	
	public void removeListener( CacheUpdateListener<Tunnel> listener )
	{
		listeners.remove( listener );
	}
	
	private void broadcastAdded( Tunnel tunnel )
	{
		for( CacheUpdateListener<Tunnel> listener : listeners )
		{
			listener.addedToCache( tunnel );
		}
	}
	
	private void broadcastRemoved( Tunnel tunnel )
	{
		for( CacheUpdateListener<Tunnel> listener : listeners )
		{
			listener.removedFromCache( tunnel );
		}
	}
	
	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private Long								sequence;
	private File								file;
	private Map<Long, Tunnel>					cache;

	private List<CacheUpdateListener<Tunnel>>	listeners;
}
