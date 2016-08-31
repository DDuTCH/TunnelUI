package com.michielvanderlee.tools.tunnels;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TunnelProcessAdapter extends TypeAdapter<TunnelProcess>
{
	//****************************************************************************************
	// Constructors
	//****************************************************************************************

	//****************************************************************************************
	// Methods
	//****************************************************************************************
	@Override
	public void write( JsonWriter out, TunnelProcess process ) throws IOException
	{
		out.beginObject();
		
		out.name( "pid" ).value( process.getPid() );
		
		out.endObject();
	}

	@Override
	public TunnelProcess read( JsonReader in ) throws IOException
	{
		TunnelProcess process = new TunnelProcess();
		in.beginObject();
		
		String name = in.nextName();
		if( name.equals( "pid" ) ) {
			process = TunnelProcess.fromPid( in.nextLong() );
		}
		
		in.endObject();
		return process;
	}
	
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************

	//****************************************************************************************
	// Properties
	//****************************************************************************************
}
