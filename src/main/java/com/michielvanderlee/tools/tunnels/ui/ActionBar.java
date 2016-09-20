package com.michielvanderlee.tools.tunnels.ui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

public class ActionBar extends HorizontalLayout
{
	private static final long serialVersionUID = 1L;
	
	//****************************************************************************************
	// Constructors
	//****************************************************************************************
	
	//****************************************************************************************
	// Methods
	//****************************************************************************************
	public void init()
	{
		setWidthUndefined();
		addStyleName( "actionBar" );
		
		btnNew = new Button( "New" );
		btnNew.setStyleName( ValoTheme.BUTTON_PRIMARY );
		
		btnEdit = new Button( "Edit" );
		btnEdit.setEnabled( false );
		
		
		btnDelete = new Button( "Delete" );
		btnDelete.setStyleName( ValoTheme.BUTTON_DANGER );
		btnDelete.setEnabled( false );
		btnDelete.setDisableOnClick( true );
		
		
		btnEnable = new Button( "Enable" );
		btnEnable.setStyleName( ValoTheme.BUTTON_FRIENDLY );
		btnEnable.setEnabled( false );
		btnEnable.setDisableOnClick( true );
		
		
		btnDisable = new Button( "Disable" );
		btnDisable.setStyleName( ValoTheme.BUTTON_DANGER );
		btnDisable.setEnabled( false );
		btnDisable.setDisableOnClick( true );
		
		
		addComponent( btnNew );
		addComponent( btnEdit );
		addComponent( btnDelete );
		addComponent( createSeperator() );
		addComponent( btnEnable );
		addComponent( btnDisable );
	}
	
	
	public void enableEditButton( boolean enabled )
	{
		btnEdit.setEnabled( enabled );
	}
	
	public void enableDeleteButton( boolean enabled )
	{
		btnDelete.setEnabled( enabled );
	}
	
	public void enableEnableButton( boolean enabled )
	{
		btnEnable.setEnabled( enabled );
	}
	
	public void enableDisableButton( boolean enabled )
	{
		btnDisable.setEnabled( enabled );
	}
	
	
	public void addNewClickListener( ClickListener listener )
	{
		btnNew.addClickListener( listener );
	}
	public void removeNewClickListener( ClickListener listener )
	{
		btnNew.removeClickListener( listener );
	}
	
	public void addEditClickListener( ClickListener listener )
	{
		btnEdit.addClickListener( listener );
	}
	public void removeEditClickListener( ClickListener listener )
	{
		btnEdit.removeClickListener( listener );
	}
	
	public void addDeleteClickListener( ClickListener listener )
	{
		btnDelete.addClickListener( listener );
	}
	public void removeDeleteClickListener( ClickListener listener )
	{
		btnDelete.removeClickListener( listener );
	}
	
	public void addEnableClickListener( ClickListener listener )
	{
		btnEnable.addClickListener( listener );
	}
	public void removeEnableClickListener( ClickListener listener )
	{
		btnEnable.removeClickListener( listener );
	}
	
	public void addDisableClickListener( ClickListener listener )
	{
		btnDisable.addClickListener( listener );
	}
	public void removeDisableClickListener( ClickListener listener )
	{
		btnDisable.removeClickListener( listener );
	}
	
	private Label createSeperator()
	{
		Label lbl = new Label("<hr />", ContentMode.HTML);
		
		return lbl;
	}
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************

	//****************************************************************************************
	// Properties
	//****************************************************************************************
	private Button btnNew;
	private Button btnEdit;
	private Button btnDelete;
	
	private Button btnEnable;
	private Button btnDisable;
	
}
