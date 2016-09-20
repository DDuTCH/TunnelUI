package com.michielvanderlee.vaadin.windows;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class ModalWindow extends Window
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public ModalWindow()
	{
		this( "" );
	}

	public ModalWindow( String caption )
	{
		this( caption, false, true, true );
	}

	public ModalWindow( String caption, Component content )
	{
		this( caption );
		setContent( content );
	}

	public ModalWindow( String caption, boolean resizable, boolean draggable, boolean closable )
	{
		super( caption );
		setWidthUndefined();
		setModal( true );
		setResizable( resizable );
		setDraggable( draggable );
		setClosable( closable );
		
		setStyleName( "modalwindow" );
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	@Override
	public void setContent( Component content )
	{
		VerticalLayout vlayout = new VerticalLayout();
		vlayout.setWidthUndefined();

		if( content != null )
		{
			vlayout.addComponent( content );
		}
		vlayout.addComponent( createActionBar() );

		super.setContent( vlayout );
	}

	public HorizontalLayout createActionBar()
	{
		if( btnOk == null || btnCancel == null )
		{
			initActionButtons();
		}
		
		HorizontalLayout hlayout = new HorizontalLayout();
		hlayout.setStyleName( "actionbar" );
		
		hlayout.addComponent( btnOk );
		hlayout.addComponent( btnCancel );

		return hlayout;
	}
	
	public void addOkClickListener( ClickListener listener )
	{
		btnOk.addClickListener( listener );
	}
	
	private void initActionButtons()
	{
		btnOk = new Button( "OK" );
		btnOk.setStyleName( ValoTheme.BUTTON_PRIMARY );
		btnOk.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event )
			{
				close();
			}
		} );
		
		btnCancel = new Button( "Cancel" );
		btnCancel.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event )
			{
				close();
			}
		} );
	}
	
	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private Button btnOk;
	private Button btnCancel;
}
