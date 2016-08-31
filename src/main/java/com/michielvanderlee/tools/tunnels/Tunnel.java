package com.michielvanderlee.tools.tunnels;

import java.io.Serializable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.gson.annotations.JsonAdapter;

public class Tunnel implements Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public Tunnel( Integer localPort, Integer hostPort, String host, String tunnelHost, String tunnelUser )
	{
		super();
		this.localPort = localPort;
		this.hostPort = hostPort;
		this.host = host;
		this.tunnelHost = tunnelHost;
		this.tunnelUser = tunnelUser;
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	public static Tunnel fromCmd( String cmd ) throws ParseException
	{
		String[] args = cmd.substring( 4 ).split( " " );

		Options options = new Options();
		options.addOption( "L", true, "" );

		CommandLineParser parser = new DefaultParser();
		CommandLine cmdl = parser.parse( options, args );

		// Parse port:host:hostport
		String[] args1 = cmdl.getOptionValue( 'L' ).split( "L" );
		// Parse user@host
		String[] args2 = cmdl.getArgs()[0].split( "@" );

		Tunnel tunnel = new Tunnel( Integer.valueOf( args1[0] ), Integer.valueOf( args1[2] ), args1[1], args2[1], args2[0] );
		return tunnel;
	}

	@Override
	public String toString()
	{
		return new StringBuilder()
				.append( localPort )
				.append( ':' )
				.append( host )
				.append( ':' )
				.append( hostPort )
				.append( ' ' )
				.append( tunnelUser )
				.append( '@' )
				.append( tunnelHost )
				.toString();
	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	public Integer getLocalPort()
	{
		return localPort;
	}

	public void setLocalPort( Integer localPort )
	{
		this.localPort = localPort;
	}

	public Integer getHostPort()
	{
		return hostPort;
	}

	public void setHostPort( Integer hostPort )
	{
		this.hostPort = hostPort;
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

	public TunnelProcess getProcess()
	{
		return process;
	}

	public void setProcess( TunnelProcess process )
	{
		this.process = process;
	}

	public boolean isEnabled()
	{
		return process.getPid() != 0;
	}
	
	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private Integer				localPort;
	private Integer				hostPort;
	private String				host;
	private String				tunnelHost;
	private String				tunnelUser;

	@JsonAdapter( TunnelProcessAdapter.class )
	private TunnelProcess		process;

}
