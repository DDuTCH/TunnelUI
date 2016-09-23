package com.michielvanderlee.tools.tunnels.ui;

import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;

public class TunnelGrid extends Grid
{
	//****************************************************************************************
	// Constructors
	//****************************************************************************************

	//****************************************************************************************
	// Methods
	//****************************************************************************************
	@Override
	public void sort(Object propertyId, SortDirection direction) {
        super.sort( propertyId, direction );
        
        this.sortPropertyId = propertyId;
        this.sortDirection = direction;
    }
	
	public void refreshSort()
	{
		super.sort( sortPropertyId, sortDirection );
	}
	
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************
	// @formatter:off
	public Object getSortPropertyId()
	{
		return sortPropertyId;
	}
	
	public SortDirection getSortDirection()
	{
		return sortDirection;
	}
	
	// @formatter:on
	//****************************************************************************************
	// Properties
	//****************************************************************************************
	
	//****************************************************************************************
	// State
	//****************************************************************************************
	private Object sortPropertyId;
	private SortDirection sortDirection;
	
}
