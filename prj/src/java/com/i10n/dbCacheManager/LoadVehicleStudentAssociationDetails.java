//package com.i10n.dbCacheManager;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Vector;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.log4j.Logger;
//
//import com.i10n.fleet.util.DBConnectionUtils;
//
///**
// * @author Dharmaraju V
// *
// */
//public class LoadVehicleStudentAssociationDetails {
//	private static final Logger LOG = Logger.getLogger(LoadVehicleStudentAssociationDetails.class);
//	
//	static private LoadVehicleStudentAssociationDetails _instance = null;
//	private LoadVehicleStudentAssociationDetails(){};
//	
//	public static LoadVehicleStudentAssociationDetails getInstance() {
//		if(null == _instance){
//			_instance = new LoadVehicleStudentAssociationDetails();
//			_instance.loadVehiclesToStudentAssociationDetails();
//		}
//		return _instance;
//	}
//	
//	public ConcurrentHashMap<Long,Vector<Long>> cacheVehiclesToStudentAssociation = new ConcurrentHashMap<Long, Vector<Long>>();
//	
//	private void loadVehiclesToStudentAssociationDetails (){
//		Connection dbConnection = null;
//		try {
//			LOG.debug("Caching VehiclesToStudentAssociation details ...");
//			
//			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
//			Statement statement = dbConnection.createStatement();
//			Vector<Long> studentAssociation = null;
//			String sqlQueryForVehiclesToStudentAssociationData = null;
//			
//			/* 
//			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
//			 * Database again and again.. 
//			 */
//			
//			sqlQueryForVehiclesToStudentAssociationData = "select * from vehiclestostudent order by vehicleid desc";
//			
//			ResultSet rs=statement.executeQuery(sqlQueryForVehiclesToStudentAssociationData);
//			
//			while(rs.next()){
//				Long vehicleId = rs.getLong("vehicleid");
//				if(cacheVehiclesToStudentAssociation.containsKey(vehicleId)){
//					studentAssociation = cacheVehiclesToStudentAssociation.get(vehicleId);
//					studentAssociation.add(rs.getLong("studentid"));
//					cacheVehiclesToStudentAssociation.put(vehicleId, studentAssociation);
//				}else{
//					studentAssociation = new Vector<Long>();
//					studentAssociation.add(rs.getLong("studentid"));
//					cacheVehiclesToStudentAssociation.put(vehicleId, studentAssociation);
//				}
//			}
//			LOG.debug("Caching VehiclesToStudentAssociation details Successful...");
//		} catch (SQLException e) {
//			LOG.fatal("Problem while caching VehiclesToStudentAssociation details.",e);
//		}
//		finally {
//			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
//		}
//	}
//	
//	public void getDetailsForNewlyAddedVehicle (Long vehicleId){
//		Connection dbConnection = null;
//		try {
//			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
//			Statement statement = dbConnection.createStatement();
//			Vector<Long> studentAssociation = null;
//			String sqlQueryForVehiclesToStudentAssociationData = null;
//			/* 
//			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
//			 * Database again and again.. 
//			 */
//			
//			sqlQueryForVehiclesToStudentAssociationData = "select * from vehiclestostudent where vehicleid = "+vehicleId;
//			
//			ResultSet rs=statement.executeQuery(sqlQueryForVehiclesToStudentAssociationData);
//			
//			while(rs.next()){
//				if(cacheVehiclesToStudentAssociation.containsKey(vehicleId)){
//					studentAssociation = cacheVehiclesToStudentAssociation.get(vehicleId);
//					studentAssociation.add(rs.getLong("studentid"));
//					cacheVehiclesToStudentAssociation.put(vehicleId, studentAssociation);
//				}else{
//					studentAssociation = new Vector<Long>();
//					studentAssociation.add(rs.getLong("studentid"));
//					cacheVehiclesToStudentAssociation.put(vehicleId, studentAssociation);
//				}
//			}
//		
//		} catch (SQLException e) {
//			LOG.error(e);
//		} finally {
//			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
//		}
//	}
//}
