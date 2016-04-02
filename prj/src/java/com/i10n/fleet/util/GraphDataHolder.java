package com.i10n.fleet.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Place holder for fuel data for interactive graph
 * @author vishnu
 *
 */
public class GraphDataHolder {
	float value;
	String date;
	final DateFormat formatter = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss,0");

	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public GraphDataHolder(float value) {
		super();
		this.value = value;
		this.date = formatter.format(new Date());
	}
	
	public GraphDataHolder(float value, Date dateObj) {
		super();
		this.value = value;
		this.date = formatter.format(dateObj);
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(value);
		buffer.append(":");
		buffer.append(date);
		return buffer.toString();
	}
};