package com.michielvanderlee.tools.tunnels.ui;

import java.util.Set;

import com.michielvanderlee.tools.tunnels.Tunnel;
import com.michielvanderlee.tools.tunnels.TunnelService;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class TunnelForm extends FormLayout
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public TunnelForm( Grid grid )
	{
		super();
		this.grid = grid;
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
		hostPort = new TextField( "Host Port:", hostPortProp );
		hostPort.addValidator( new IntegerRangeValidator( "Must be between 1-66535", 1, 65535 ) );
		hostPort.setConverter( new NoGroupingIntegerConverter() );
		hostPort.setImmediate( true );

		tunnelHost = new TextField( "Tunnel Host:" );
		tunnelUser = new TextField( "Tunnel User:" );

		HorizontalLayout hl1 = new HorizontalLayout();
		create = new Button( "Create" );
		create.addClickListener( new ClickListener() {

			@Override
			public void buttonClick( ClickEvent event )
			{
				TunnelService.addTunnel( new Tunnel(
						(Integer) localPort.getConvertedValue(),
						(Integer) hostPort.getConvertedValue(),
						host.getValue(),
						tunnelHost.getValue(),
						tunnelUser.getValue() ) );
			}
		} );
		create.setStyleName( ValoTheme.BUTTON_PRIMARY );

		delete = new Button( "Delete" );
		delete.setEnabled( false );
		delete.setDisableOnClick( true );
		delete.setStyleName( ValoTheme.BUTTON_DANGER );
		delete.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event )
			{
				if( selectedItems != null && selectedItems.size() > 0 )
				{
					for( Tunnel selectedTunnel : selectedItems )
					{
						TunnelService.removeTunnel( selectedTunnel );
					}
				}
				enable.setEnabled( false );
				disable.setEnabled( false );
			}
		} );

		HorizontalLayout hl2 = new HorizontalLayout();
		enable = new Button( "Enable" );
		enable.setEnabled( false );
		enable.setStyleName( ValoTheme.BUTTON_PRIMARY );
		enable.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event )
			{
				if( selectedItems != null && selectedItems.size() > 0 )
				{
					for( Tunnel selectedTunnel : selectedItems )
					{
						TunnelService.startTunnel( selectedTunnel );
					}
					grid.clearSortOrder();
				}
			}
		} );
		
		disable = new Button( "Disable" );
		disable.setEnabled( false );
		disable.setStyleName( ValoTheme.BUTTON_DANGER );
		disable.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event )
			{
				if( selectedItems != null && selectedItems.size() > 0 )
				{
					for( Tunnel selectedTunnel : selectedItems )
					{
						selectedTunnel.getProcess().stop();
					}
					grid.clearSortOrder();
				}
			}
		} );

		
		addComponent( localPort );
		addComponent( host );
		addComponent( hostPort );
		addComponent( tunnelHost );
		addComponent( tunnelUser );

		hl1.addComponent( create );
		hl1.addComponent( delete );
		hl1.setSpacing( true );
		
		hl2.addComponent( enable );
		hl2.addComponent( disable );
		hl2.setSpacing( true );

		addComponent( hl1 );
		addComponent( hl2 );
	}

	public void selectItems( Set<Tunnel> selectedItems )
	{
		this.selectedItems = selectedItems;
		if( selectedItems.size() > 0 )
		{
			populateFormWithItem( selectedItems.iterator().next() );

			delete.setEnabled( true );
			enable.setEnabled( true );
			disable.setEnabled( true );
		}
		else
		{
			delete.setEnabled( false );
			enable.setEnabled( false );
			disable.setEnabled( false );
		}
		
	}

	private void populateFormWithItem( Tunnel tunnel )
	{
		localPort.setValue( Integer.toString( tunnel.getLocalPort() ) );
		hostPort.setValue( Integer.toString( tunnel.getHostPort() ) );
		host.setValue( tunnel.getHost() );
		tunnelHost.setValue( tunnel.getTunnelHost() );
		tunnelUser.setValue( tunnel.getTunnelUser() );
	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	public TextField getLocalPort()
	{
		return localPort;
	}

	public TextField getHostPort()
	{
		return hostPort;
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

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private Grid grid;
	
	private TextField		localPort;
	private TextField		hostPort;
	private TextField		host;
	private TextField		tunnelHost;
	private TextField		tunnelUser;

	private Button			create;
	private Button			delete;

	private Button			enable;
	private Button			disable;
	
	private Set<Tunnel>		selectedItems;
}
