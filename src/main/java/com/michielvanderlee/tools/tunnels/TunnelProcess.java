package com.michielvanderlee.tools.tunnels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.SystemUtils;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.VaadinSession;

import de.flapdoodle.embed.process.runtime.Processes;

public class TunnelProcess
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	public TunnelProcess()
	{
		pid = 0L;
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	public static TunnelProcess fromPid( long pid ) throws ExecuteException, IOException
	{
		TunnelProcess process = new TunnelProcess();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler( outputStream );
		DefaultExecutor exec = new DefaultExecutor();
		
		String cmd = getFromPidCmd( pid );
		
		CommandLine commandline = CommandLine.parse( cmd );
		exec.setStreamHandler( streamHandler );
		exec.execute( commandline );

		String output = outputStream.toString();
		if( !output.contains( "No Instance(s) Available." ) )
		{
			process.pid = pid;
		}

		return process;
	}

	public DefaultExecuteResultHandler execute( Tunnel tunnel ) throws ExecuteException, IOException
	{
		CommandLine cmd = CommandLine.parse( toCmd( tunnel ) );
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler() {
			@Override
			public void onProcessFailed( final ExecuteException e )
			{
				super.onProcessFailed( e );

				String output = outputStream.toString();
				TunnelService.setErrorMsg( new Exception( output ) );
				getLogger().warning( "Could not open a tunnel: " + output );
				
				pid = 0L;
			}
		};
		final ExecuteWatchdog watchdog = new ExecuteWatchdog( ExecuteWatchdog.INFINITE_TIMEOUT );
		final PumpStreamHandler streamHandler = new PumpStreamHandler( outputStream );

		executor = new DefaultExecutor();
		executor.setWatchdog( watchdog );
		executor.setStreamHandler( streamHandler );
		executor.execute( cmd, resultHandler );

		while( !watchdog.isWatching() )
		{
			try
			{
				Thread.sleep( 10 );
			}
			catch( InterruptedException e )
			{
			}
		}
		pid = findPid();

		return resultHandler;
	}

	public void stop()
	{
		if( executor == null )
		{
			stopFromPid();
		}
		else
		{
			executor.getWatchdog().destroyProcess();
		}
		pid = 0L;
	}

	public String toCmd( Tunnel tunnel )
	{
		return new StringBuilder()
				.append( "ssh -L " )
				.append( tunnel.getLocalPort() )
				.append( ':' )
				.append( tunnel.getHost() )
				.append( ':' )
				.append( tunnel.getHostPort() )
				.append( ' ' )
				.append( tunnel.getTunnelUser() )
				.append( '@' )
				.append( tunnel.getTunnelHost() )
				.append( " -N" )
				.append( " -o \"ExitOnForwardFailure yes\"" )
				.toString();
	}

	private Long findPid()
	{
		if( executor != null && executor.getWatchdog() != null )
		{
			try
			{
				ExecuteWatchdog watchdog = executor.getWatchdog();
				Field processField = watchdog.getClass().getDeclaredField( "process" );
				processField.setAccessible( true );
				Process process = (Process) processField.get( watchdog );
				
				if( watchdog.isWatching())
				{
					return Processes.processId( process );
				}
				else
				{
					return 0L;
				}
			}
			catch( Exception e )
			{
				TunnelService.setErrorMsg( e );
				return 0L;
			}
		}

		return 0L;
	}

	private void stopFromPid()
	{
		if( pid == 0L )
		{
			return;
		}

		try
		{
			CommandLine commandline = CommandLine.parse( getStopPidCmd( pid ) );
			DefaultExecutor exec = new DefaultExecutor();
			exec.setExitValues( new int[] { 0, 1 } );
			exec.execute( commandline );
		}
		catch( Exception e )
		{
			TunnelService.setErrorMsg( e );
		}
	}

	@Override
	public String toString()
	{
		return String.valueOf( pid );
	}

	private static String getFromPidCmd( long pid )
	{
		if( SystemUtils.IS_OS_WINDOWS )
		{
			return "wmic " +
					"path win32_process " +
					"where Processid=" + pid +
					" get Commandline";
		}
		else if( SystemUtils.IS_OS_UNIX )
		{
			return "ps -o cmd= -p " + pid;
		}
		else
		{
			throw new RuntimeException( "OS no recognized. Can only run on Windows or Unix" );
		}
	}

	private static String getStopPidCmd( long pid )
	{
		if( SystemUtils.IS_OS_WINDOWS )
		{
			return "tskill " + pid;
		}
		else if( SystemUtils.IS_OS_UNIX )
		{
			return "kill " + pid;
		}
		else
		{
			throw new RuntimeException( "OS no recognized. Can only run on Windows or Unix" );
		}
	}
	
	private static Logger getLogger() {
        return Logger.getLogger(TunnelProcess.class.getName());
    }
	
	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	public Long getPid()
	{
		return pid;
	}

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private Executor	executor;
	private Long		pid;

}
