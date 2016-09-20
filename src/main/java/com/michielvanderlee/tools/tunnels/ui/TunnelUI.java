package com.michielvanderlee.tools.tunnels.ui;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.annotation.WebServlet;

import com.michielvanderlee.tools.tunnels.CacheUpdateListener;
import com.michielvanderlee.tools.tunnels.LightFormatter;
import com.michielvanderlee.tools.tunnels.Tunnel;
import com.michielvanderlee.tools.tunnels.Tunnel.TunnelField;
import com.michielvanderlee.tools.tunnels.TunnelService;
import com.michielvanderlee.vaadin.windows.ModalWindow;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ImageRenderer;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme( "tunnelui" )
@Widgetset( "com.michielvanderlee.tools.tunnels.ui.TunnelUIWidgetset" )
public class TunnelUI extends UI
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public TunnelUI()
	{
		logArea = new TextArea();
		logArea.setWidth( 670, Unit.PIXELS );
		logArea.setReadOnly( true );
		logArea.setImmediate( true );
		logArea.setWordwrap( false );
		logArea.setBuffered( true );

		Logger logger = Logger.getLogger( "" );

		logger.addHandler( new Handler() {
			{
				setLevel( Level.INFO );
				setFormatter( new LightFormatter() );
			}

			@Override
			public void publish( LogRecord record )
			{
				if( !isLoggable( record ) )
				{
					return;
				}
				String msg = getFormatter().format( record );

				logArea.setReadOnly( false );
				logArea.setValue( logArea.getValue() + msg );
				logArea.setReadOnly( true );
				logArea.commit();
			}

			@Override
			public void flush()
			{
				// No action needed
			}

			@Override
			public void close() throws SecurityException
			{
				// No action needed
			}
		} );
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	@Override
	protected void init( VaadinRequest vaadinRequest )
	{
		setPollInterval( 1000 );

		final VerticalLayout vl = new VerticalLayout();

		actionBar = new ActionBar();
		actionBar.init();
		actionBar.addNewClickListener( new OnNewClickListener() );
		actionBar.addEditClickListener( new OnEditClickListener() );
		actionBar.addDeleteClickListener( new OnDeleteClickListener() );
		actionBar.addEnableClickListener( new OnEnableClickListener() );
		actionBar.addDisableClickListener( new OnDisableClickListener() );

		grid = buildGrid();
		grid.addSelectionListener( new GridSelectionListener() );

		vl.addComponent( actionBar );
		vl.addComponent( grid );
		vl.addComponent( logArea );

		setContent( vl );

		addPollListener( new PollListener() {

			@Override
			public void poll( PollEvent event )
			{
				// Force refresh of grid
				grid.sort( "connected", SortDirection.DESCENDING );
			}
		} );
	}

	private Set<Tunnel> getSelectedTunnels()
	{
		Set<Tunnel> selectedTunnels = new HashSet<Tunnel>();
		for( Object selected : grid.getSelectedRows() )
		{
			assert selected instanceof Tunnel;
			selectedTunnels.add( (Tunnel) selected );
		}

		return selectedTunnels;
	}

	private Grid buildGrid()
	{
		Grid grid = new Grid( getTunnelContainer() );
		grid.setImmediate( true );
		grid.setWidth( 670, Unit.PIXELS );
		grid.getColumn( TunnelField.ID.getName() )
				.setHidden( true );
		grid.getColumn( TunnelField.LOCAL_PORT.getName() )
				.setConverter( new NoGroupingIntegerConverter() );
		grid.getColumn( TunnelField.REMOTE_PORT.getName() )
				.setConverter( new NoGroupingIntegerConverter() );
		grid.getColumn( TunnelField.HOST.getName() )
				.setWidth( 165 );
		grid.getColumn( TunnelField.TUNNEL_HOST.getName() )
				.setWidth( 165 );
		grid.getColumn( TunnelField.TUNNEL_USER.getName() )
				.setHidden( true );
		grid.getColumn( TunnelField.TUNNEL_PASSWORD.getName() )
				.setHidden( true );
		grid.getColumn( TunnelField.TUNNEL_TYPE.getName() )
				.setHidden( true );
		grid.getColumn( TunnelField.SESSION.getName() )
				.setHidden( true );
		grid.getColumn( TunnelField.CONNECTED.getName() )
				.setRenderer( new ImageRenderer(), new ConnectedConverter() )
				.setHeaderCaption( "" );
		grid.setCellStyleGenerator( new CellStyleGenerator() {
			@Override
			public String getStyle( CellReference cell )
			{
				if( cell.getPropertyId().equals( TunnelField.CONNECTED.getName() ) )
				{
					return "icon";
				}
				return null;
			}
		} );

		grid.setColumnOrder( TunnelField.CONNECTED.getName(), TunnelField.LOCAL_PORT.getName(), TunnelField.HOST.getName(), TunnelField.REMOTE_PORT.getName(), TunnelField.TUNNEL_HOST.getName() );
		grid.setSelectionMode( SelectionMode.MULTI );
		grid.sort( TunnelField.CONNECTED.getName(), SortDirection.DESCENDING );

		return grid;
	}

	private BeanItemContainer<Tunnel> getTunnelContainer()
	{
		BeanItemContainer<Tunnel> container = new BeanItemContainer<Tunnel>( Tunnel.class );
		TunnelService service = TunnelService.getInstance();
		container.addAll( service.getCache().getAll() );

		service.getCache().addListener( new CacheUpdateListener<Tunnel>() {

			@Override
			public void removedFromCache( Tunnel object )
			{
				container.removeItem( object );
			}

			@Override
			public void addedToCache( Tunnel object )
			{
				container.addItem( object );
			}
		} );

		return container;
	}

	private static Logger getLogger()
	{
		return Logger.getLogger( TunnelUI.class.getName() );
	}

	@WebServlet( urlPatterns = "/*", name = "TunnelUIServlet", asyncSupported = true )
	@VaadinServletConfiguration( ui = TunnelUI.class, productionMode = false )
	public static class TunnelUIServlet extends VaadinServlet
	{

	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	// @formatter:off
	
	// @formatter:on
	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private ActionBar	actionBar;
	private Grid		grid;
	private TextArea	logArea;

	// ****************************************************************************************
	// Embedded classes
	// ****************************************************************************************
	private class ConnectedConverter implements Converter<Resource, Boolean>
	{
		@Override
		public Boolean convertToModel( Resource value, Class<? extends Boolean> targetType, Locale locale ) throws com.vaadin.data.util.converter.Converter.ConversionException
		{
			return false;
		}

		@Override
		public Resource convertToPresentation( Boolean value, Class<? extends Resource> targetType, Locale locale ) throws com.vaadin.data.util.converter.Converter.ConversionException
		{
			if( value )
			{
				return new ThemeResource( "img/circle_green.png" );
			}
			else
			{
				return new ThemeResource( "img/circle_red.png" );
			}
		}

		@Override
		public Class<Boolean> getModelType()
		{
			return Boolean.class;
		}

		@Override
		public Class<Resource> getPresentationType()
		{
			return Resource.class;
		}
	}

	private class GridSelectionListener implements SelectionListener
	{
		@Override
		public void select( SelectionEvent event )
		{
			Set<Tunnel> selectedTunnels = new HashSet<Tunnel>();
			for( Object selected : event.getSelected() )
			{
				assert selected instanceof Tunnel;
				selectedTunnels.add( (Tunnel) selected );
			}

			if( selectedTunnels.size() == 0 )
			{
				deselectAll();
			}
			else if( selectedTunnels.size() == 1 )
			{
				selectSingle( selectedTunnels );
			}
			else
			{
				selectMultiple( selectedTunnels );
			}
		}

		private void deselectAll()
		{
			actionBar.enableEditButton( false );
			actionBar.enableDeleteButton( false );
			actionBar.enableEnableButton( false );
			actionBar.enableDisableButton( false );
		}

		private void selectSingle( Set<Tunnel> tunnels )
		{
			Tunnel tunnel = tunnels.iterator().next();
			actionBar.enableEditButton( true );
			actionBar.enableDeleteButton( true );
			actionBar.enableEnableButton( !tunnel.isConnected() );
			actionBar.enableDisableButton( tunnel.isConnected() );
		}

		private void selectMultiple( Set<Tunnel> tunnels )
		{
			actionBar.enableEditButton( false );
			actionBar.enableDeleteButton( true );

			boolean allEnabled = true;
			boolean allDisabled = true;
			for( Tunnel tunnel : tunnels )
			{
				if( tunnel.isConnected() )
				{
					allDisabled = false;
				}
				else
				{
					allEnabled = false;
				}

				if( !(allEnabled && allDisabled) )
				{
					break;
				}
			}

			actionBar.enableEnableButton( !allEnabled );
			actionBar.enableDisableButton( !allDisabled );
		}
	}

	private class OnNewClickListener implements ClickListener
	{
		@Override
		public void buttonClick( ClickEvent event )
		{
			ModalWindow tunnelWindow = new ModalWindow( "New Tunnel" );

			TunnelForm tunnelForm = new TunnelForm();
			tunnelForm.init();

			tunnelWindow.setContent( tunnelForm );
			tunnelWindow.center();

			tunnelWindow.addOkClickListener( new ClickListener() {
				@Override
				public void buttonClick( ClickEvent event )
				{
					TunnelService.getInstance().addTunnel( tunnelForm.getTunnel() );
					getLogger().log( Level.FINE, "New" );
				}
			} );
			UI.getCurrent().addWindow( tunnelWindow );
		}
	}

	private class OnEditClickListener implements ClickListener
	{
		@Override
		public void buttonClick( ClickEvent event )
		{
			Set<Tunnel> selectedTunnels = getSelectedTunnels();
			if( selectedTunnels.size() == 0 || selectedTunnels.size() > 1 )
			{
				getLogger().log( Level.WARNING, "Can't edit " + selectedTunnels.size() + " items." );
				return;
			}

			Tunnel selectedTunnel = selectedTunnels.iterator().next();
			ModalWindow tunnelWindow = new ModalWindow( "Edit Tunnel" );

			TunnelForm tunnelForm = new TunnelForm();
			tunnelForm.init();
			tunnelForm.populateFormWithItem( selectedTunnel );

			tunnelWindow.setContent( tunnelForm );
			tunnelWindow.center();

			tunnelWindow.addOkClickListener( new ClickListener() {
				@Override
				public void buttonClick( ClickEvent event )
				{
					TunnelService.getInstance().removeTunnel( selectedTunnel.getId() );
					TunnelService.getInstance().addTunnel( tunnelForm.getTunnel() );
					getLogger().log( Level.FINE, "Edit" );
				}
			} );
			UI.getCurrent().addWindow( tunnelWindow );
		}
	}

	private class OnDeleteClickListener implements ClickListener
	{
		@Override
		public void buttonClick( ClickEvent event )
		{
			Set<Tunnel> selectedTunnels = getSelectedTunnels();
			if( selectedTunnels.size() == 0 )
			{
				getLogger().log( Level.WARNING, "Nothing to delete." );
				return;
			}

			for( Tunnel tunnel : selectedTunnels )
			{
				TunnelService.getInstance().removeTunnel( tunnel.getId() );
			}
		}
	}

	private class OnEnableClickListener implements ClickListener
	{
		@Override
		public void buttonClick( ClickEvent event )
		{
			Set<Tunnel> selectedTunnels = getSelectedTunnels();
			if( selectedTunnels.size() == 0 )
			{
				getLogger().log( Level.WARNING, "Nothing to enable." );
				return;
			}

			for( Tunnel tunnel : selectedTunnels )
			{
				TunnelService.getInstance().startTunnel( tunnel.getId() );
			}
		}
	}

	private class OnDisableClickListener implements ClickListener
	{
		@Override
		public void buttonClick( ClickEvent event )
		{
			Set<Tunnel> selectedTunnels = getSelectedTunnels();
			if( selectedTunnels.size() == 0 )
			{
				getLogger().log( Level.WARNING, "Nothing to disable." );
				return;
			}

			for( Tunnel tunnel : selectedTunnels )
			{
				TunnelService.getInstance().stopTunnel( tunnel.getId() );
			}
		}
	}
}
