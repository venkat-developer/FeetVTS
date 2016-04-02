/*package com.i10n.dbCacheManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.i10n.fleet.util.DBConnectionUtils;

*//**
 * Loads all the proxy server details for address fetching
 *
 *//*
public class LoadProxyDetails {
	
	*//**
	 * Table needs to be created
	 * 
	 * create table proxies(server varchar(30), port integer);
	 * 
	 * 
	 *//*
	
	private static final long REFRESH_DURATION_MS = 5L*60L*1000L;
	
	*//**
	 * Class to store proxy details
	 *//*
	public class ProxyDetails {
		String server;
		int port;
		public String getServer() {
			return server;
		}
		public void setServer(String server) {
			this.server = server;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public ProxyDetails(String server, int port) {
			super();
			this.server = server;
			this.port = port;
		}
	}
	
	private static final Logger LOG = Logger.getLogger(LoadProxyDetails.class);
	static private LoadProxyDetails _instance = null;
	
	*//**
	 * Constructor populates data
	 *//*
	private LoadProxyDetails(){
		loadProxyData();
		TimerTask refreshData = new TimerTask() {			
			@Override
			public void run() {
				LOG.debug("Timertask refreshing proxy list at "+new Date());
				loadProxyData();
				lastReturned = 0;
			}
		};
		Timer timer = new Timer();
	    timer.scheduleAtFixedRate(refreshData, REFRESH_DURATION_MS, REFRESH_DURATION_MS);
	};

	public static LoadProxyDetails getInstance() {
		if(null == _instance){
			_instance = new LoadProxyDetails();
		}
		return _instance;
	}
	
	// Helps in achieving round robin fashion
	private int lastReturned = 0;

	*//**
	 * Retrieves queue
	 * @param routeId
	 * @return
	 *//*
	public ProxyDetails getProxyServer(){
		lastReturned = (lastReturned+1)%proxyServers.size();
		return proxyServers.get(lastReturned);
	}
	
	
	*//**	Mapping routeId with its entity*//*
	public CopyOnWriteArrayList<ProxyDetails> proxyServers = new CopyOnWriteArrayList<ProxyDetails> ();

	*//**
	 * Fetch data
	 *//*
	private void loadProxyData (){
		Connection dbConnection = null;
		try {
			LOG.debug("Caching Proxy Details ..... ");
			
			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
			Statement statement = dbConnection.createStatement();

			String sqlQueryForRouteData = null;

			 
			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
			 * Database again and again.. 
			 

			sqlQueryForRouteData = "select server, port from proxies";

			ResultSet rs=statement.executeQuery(sqlQueryForRouteData);

			while(rs.next()){
				String server = rs.getString("server");
				int port = rs.getInt("port");
				proxyServers.add(new ProxyDetails(server, port));
			}
			LOG.debug("Caching Proxy Details Successful..... ");
		} catch (SQLException e) {
			LOG.fatal("Problem while caching Routes ",e);
		} finally {
			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
		}
	}
}*/