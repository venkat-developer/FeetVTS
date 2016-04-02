/**
 * 
 */
package com.i10n.db.entity;

import java.util.Date;

import com.googlecode.jsonplugin.annotations.JSON;


/**
 * @author joshua
 *
 */
public class BrowserDate {
	protected Date localTime;
    
	   
	public Date getLocalTime() {
		return localTime;
	}

	 @JSON(format = "yyyy/MM/dd  HH:mm:ss")
	public void setLocalTime(Date localTime) {
		this.localTime = localTime;
	}
}
