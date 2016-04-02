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
//import com.i10n.db.entity.Students;
//import com.i10n.db.entity.primarykey.LongPrimaryKey;
//import com.i10n.fleet.util.DBConnectionUtils;
//
///**
// * @author Dharmaraju V
// *
// */
//public class LoadStudentsDetails {
//	private static final Logger LOG = Logger.getLogger(LoadStudentsDetails.class);
//	
//	static private LoadStudentsDetails _instance = null;
//	private LoadStudentsDetails(){};
//	
//	public static LoadStudentsDetails getInstance() {
//		if(null == _instance){
//			_instance = new LoadStudentsDetails();
//			_instance.loadStudentsDetails();
//		}
//		return _instance;
//	}
//	
//	
//	
//	private Students students = null;
//	
//	public ConcurrentHashMap<Long, Students> cacheStudents = new ConcurrentHashMap<Long, Students>();
//	
//	private void loadStudentsDetails (){
//		Connection dbConnection = null;
//		try {
//			LOG.debug("Caching Student details ...");
//			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
//			Statement statement = dbConnection.createStatement();
//			
//			String sqlQueryForStudentsData = null;
//			
//			/* 
//			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
//			 * Database again and again.. 
//			 */
//			
//			sqlQueryForStudentsData = "select * from students order by id desc";
//			
//			ResultSet rs=statement.executeQuery(sqlQueryForStudentsData);
//			
//			while(rs.next()){
//				students = new Students();
//				students.setId(new LongPrimaryKey(rs.getLong("id")));
//				students.setStudentName(rs.getString("studentname")); 
//				students.setStopId(rs.getLong("stopid"));
//				students.setRouteId(rs.getLong("routeid")); 
//				students.setMobileNo(rs.getLong("mobileno"));
//				students.setAlertMinutes(rs.getInt("alertmin"));
//				students.setOwnerId(rs.getLong("ownerid"));
//				
//				cacheStudents.put(rs.getLong("id"), students);
//			}
//			LOG.debug("Caching Student details Successful...");
//		} catch (SQLException e) {
//			LOG.fatal("Caching Student details.",e);
//		} finally {
//			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
//		}
//	}
//	
//	public void getDetailsForNewlyAddedStudent(Long studentId){
//		Connection dbConnection = null;
//		try {
//			dbConnection = DBConnectionUtils.getConnectionFromPool(LOG);
//			Statement statement = dbConnection.createStatement();
//			
//			String sqlQueryForStudentsData = null;
//			
//			/* 
//			 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
//			 * Database again and again.. 
//			 */
//			
//			sqlQueryForStudentsData = "select * from students where id ="+studentId;
//			
//			ResultSet rs=statement.executeQuery(sqlQueryForStudentsData);
//			
//			while(rs.next()){
//				students = new Students();
//				students.setId(new LongPrimaryKey(rs.getLong("id")));
//				students.setStudentName(rs.getString("studentname")); 
//				students.setStopId(rs.getLong("stopid"));
//				students.setRouteId(rs.getLong("routeid")); 
//				students.setMobileNo(rs.getLong("mobileno"));
//				students.setAlertMinutes(rs.getInt("alertmin"));
//				students.setOwnerId(rs.getLong("ownerid"));
//				
//				cacheStudents.put(rs.getLong("id"), students);
//			}
//			
//		} catch (SQLException e) {
//			LOG.error(e);
//		} finally {
//			DBConnectionUtils.returnConnectionToPool(dbConnection, LOG);
//		}
//	}
//	
//
//}
