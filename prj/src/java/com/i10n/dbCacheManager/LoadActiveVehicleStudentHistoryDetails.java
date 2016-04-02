/*package com.i10n.dbCacheManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.fleet.util.DBConnectionUtils;

*//**
 * @author Dharmaraju V
 *
 *//*
public class LoadActiveVehicleStudentHistoryDetails {
		private static final Logger LOG = Logger.getLogger(LoadActiveVehicleStudentHistoryDetails.class);

	static private LoadActiveVehicleStudentHistoryDetails _instance = null;
	private LoadActiveVehicleStudentHistoryDetails(){};

	public static LoadActiveVehicleStudentHistoryDetails getInstance() {
		if(null == _instance){
			_instance = new LoadActiveVehicleStudentHistoryDetails();
			_instance.loadActiveVehicleStudentHistoryDetails();
		}
		return _instance;
	}


	public ConcurrentHashMap<String, Vector<Long>> cacheActiveVehicleStudentHistories = new ConcurrentHashMap<String, Vector<Long>>();

	private void loadActiveVehicleStudentHistoryDetails (){
		Connection dbConnection = null;
		try {
			LOG.debug("Caching ActiveVehicleStudentHistory details ...");
			
			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
			Statement statement = dbConnection.createStatement();

			String sqlQueryForActiveVehicleStudentHistoryDetailsData = null;
			String activeVehicleRouteAssociation = null;
			Vector<Long>  studentHistoryIdList = null;

			 
			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
			 * Database again and again.. 
			 

			sqlQueryForActiveVehicleStudentHistoryDetailsData = "select * from studenthistory where actualtime is null order by routeid asc,vehicleid desc ";

			ResultSet rs=statement.executeQuery(sqlQueryForActiveVehicleStudentHistoryDetailsData);

			while(rs.next()){
				activeVehicleRouteAssociation = new String();
				activeVehicleRouteAssociation=((rs.getLong("vehicleid"))+"-"+(rs.getLong("routeid"))).replace(" ", "").trim();
				if(cacheActiveVehicleStudentHistories.containsKey(activeVehicleRouteAssociation)){
					studentHistoryIdList = cacheActiveVehicleStudentHistories.get(activeVehicleRouteAssociation);
					studentHistoryIdList.add(rs.getLong("id"));
					cacheActiveVehicleStudentHistories.put(activeVehicleRouteAssociation, studentHistoryIdList);
				}else{
					studentHistoryIdList = new Vector<Long>();
					studentHistoryIdList.add(rs.getLong("id"));
					cacheActiveVehicleStudentHistories.put(activeVehicleRouteAssociation, studentHistoryIdList);
				}
			}
			LOG.debug("Caching ActiveVehicleStudentHistory details Successful...");
		} catch (SQLException e) {
			LOG.fatal("Problem while caching ActiveVehicleStudentHistory details.",e);
		} finally {
			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
		}
	}

	public void getDetailsforNewlyAddedVehicleRouteStudents (Long vehicleId, Long routeId){
		Connection dbConnection = null;
		try {
			
			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
			Statement statement = dbConnection.createStatement();

			String sqlQueryForActiveVehicleStudentHistoryDetailsData = null;
			String activeVehicleRouteAssociation = null;
			Vector<Long>  studentHistoryIdList = new Vector<Long>();

			 
			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
			 * Database again and again.. 
			 

			sqlQueryForActiveVehicleStudentHistoryDetailsData = "select * from studenthistory where actualtime is null order by routeid asc,vehicleid desc ";

			ResultSet rs=statement.executeQuery(sqlQueryForActiveVehicleStudentHistoryDetailsData);

			while(rs.next()){
				activeVehicleRouteAssociation = new String();
				activeVehicleRouteAssociation=((rs.getLong("vehicleid"))+"-"+(rs.getLong("routeid"))).replace(" ", "").trim();
				if(cacheActiveVehicleStudentHistories.containsKey(activeVehicleRouteAssociation)){
					studentHistoryIdList = cacheActiveVehicleStudentHistories.get(activeVehicleRouteAssociation);
					studentHistoryIdList.add(rs.getLong("id"));
					cacheActiveVehicleStudentHistories.put(activeVehicleRouteAssociation, studentHistoryIdList);
				}else{
					studentHistoryIdList = new Vector<Long>();
					studentHistoryIdList.add(rs.getLong("id"));
					cacheActiveVehicleStudentHistories.put(activeVehicleRouteAssociation, studentHistoryIdList);
				}
			}

		} catch (SQLException e) {
			LOG.error(e);
		} finally {
			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
		}
	}
}
*/