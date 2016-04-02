package com.i10n.db.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.postgis.Geometry;
import org.postgis.Point;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.LiveVehicleStatusDaoImp;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.User;
import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.LiveVehicleStatus.VehicleStatus;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.idao.IDAO;
import com.i10n.db.idao.IUserDAO;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.providers.mock.DashboardDataProvider;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings(value = { "unused", "rawtypes" })
public class TestDBConnection {
	
	//The user DAO Object
	
	private IDAO trackHistoryDao;
	private static int i = 0;
	
//	public TestDBConnection() {
//		//Set the JDBC Template
//		JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
//		//Set the sequence
//		DataFieldMaxValueIncrementer incrementer = new PostgreSQLSequenceMaxValueIncrementer(
//				getDataSource(), "user_id_sequence");
//
//		this.userDao = new UserDaoImp();
//		((UserDaoImp) userDao).setUserIdIncrementer(incrementer);
//		((UserDaoImp) userDao).setJdbcTemplate(jdbcTemplate);
//	}
	
	//Load from Config file
	public TestDBConnection() {
//		ClassPathResource classPathResource = new ClassPathResource("dbConfig.xml");
//		XmlBeanFactory beanFactory = new XmlBeanFactory(classPathResource);
		this.trackHistoryDao = (TrackHistoryDaoImpl)(DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO));		
	}

	/**
	 * Init's Data Source
	 * @return
	 * DriverManagerDataSource
	 */
	private DriverManagerDataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUsername("postgres");
		dataSource.setPassword("i10n.com");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/fleet");
		return dataSource;
	}
	
	/**
	 * Fetches all the Users
	 * @param userDao
	 * The UserDAO Object
	 */
	private void showAllUsers(UserDaoImp userDao){
		List<User> users = userDao.selectAll();
		for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
			User element = (User) iter.next();
			System.out.println("User Details: " + element.toString());
		}
		System.out.println(userDao.authenticateUser("annai", "fe01ce2a7fbac8fafaed7c982a04e229"));
	}
	
	private List<User> getUserListToInsert(List<User> userList){
		for(int i = 0; i< 100; i++){
			//User(Long id, String login, String password, UserRole role,
				//	UserStatus userstatus,Long groupId, Long owner_id,String firstname,String lastname) {
			User newUser = new User(3424242L+i, "Dharma_Test"+i,"i10n", User.UserRole.NORMAL_USER, User.UserStatus.USER_ACTIVE, 1L, 2L, "test_first_"+i, "test_last_"+i, 0, 0);
			userList.add(newUser);
		}
		return userList;
	}
	
	private void addNewUsers(){
		//User(String login, String password, Long ownerId, String firstname, String Lastname, Long groupId)
		IEntity newUser = new User("test@i10n.com","i10n", 2L, "test_first_"+i, "test_last_"+i, 1L);
		System.out.println("Before Insertion: " + newUser.toString());
//		newUser = userDao.insert(newUser);
		System.out.println("After Insertion: " + newUser.toString());
	}
	
	private void getStatusList(Long userId){/*
		List<LiveVehicleStatus> liveVehicleStatusList = ((LiveVehicleStatusDaoImp) DBManager.getInstance().
				getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).fetchLiveVehicleStatusOfUser(userId);
		HashMap<String, Integer> statusCount = new HashMap<String, Integer>();
		int count = 0;
		for(LiveVehicleStatus liveStatus : liveVehicleStatusList){
			VehicleStatus vehicleStatus = new DashboardDataProvider().getVehicleStatus(liveStatus);
			if(statusCount.get(vehicleStatus.toString()) != null){
				count = statusCount.get(vehicleStatus.toString());
				statusCount.put(vehicleStatus.toString(), ++count);
				System.out.println(vehicleStatus.toString()+": "+count);
			} else {
				statusCount.put(vehicleStatus.toString(), ++count);
				System.out.println("Null");
			}
			count = 0;
		}
		System.out.println("vehiclesonlinecount"+ statusCount.get(VehicleStatus.ONLINE.toString()));
		System.out.println("vehiclesofflinecount"+  statusCount.get(VehicleStatus.OFFLINE.toString()));
		System.out.println("vehiclesoffroadcount"+  statusCount.get(VehicleStatus.OFFROAD.toString()));
		System.out.println("vehiclesofflinebgsmcount"+  statusCount.get(VehicleStatus.OFFLINE_BAD_GSM.toString()));
		System.out.println("vehiclesofflinecdccount"+  statusCount.get(VehicleStatus.OFFLINE_CHARGER_DISCONNECTED.toString()));
	*/}
	
	private List<TrackHistory> getTrackHistoryListToInsert(List<TrackHistory> trackHistoryList){
		
		for(int i = 0; i< 100; i++){
			TrackHistory trackHistory = new TrackHistory();
			TrackHistoryDaoImpl trackHistoryDao = (TrackHistoryDaoImpl) getTrackHistoryDao();
			trackHistory.setId(new LongPrimaryKey(trackHistoryDao.getTrackHistoryIdIncrementer().nextLongValue()));
			trackHistory.setTripid(3L);
			trackHistory.setGpsSignal((float) 0);
			trackHistory.setGsmSignal((float) 0);
			trackHistory.setDirection(0);
			trackHistory.setSqg(0);
			trackHistory.setSqd(0);
			trackHistory.setCumulativeDistance(0);
			trackHistory.setBatteryVoltage((float) 0);
			trackHistory.setSpeed(0);
			trackHistory.setLac(0);
			trackHistory.setCid(0);
			trackHistory.setPing(false);
			trackHistory.setMrs(false);
			trackHistory.setChargerConnected(false);
			trackHistory.setRestart(false);
			trackHistory.setPanic(false);
			trackHistory.setOccurredat(new Date());
			Point p=new Point(77, 22);
			Geometry location = (Geometry)p;
			trackHistory.setLocation(location);
			trackHistory.setFuel(0);
			trackHistory.setDistance((float) 0);
			trackHistory.setPingCounter(0);
			trackHistory.setAnalog1(0);
			trackHistory.setAnalog2(0);
			trackHistory.setError(0);
			trackHistory.setDigital1(true);
			trackHistory.setDigital2(true);
			trackHistory.setDigital3(true);
			trackHistory.setGpsFixInformation(0);
			trackHistory.setAirDistance((float) 0);
			System.out.println(trackHistory.toString());
			trackHistoryList.add(trackHistory);
			try {
				trackHistoryDao.processInsertion(trackHistory);
			} catch (OperationNotSupportedException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return trackHistoryList;
		
	}
	


	
	/**
	 * Getter for UserDAO Object
	 * @return
	 * UserDAO Object 
	 */
	public IDAO getTrackHistoryDao() {
		return trackHistoryDao;
	}

	/**
	 * Setter for UserDAO Object
	 * @param userDao
	 * the userDAO object
	 */
	public void setTrackHistoryDao(IUserDAO userDao) {
		this.trackHistoryDao = userDao;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestDBConnection testDB = new TestDBConnection();
		testDB.getStatusList(1L);
//		TrackHistoryDaoImpl trackHistoryDao = (TrackHistoryDaoImpl) testDB.getTrackHistoryDao(); 
//		List<TrackHistory> trackHistoryList = new ArrayList<TrackHistory>();
//		System.out.println("Size : "+TrackHistoryDaoImpl.BATCH_INSERTION_ENTITY_LIST.size());
//		testDB.getTrackHistoryListToInsert(trackHistoryList);
//		System.out.println("Size : "+TrackHistoryDaoImpl.BATCH_INSERTION_ENTITY_LIST.size());
		//testDB.showAllUsers((UserDaoImp)userDao);
//		testDB.addNewUser();
	}
}
