//package com.i10n.dbCacheManager;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.log4j.Logger;
//
//import com.i10n.db.entity.StudentHistory;
//import com.i10n.db.entity.primarykey.LongPrimaryKey;
//import com.i10n.fleet.util.DBConnectionUtils;
//
///**
// * @author Dharmaraju V
// *
// */
//public class LoadStudentHistoryDetails {
//	private static final Logger LOG = Logger.getLogger(LoadStudentHistoryDetails.class);
//
//	static private LoadStudentHistoryDetails _instance = null;
//	private LoadStudentHistoryDetails(){};
//
//	public static LoadStudentHistoryDetails getInstance() {
//		if(null == _instance){
//			_instance = new LoadStudentHistoryDetails();
//			_instance.loadStudentHistoryDetails();
//		}
//		return _instance;
//	}
//
//
//
//	private StudentHistory studentHistory = null;
//
//	public ConcurrentHashMap<Long, StudentHistory> cacheStudentHistory= new ConcurrentHashMap<Long, StudentHistory>();
//
//	private void loadStudentHistoryDetails (){
//		Connection dbConnection = null;
//		try {
//			LOG.debug("Caching StudentHistory details ...");
//			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
//			Statement statement = dbConnection.createStatement();
//
//			String sqlQueryForStudentHistoryData = null;
//
//			/* 
//			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
//			 * Database again and again.. 
//			 */
//
//			sqlQueryForStudentHistoryData = "select * from studenthistory order by id desc";
//
//			ResultSet rs=statement.executeQuery(sqlQueryForStudentHistoryData);
//			while(rs.next()){
//				studentHistory = new StudentHistory();
//				studentHistory.setId(new LongPrimaryKey(rs.getLong("id")));
//				studentHistory.setStudentId(rs.getLong("studentid"));
//				studentHistory.setStopId(rs.getLong("stopid"));
//				studentHistory.setVehicleId(rs.getLong("vehicleid")); 
//				studentHistory.setRouteId(rs.getLong("routeid"));
//				studentHistory.setAlertMin(rs.getInt("alertmins"));
//				studentHistory.setSmsSentTime(rs.getTimestamp("smssenttime")); 
//				studentHistory.setExpectedTime(rs.getTimestamp("expectedtime")); 
//				studentHistory.setActualTime(rs.getTimestamp("actualtime"));
//				studentHistory.setSMSSent(rs.getBoolean("smssent"));
//
//				cacheStudentHistory.put(rs.getLong("id"),studentHistory); 
//			}
//			LOG.debug("Caching StudentHistory details Successful...");
//		} catch (SQLException e) {
//			LOG.fatal("Problem while caching StudentHistory details.",e);
//		} finally {
//			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
//		}
//	}
//
//	public void getDetailsForNewlyAddedStudentHistory (Long studentHistoryId){
//		Connection dbConnection = null;
//		try {
//			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
//			Statement statement = dbConnection.createStatement();
//
//			String sqlQueryForStudentHistoryData = null;
//
//			/* 
//			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
//			 * Database again and again.. 
//			 */
//
//			sqlQueryForStudentHistoryData = "select * from studenthistory where id = "+studentHistoryId;
//
//			ResultSet rs=statement.executeQuery(sqlQueryForStudentHistoryData);
//			while(rs.next()){
//				studentHistory = new StudentHistory();
//				studentHistory.setId(new LongPrimaryKey(rs.getLong("id")));
//				studentHistory.setStudentId(rs.getLong("studentid"));
//				studentHistory.setStopId(rs.getLong("stopid"));
//				studentHistory.setVehicleId(rs.getLong("vehicleid")); 
//				studentHistory.setRouteId(rs.getLong("routeid"));
//				studentHistory.setAlertMin(rs.getInt("alertmins"));
//				studentHistory.setSmsSentTime(rs.getTimestamp("smssenttime")); 
//				studentHistory.setExpectedTime(rs.getTimestamp("expectedtime")); 
//				studentHistory.setActualTime(rs.getTimestamp("actualtime"));
//				studentHistory.setSMSSent(rs.getBoolean("smssent"));
//
//				cacheStudentHistory.put(rs.getLong("id"),studentHistory); 
//			}
//
//		} catch (SQLException e) {
//			LOG.error(e);
//		} finally {
//			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
//		}
//	}
//}
