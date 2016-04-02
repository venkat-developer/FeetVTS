package com.i10n.db.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Driver;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.User;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.DriverGroupReportRowMapper;
import com.i10n.db.entity.rowmapper.DriverRowMapper;
import com.i10n.db.entity.rowmapper.DriverSmartcardRowMapper;
import com.i10n.db.entity.rowmapper.TripRowMapper;
import com.i10n.db.idao.IDriverDAO;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadUserDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class DriverDaoImp implements IDriverDAO {

	private static Logger LOG = Logger.getLogger(DriverDaoImp.class);

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer driverIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setDriverIdIncrementer(DataFieldMaxValueIncrementer driverIncrementer) {
		this.driverIdIncrementer = driverIncrementer;
	}

	@Override
	public Driver delete(Driver driver) throws OperationNotSupportedException {
		String query = " select * from trips where driverid = ? and enddate is null";
		Object qargs[] = new Object[] { driver.getId().getId() };
		int qtypes[] = new int[] { Types.BIGINT };
		List<Driver> drivers = jdbcTemplate.query(query, qargs, qtypes, new TripRowMapper());

		if (drivers.size() == 0) {
			String sql = "update drivers set deleted = true where id = ?";
			Object args[] = new Object[] { driver.getId().getId() };
			int types[] = new int[] { Types.BIGINT };
			jdbcTemplate.update(sql, args, types);
			jdbcTemplate.execute("NOTIFY update");
			return driver;
		}
		driver = null;
		return driver;
	}

	@Override
	public Driver update(Driver driver) throws OperationNotSupportedException {
		String sql = "update drivers set firstname = ?,lastname = ?, licenseno = ? ,photo = ? where id = ? ";
		Object arg[] = new Object[] { driver.getFirstName(),
				driver.getLastName(), driver.getLicenseno(),driver.getPhoto(),driver.getId().getId() };
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.BIGINT,Types.VARCHAR,Types.BIGINT };
		int rowsUpdated = jdbcTemplate.update(sql, arg, types);
		if (rowsUpdated != 1) {
			driver = null;
		}
		jdbcTemplate.execute("NOTIFY update");
		return driver;
	}

	public List<Driver> isDriverAssigned(Long driverid) {
		String sql = "select * from drivers where id = " + driverid+ " and id in(select driverid from acldriver where userid = "
				+ SessionUtils.getCurrentlyLoggedInUser().getId()+ ") and deleted = false";
		return jdbcTemplate.query(sql, new DriverRowMapper());
	}

	@Override
	public Driver insert(Driver driver) throws OperationNotSupportedException {
		Long id = driverIdIncrementer.nextLongValue();
		driver.setId(new LongPrimaryKey(id));

		String sql = "insert into drivers (id, firstname, lastname, licenseno, photo,groupid,ownerid) values (?,?,?,?,?,?,?)";
		Object args[] = new Object[] { id, driver.getFirstName(),driver.getLastName(), driver.getLicenseno(), driver.getPhoto(),
				driver.getGroupId(),SessionUtils.getCurrentlyLoggedInUser().getId() };
		int types[] = new int[] { Types.BIGINT, Types.VARCHAR, Types.VARCHAR,
				Types.BIGINT, Types.VARCHAR, Types.BIGINT,Types.BIGINT };
		jdbcTemplate.update(sql, args, types);

		String asql = "insert into acldriver values(?,?)";
		Object aargs []= new Object[] {id,SessionUtils.getCurrentlyLoggedInUser().getId()};
		int atypes[] = new int[] { Types.BIGINT, Types.BIGINT};
		jdbcTemplate.update(asql, aargs, atypes);
		jdbcTemplate.execute("NOTIFY drivers");
		return driver;
	}

	@Override
	public List<Driver> selectAll() {
		String sql = "select * from drivers where deleted = false";
		return jdbcTemplate.query(sql, new DriverRowMapper());
	}

	public List<Driver> selectAllOwned(){
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long userId = user.getId(); 
		String sql="select * from drivers where deleted = false and id in ( select driverid from acldriver where "+
				"userid in ( select id from users where owner_id =? or id =?))";
		Object[] args = new Object[] { userId,userId };
		int[] types = new int[] { Types.BIGINT, Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new DriverRowMapper());
	}

	@Override
	public List<Driver> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from drivers where id = " + primaryKey.getId();
		return jdbcTemplate.query(sql, new DriverRowMapper());
	}

	public Driver getDriverForTheTrip(LongPrimaryKey tripId) {
		String sql = "select * from drivers where id in (select driverid from trips where id = ?) and deleted = false";
		int[] variableTypes = new int[] { Types.BIGINT };
		Object[] variableValues = new Object[] { tripId.getId() };
		List<Driver> drivers = jdbcTemplate.query(sql, variableValues, variableTypes, new DriverRowMapper());
		return (drivers == null || drivers.size() == 0) ? null : drivers.get(0);
	}

	public List<Driver> getVacantDrivers(Long userId) {

		User user = LoadUserDetails.getInstance().retrieve(userId);
		String sql = "select * from drivers where id in ((select driverid from acldriver where userid = "
				+user.getOwnerId()+") except ( select driverid from acldriver where userid = "+user.getOwnerId()+" and driverid not in " 
				+"((select driverid from trips where enddate is null) except (select driverid from trips where active = 't')))) " 
				+" and groupid <> 0 and deleted = false";
		LOG.debug("Tripsettings Vacant driver fetch query1 : "+sql);

		List<Driver> vacantDrivers = jdbcTemplate.query(sql, new DriverRowMapper());

		sql = "select * from drivers where id in( select driverid from acldriver where userid = "
				+userId+") and ownerid = "+user.getOwnerId()+" and id not in (select driverid from trips ) and groupid <> 0 and deleted = false";
		LOG.debug("Tripsettings Vacant driver fetch query2 : "+sql);
		List<Driver> vacantDriverList = jdbcTemplate.query(sql, new DriverRowMapper());
		
		for(Driver driver : vacantDriverList){
			if(!vacantDrivers.contains(driver)){
				vacantDrivers.add(driver);
			}
		}
		return vacantDrivers;
	}

	public List<Driver> getNonAsssignedDrivers(Long userId) {
		User user = LoadUserDetails.getInstance().retrieve(userId);
		String sql = "select * from drivers where id not in ( select driverid from acldriver where userid="+ userId
				+ ") and ownerid = "+user.getOwnerId()+" and deleted = false";
		return jdbcTemplate.query(sql, new DriverRowMapper());
	}

	@Override
	public List<Driver> getDriver(LongPrimaryKey owner) {
		String sql = "Select * from drivers where owner_id=? and deleted = false";
		Object[] arg = new Object[] { owner };
		int[] type = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, arg, type, new DriverRowMapper());
	}

	public List<Driver> getAssignedDriver(Long userid) {
		String sql = "Select * from drivers where id in (select driverid from acldriver where userid = ? ) and deleted = false";
		Object[] arg = new Object[] { userid };
		int[] type = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, arg, type, new DriverRowMapper());
	}

	public List<Driver> getAssignedDriverWithGroupValue(Long userid) {

		String sql="Select drivers.id,drivers.firstname,drivers.lastname,drivers.licenseno,drivers.photo," +
				"drivers.groupid,drivers.deleted, group_values.group_value from drivers,group_values " +
				"where drivers.id in (select driverid from acldriver where userid=?)" +
				" and drivers.deleted = false and group_values.id=drivers.groupid";
		Object[] arg = new Object[] { userid };
		int[] type = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, arg, type,new DriverGroupReportRowMapper());
	}

	public List<Driver> getGroupAssignedDriver(Long groupId, ArrayList<Long> userIds) {
		String query = "select * from drivers where id in (select driverid from acldriver where userid in "
				+ userIds + ") and groupid=? and deleted = false";
		String newsql = query.replace("[", "(");
		String sql = newsql.replace("]", ")");
		Object[] args = new Object[] { groupId };
		int[] types = new int[] { Types.BIGINT };
		List<Driver> result = jdbcTemplate.query(sql, args, types, new DriverRowMapper());
		return result;
	}

	public List<Driver> getGroupVacantDriver(ArrayList<Long> userId) {
		String query = "select * from drivers where id in (select driverid from acldriver where userid in "
				+ userId + ") and groupid=0 and deleted = false";
		String newsql = query.replace("[", "(");
		String sql = newsql.replace("]", ")");
		List<Driver> result = jdbcTemplate.query(sql, new DriverRowMapper());
		return result;
	}

	public boolean updateGroupId(Long driverId, Long groupId) throws OperationNotSupportedException {
		String sql = "update drivers set groupid=? where id=?";
		Object args[] = new Object[] { groupId, driverId };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT };
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			return false;
		}
		jdbcTemplate.execute("NOTIFY drivers");
		return true;

	}

	public boolean updateVacantGroupId(Long driverId) throws OperationNotSupportedException {
		String dsql = "select * from trips where driverid=? and enddate is null";
		Object dargs[] = new Object[] { driverId };
		int dtypes[] = new int[] { Types.BIGINT };
		List<Trip> vtrips = jdbcTemplate.queryForList( dsql, dargs, dtypes);
		if (vtrips.size() == 0) {
			String sql = "update drivers set groupid=0 where id=?";
			Object args[] = new Object[] { driverId };
			int types[] = new int[] { Types.BIGINT };
			jdbcTemplate.update(sql, args, types);
			return true;
		}
		return false;
	}

	public List<Driver> selectDriverName(String mSmartcardid) {
		String sql = "select  drivers.firstname,hardwaremodules.imei,vehicles.id from"+
				" drivers,hardwaremodules,vehicles where drivers.id in (select driverid from trips"+
				" where active='t' and vehicleid in ( select id from vehicles where imeiid in(select id "+
				"from hardwaremodules where imei in(select imei from smartcard where smartcardid=?)))) and "+
				"hardwaremodules.imei in (select smartcard.imei from smartcard where smartcardid=?) and "+
				"vehicles.id=(select id from vehicles where imeiid in(select id from hardwaremodules where"+
				" imei in(select imei from smartcard where smartcardid=?)))";
		Object args[] = new Object[] { mSmartcardid,mSmartcardid,mSmartcardid };
		int types[] =new int[]{Types.VARCHAR,Types.VARCHAR,Types.VARCHAR};
		List<Driver> result= jdbcTemplate.query(sql, args, types,new DriverSmartcardRowMapper());
		return result;
	}

	/**
	 * Retrieving list from cache
	 * @param userid
	 * @return
	 */
	public List<Driver> getDriveId(Long userid) {
		/* Vishnu : Fetching information from cache */
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(userid.longValue());

		List<Driver> driverArray = new ArrayList<Driver>();
		for (TripDetails trip :tripDetailsList) {
			driverArray.add(trip.getDriver());
		}
		return driverArray;
	}


	/**
	 * Load from cache
	 * @param primaryKey
	 * @return
	 */
	public List<Driver> selectByPrimaryKey(List<LongPrimaryKey> primaryKeys) {
		List<Driver> drivers = new ArrayList<Driver>();
		for (LongPrimaryKey lpk : primaryKeys) {
			Driver driver = LoadDriverDetails.getInstance().retrieve(lpk.getId());
			if (driver!=null) {
				drivers.add(driver);
			}
		}
		return drivers;		
	}
}