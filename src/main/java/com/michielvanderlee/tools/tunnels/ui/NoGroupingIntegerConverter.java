package com.michielvanderlee.tools.tunnels.ui;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToIntegerConverter;

public class NoGroupingIntegerConverter extends StringToIntegerConverter
{
	//****************************************************************************************
	// Constructors
	//****************************************************************************************

	//****************************************************************************************
	// Methods
	//****************************************************************************************
	protected NumberFormat getFormat(Locale locale) {
        NumberFormat format = super.getFormat(locale);
        format.setGroupingUsed(false);
        return format;
    };
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************

	//****************************************************************************************
	// Properties
	//****************************************************************************************
}
