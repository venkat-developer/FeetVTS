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
public class DateRange {
	 protected Date startDate;

	    protected Date endDate;

	    @JSON(format = "MM/dd/yyyy  HH:mm:ss")
	    public void setStart(Date start) {
	        startDate = start;
	        
	    }

	    public Date getStart() {
	        return startDate;
	    }

	    @JSON(format = "MM/dd/yyyy  HH:mm:ss")
	    public void setEnd(Date end) {
	        endDate = end;
	    }

	    public Date getEnd() {
	        return endDate;
	    }
	}

