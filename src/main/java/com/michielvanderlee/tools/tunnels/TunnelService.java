package com.michielvanderlee.tools.tunnels;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinSession;

public class TunnelService
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	private TunnelService()
	{
		jsch = new JSch();
		tunnelCache = new TunnelCache( CACHE_FILE_NAME );
		try
		{
			tunnelCache.loadFromFile();
		}
		catch( IOException e )
		{
			getLogger().log( Level.SEVERE, "Failed to load from file.", e );
		}
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	public void startTunnel( Long id )
	{
		try
		{
			Tunnel tunnel = tunnelCache.get( id );
			Session session = jsch.getSession( tunnel.getTunnelUser(), tunnel.getTunnelHost() );
			
			session.setUserInfo( buildUserInfo( tunnel ) );
			session.connect();
			switch( tunnel.getType() )
			{
				case FORWARD:
					session.setPortForwardingL( tunnel.getLocalPort(), tunnel.getHost(), tunnel.getRemotePort() );
					break;
				case REVERSE:
					session.setPortForwardingR( tunnel.getRemotePort(), tunnel.getHost(), tunnel.getLocalPort() );
					break;
				case VPN:
					break;
			}

			tunnel.setSession( session );
			getLogger().info( "Opened tunnel: " + tunnel.toString() );
		}
		catch( JSchException e )
		{
			getLogger().log( Level.SEVERE, "Failed to start Tunnel.", e );
		}
	}

	public void stopTunnel( long id )
	{
		Tunnel tunnel = tunnelCache.get( id );
		if( tunnel.getSession() != null )
		{
			tunnel.getSession().disconnect();

			getLogger().info( "Closed tunnel: " + tunnel.toString() );
		}
	}

	public void addTunnel( Tunnel tunnel )
	{
		try
		{
			tunnelCache.add( tunnel );
			tunnelCache.saveToFile();
			startTunnel( tunnel.getId() );
		}
		catch( IOException e )
		{
			getLogger().log( Level.SEVERE, "Failed to start Tunnel.", e );
		}
	}

	public void removeTunnel( Long id )
	{
		stopTunnel( id );
		tunnelCache.remove( id );
	}

	public void removeAll()
	{
		Iterator<Tunnel> iter = tunnelCache.getAll().iterator();
		while( iter.hasNext() )
		{
			Tunnel tunnel = iter.next();
			removeTunnel( tunnel.getId() );
		}
	}

	public void setErrorMsg( Exception e )
	{
		if( errorProperty == null )
		{
			e.printStackTrace();
		}
		else
		{
			VaadinSession.getCurrent().getLockInstance().lock();
			try
			{
				errorProperty.setValue( e.getMessage() );
			}
			finally
			{
				VaadinSession.getCurrent().getLockInstance().unlock();
			}
		}

	}

	public static TunnelService getInstance()
	{
		if( instance == null )
		{
			instance = new TunnelService();
		}

		return instance;
	}

	private UserInfo buildUserInfo( Tunnel tunnel )
	{
		UserInfo userInfo = new UserInfo() {

			@Override
			public void showMessage( String message )
			{
				getLogger().log( Level.INFO, message );
			}

			@Override
			public boolean promptYesNo( String message )
			{
				return true;
			}

			@Override
			public boolean promptPassword( String message )
			{
				return true;
			}

			@Override
			public boolean promptPassphrase( String message )
			{
				return false;
			}

			@Override
			public String getPassword()
			{
				return tunnel.getTunnelPassword();
			}

			@Override
			public String getPassphrase()
			{
				return null;
			}
		};

		return userInfo;
	}

	private static Logger getLogger()
	{
		return Logger.getLogger( TunnelService.class.getName() );
	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	public TunnelCache getCache()
	{
		return tunnelCache;
	}

	public ObjectProperty<String> getErrorProperty()
	{
		return errorProperty;
	}

	public void setErrorProperty( ObjectProperty<String> errorPropertyIn )
	{
		errorProperty = errorPropertyIn;
	}

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private static final String		CACHE_FILE_NAME	= "tunnels.json";
	private static TunnelService	instance;

	private ObjectProperty<String>	errorProperty	= null;
	private JSch					jsch;
	private TunnelCache				tunnelCache;
}
