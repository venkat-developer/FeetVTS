package com.i10n.fleet.update;

public interface IStreamUpdate {
	
	long getId();
	String getType();
	String toJSONString();
	String[] getLogin();

}
