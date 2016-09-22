package com.michielvanderlee.tools.tunnels.ui;

import com.michielvanderlee.tools.tunnels.Tunnel;
import com.michielvanderlee.tools.tunnels.TunnelBuilder;
import com.michielvanderlee.tools.tunnels.TunnelType;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class TunnelForm extends FormLayout
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public TunnelForm( )
	{
		super();
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	public void init()
	{
		this.setWidthUndefined();

		ObjectProperty<Integer> localPortProp = new ObjectProperty<Integer>( 1 );
		localPort = new TextField( "Local Port:", localPortProp );
		localPort.addValidator( new IntegerRangeValidator( "Must be between 1-66535", 1, 65535 ) );
		localPort.setConverter( new NoGroupingIntegerConverter() );
		localPort.setImmediate( true );

		host = new TextField( "Host:" );

		ObjectProperty<Integer> hostPortProp = new ObjectProperty<Integer>( 1 );
		remotePort = new TextField( "Remote Port:", hostPortProp );
		remotePort.addValidator( new IntegerRangeValidator( "Must be between 1-66535", 1, 65535 ) );
		remotePort.setConverter( new NoGroupingIntegerConverter() );
		remotePort.setImmediate( true );

		tunnelHost = new TextField( "Tunnel Host:" );
		tunnelUser = new TextField( "Tunnel User:" );
		tunnelPassword = new PasswordField( "Tunnel Password:" );
		reverseTunnel = new CheckBox( "Reverse Tunnel:" );
		
		addComponent( localPort );
		addComponent( host );
		addComponent( remotePort );
		addComponent( tunnelHost );
		addComponent( tunnelUser );
		addComponent( tunnelPassword );
		addComponent( reverseTunnel );
	}

	public Tunnel getTunnel()
	{
		return new TunnelBuilder()
				.setLocalPort( Integer.valueOf( localPort.getValue() ) )
				.setHost( host.getValue() )
				.setRemotePort( Integer.valueOf( remotePort.getValue() ) )
				.setTunnelHost( tunnelHost.getValue() )
				.setTunnelUser( tunnelUser.getValue() )
				.setTunnelPassword( tunnelPassword.getValue() )
				.setTunnelType( reverseTunnel.getValue() ? TunnelType.REVERSE : TunnelType.FORWARD )
				.build();
	}
	
	public void populateFormWithItem( Tunnel tunnel )
	{
		localPort.setValue( Integer.toString( tunnel.getLocalPort() ) );
		remotePort.setValue( Integer.toString( tunnel.getRemotePort() ) );
		host.setValue( tunnel.getHost() );
		tunnelHost.setValue( tunnel.getTunnelHost() );
		tunnelUser.setValue( tunnel.getTunnelUser() );
		tunnelPassword.setValue( tunnel.getTunnelPassword() );
		reverseTunnel.setValue( tunnel.getType() == TunnelType.REVERSE );
	}
	
	public void disablePortFields()
	{
		localPort.setEnabled( false );
		remotePort.setEnabled( false );		
	}
	
	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	public TextField getLocalPort()
	{
		return localPort;
	}

	public TextField getRemotePort()
	{
		return remotePort;
	}

	public TextField getHost()
	{
		return host;
	}

	public TextField getTunnelHost()
	{
		return tunnelHost;
	}

	public TextField getTunnelUser()
	{
		return tunnelUser;
	}
	
	public PasswordField getTunnelPassword()
	{
		return tunnelPassword;
	}

	public CheckBox getReverseTunnel()
	{
		return reverseTunnel;
	}
	
	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private TextField		localPort;
	private TextField		remotePort;
	private TextField		host;
	private TextField		tunnelHost;
	private TextField		tunnelUser;
	private PasswordField	tunnelPassword;
	private CheckBox		reverseTunnel;
}
