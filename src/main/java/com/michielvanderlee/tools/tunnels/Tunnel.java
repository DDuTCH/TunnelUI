package com.michielvanderlee.tools.tunnels;

import java.io.Serializable;

import com.jcraft.jsch.Session;

public class Tunnel implements Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public Tunnel( Integer localPort, Integer hostPort, String host, String tunnelHost, String tunnelUser, String tunnelPassword, TunnelType type )
	{
		this.localPort = localPort;
		this.remotePort = hostPort;
		this.host = host;
		this.tunnelHost = tunnelHost;
		this.tunnelUser = tunnelUser;
		this.tunnelPassword = tunnelPassword;
		this.type = type;
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************	
	@Override
	public Tunnel clone()
	{
		return new TunnelBuilder()
				.setHost( host )
				.setLocalPort( localPort )
				.setRemotePort( remotePort )
				.setTunnelHost( tunnelHost )
				.setTunnelUser( tunnelUser )
				.setTunnelPassword( tunnelPassword )
				.setTunnelType( type )
				.build();
	}
	
	@Override
	public String toString()
	{
		return new StringBuilder()
				.append( type == TunnelType.FORWARD ? "-L" : "-R" )
				.append( " " )
				.append( localPort )
				.append( ':' )
				.append( host )
				.append( ':' )
				.append( remotePort )
				.append( ' ' )
				.append( tunnelUser )
				.append( '@' )
				.append( tunnelHost )
				.toString();
	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	// @formatter:off
	public Long getId()
	{
		return id;
	}
	public void setId( Long id )
	{
		this.id = id;
	}
	
	public Integer getLocalPort()
	{
		return localPort;
	}
	public void setLocalPort( Integer localPort )
	{
		this.localPort = localPort;
	}

	public Integer getRemotePort()
	{
		return remotePort;
	}
	public void setRemotePort( Integer remotePort )
	{
		this.remotePort = remotePort;
	}

	public String getHost()
	{
		return host;
	}
	public void setHost( String host )
	{
		this.host = host;
	}
	
	public String getTunnelHost()
	{
		return tunnelHost;
	}
	public void setTunnelHost( String tunnelHost )
	{
		this.tunnelHost = tunnelHost;
	}
	
	public String getTunnelUser()
	{
		return tunnelUser;
	}
	public void setTunnelUser( String tunnelUser )
	{
		this.tunnelUser = tunnelUser;
	}
	
	public String getTunnelPassword()
	{
		return tunnelPassword;
	}
	public void setTunnelPassword( String tunnelPassword )
	{
		this.tunnelPassword = tunnelPassword;
	}
	
	public TunnelType getType()
	{
		return type;
	}
	public void setType( TunnelType type )
	{
		this.type = type;
	}

	public Session getSession()
	{
		return session;
	}
	public void setSession( Session session )
	{
		this.session = session;
	}

	public boolean isConnected()
	{
		if( session == null )
		{
			return false;
		}
		return session.isConnected();
	}
	
	// @formatter:on
	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private transient Long		id;
	private Integer				localPort;
	private Integer				remotePort;
	private String				host;
	private String				tunnelHost;
	private String				tunnelUser;
	private String				tunnelPassword;
	private TunnelType			type;

	private transient Session	session;

	public enum TunnelField
	{
		ID( "id" ), LOCAL_PORT( "localPort" ), REMOTE_PORT( "remotePort" ), HOST( "host" ), TUNNEL_HOST( "tunnelHost" ), TUNNEL_USER( "tunnelUser" ), TUNNEL_PASSWORD( "tunnelPassword" ), TUNNEL_TYPE( "type" ), SESSION( "session" ), CONNECTED( "connected" );

		private TunnelField( String name )
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		private String name;
	}
}