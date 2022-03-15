package pt.up.fc.dcc.mooshak.shared.commands;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum AttributeType implements IsSerializable { 
	
	TEXT, 
	PASSWORD,
	LONG_TEXT,
	INTEGER,
	LONG,
	FLOAT,
	DOUBLE,
	DATE, 
	COLOR, 
	MENU,
	LIST,
	FILE, 		// Any non-Mooshak file reference
	PATH,		// a path to persistent object
	LABEL,
	HIDDEN,
	CONTENT,	// a type of folder with multiple occurrences 
	DATA		// a type of folder with a single occurrence
};