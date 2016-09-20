package com.michielvanderlee.tools.tunnels;

public class TunnelBuilder
{
	//****************************************************************************************
	// Constructors
	//****************************************************************************************
	public TunnelBuilder()
	{
	}
	
	//****************************************************************************************
	// Methods
	//****************************************************************************************
	public TunnelBuilder setHost( String host )
	{
		this.host = host;
		return this;
	}
	
	public TunnelBuilder setLocalPort( int localPort )
	{
		this.localPort = localPort;
		return this;
	}
	
	public TunnelBuilder setRemotePort( int remotePort )
	{
		this.remotePort = remotePort;
		return this;
	}
	
	public TunnelBuilder setTunnelHost( String tunnelHost )
	{
		this.tunnelHost = tunnelHost;
		return this;
	}
	
	public TunnelBuilder setTunnelUser( String tunnelUser )
	{
		this.tunnelUser = tunnelUser;
		return this;
	}
	
	public TunnelBuilder setTunnelPassword( String tunnelPassword )
	{
		this.tunnelPassword = tunnelPassword; 
		return this;
	}
	
	public TunnelBuilder setTunnelType( TunnelType type )
	{
		this.type = type;
		return this;
	}
	
	public Tunnel build()
	{
		return new Tunnel( localPort, remotePort, host, tunnelHost, tunnelUser, tunnelPassword, type );
	}
	
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************

	//****************************************************************************************
	// Properties
	//****************************************************************************************
	private Integer				localPort;
	private Integer				remotePort;
	private String				host;
	private String				tunnelHost;
	private String				tunnelUser;
	private String				tunnelPassword;
	private TunnelType			type;
}
