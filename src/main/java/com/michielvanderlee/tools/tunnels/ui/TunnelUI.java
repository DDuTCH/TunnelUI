package com.michielvanderlee.tools.tunnels.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.StringUtils;

import com.michielvanderlee.tools.tunnels.Tunnel;
import com.michielvanderlee.tools.tunnels.TunnelService;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
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

	@Override
	protected void init( VaadinRequest vaadinRequest )
	{
		setPollInterval( 1000 );
		
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		List<ScheduledFuture<?>> futures = new ArrayList<ScheduledFuture<?>>();
		final ObjectProperty<String> errorProperty = new ObjectProperty<String>( "" ) {
			@Override
			public void setValue( String newValue ) throws Property.ReadOnlyException
			{
				super.setValue( newValue );

				if( !StringUtils.isBlank( newValue ) )
				{
					for( ScheduledFuture<?> future : futures)
					{
						future.cancel( false );
					}
					
					ScheduledFuture<?> f = scheduler.schedule( new Runnable()
					{
						@Override
						public void run()
						{
							setValue( "" );
						}
					}, 5, TimeUnit.SECONDS );
					futures.add(f);
				}
			}
		};
		TunnelService.setErrorProperty( errorProperty );

		final VerticalLayout vl = new VerticalLayout();
		final CssLayout cssLayout = new CssLayout();

		final Grid grid = new Grid( TunnelService.getContainer() );
		grid.setImmediate( true );
		grid.setWidth( 610, Unit.PIXELS );
		grid.getColumn( "localPort" )
				.setConverter( new NoGroupingIntegerConverter() );
		grid.getColumn( "hostPort" )
				.setConverter( new NoGroupingIntegerConverter() );
		grid.getColumn( "host" )
				.setWidth( 165 );
		grid.getColumn( "tunnelHost" )
				.setWidth( 165 );
		grid.getColumn( "tunnelUser" )
				.setHidden( true );
		grid.getColumn( "enabled" )
				.setRenderer( new ImageRenderer(), new Converter<Resource, Boolean>() {

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

				} )
				.setHeaderCaption( "" );
		grid.setCellStyleGenerator( new CellStyleGenerator() {
			@Override
			public String getStyle( CellReference cell )
			{
				if( cell.getPropertyId().equals( "enabled" ) )
				{
					return "icon";
				}
				return null;
			}
		} );

		grid.setColumnOrder( "enabled", "localPort", "host", "hostPort", "tunnelHost", "process" );
		grid.addSelectionListener( new SelectionListener() {

			@Override
			public void select( SelectionEvent event )
			{
				Set<Tunnel> selectedTunnels = new HashSet<Tunnel>();
				for( Object selected : event.getSelected() )
				{
					assert selected instanceof Tunnel;
					selectedTunnels.add( (Tunnel) selected );
				}
				tunnelForm.selectItems( selectedTunnels );
			}
		} );
		grid.setSelectionMode( SelectionMode.MULTI );
		grid.sort( "enabled", SortDirection.DESCENDING );
		
		tunnelForm = new TunnelForm( grid );
		tunnelForm.init();

		cssLayout.addComponent( grid );
		cssLayout.addComponent( tunnelForm );

		Label lbl = new Label( );
		lbl.setImmediate( true );
		lbl.setContentMode( ContentMode.PREFORMATTED );
		lbl.setStyleName( "error" );
		
		vl.addComponent( cssLayout );
		vl.addComponent( lbl );

		setContent( vl );
		
		addPollListener( new PollListener() {
			
			@Override
			public void poll( PollEvent event )
			{
				// Force refresh of grid
				grid.sort( "enabled", SortDirection.DESCENDING );
				// Hack to update the label. 
				// Setting the lbl datasource to errorProperty would not update the label properly
				lbl.setValue( errorProperty.getValue() );
			}
		} );
	}

	@WebServlet( urlPatterns = "/*", name = "TunnelUIServlet", asyncSupported = true )
	@VaadinServletConfiguration( ui = TunnelUI.class, productionMode = false )
	public static class TunnelUIServlet extends VaadinServlet
	{

	}

	private TunnelForm tunnelForm;

}
