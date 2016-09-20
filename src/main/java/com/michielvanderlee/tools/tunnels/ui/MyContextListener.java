package com.michielvanderlee.tools.tunnels.ui;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener ( "Context listener for doing something or other." )
public class MyContextListener implements ServletContextListener
{
	//****************************************************************************************
	// Constructors
	//****************************************************************************************

	//****************************************************************************************
	// Methods
	//****************************************************************************************
	// Vaadin app deploying/launching.
    @Override
    public void contextInitialized ( ServletContextEvent contextEvent )
    {
    
    }

    // Vaadin app un-deploying/shutting down.
    @Override
    public void contextDestroyed ( ServletContextEvent contextEvent )
    {
    	
    }
    
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************

	//****************************************************************************************
	// Properties
	//****************************************************************************************
}
