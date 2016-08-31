package com.michielvanderlee.tools.tunnels.ui;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.michielvanderlee.tools.tunnels.TunnelService;

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
    	try
		{
			TunnelService.loadFromFile( new File( "tunnels.json" ) );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
