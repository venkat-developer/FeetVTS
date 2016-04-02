/*package com.i10n.fleet.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

*//**
 * Provides the unique ID for DB insertion
 * 
 * @author Dharmaraju V
 *
 *//*
public class IDGenerator {
	
	private static Logger LOG = Logger.getLogger(IDGenerator.class);

	private static Long lastGeneratedId;
	private static IDGenerator _instance = null;

	public static void initialize(){
		if(_instance == null){
			_instance = new IDGenerator();
			lastGeneratedId = new Long(0);
			loadID ();
		}
	}

	public static IDGenerator getInstance () {			
		if (_instance == null) {
			_instance = new IDGenerator ();
		}		
		return _instance;
	}


	private static void loadID () {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
			Statement statement = dbConnection.createStatement();

			StringBuffer sqlQuery = new StringBuffer();

			sqlQuery.append("select max (id) as max_id from trackhistory");			
			ResultSet rs=statement.executeQuery(sqlQuery.toString());
			while(rs.next()){	
				if (lastGeneratedId.longValue() < rs.getLong("max_id")) {
					lastGeneratedId = new Long( rs.getLong("max_id") );					
				}
			}
			sqlQuery = new StringBuffer();
			sqlQuery.append("select max (id) as max_id from idlepoints");	
			rs=statement.executeQuery(sqlQuery.toString());
			while(rs.next()){
				if (lastGeneratedId.longValue() < rs.getLong("max_id")) {
					lastGeneratedId = new Long( rs.getLong("max_id") );					
				}
			}
			sqlQuery = new StringBuffer();
			sqlQuery.append("select max (tripid) as max_id from livevehiclestatus");			
			rs=statement.executeQuery(sqlQuery.toString());
			while(rs.next()){	
				if (lastGeneratedId.longValue() < rs.getLong("max_id")) {
					lastGeneratedId = new Long( rs.getLong("max_id") );					
				}
			}
			
		} catch (SQLException e) {
			LOG.error("Error while generating ID",e);
		} finally{
			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
		}
	}

	public long getNewId () {
		synchronized (lastGeneratedId) {
			lastGeneratedId++;
			return lastGeneratedId.longValue();
		}
	}
}
*/