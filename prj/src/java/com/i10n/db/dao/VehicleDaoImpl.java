package com.i10n.db.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.LiveVehicleStatusRowMapper;
import com.i10n.db.entity.rowmapper.TripRowMapper;
import com.i10n.db.entity.rowmapper.VehicleCacheRowMapper;
import com.i10n.db.entity.rowmapper.VehicleDetailsRowMapper;
import com.i10n.db.entity.rowmapper.VehicleRowMapper;
import com.i10n.db.idao.IVehicleDAO;
import com.i10n.dbCacheManager.LoadAclVehicleDetails;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.dbCacheManager.LoadUserDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class VehicleDaoImpl implements IVehicleDAO {

	private static Logger LOG = Logger.getLogger(VehicleDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer vehicleIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setVehicleIdIncrementer(DataFieldMaxValueIncrementer bookIncrementer) {
		this.vehicleIdIncrementer = bookIncrementer;
	}

	public DataFieldMaxValueIncrementer getVehicleIdIncrementer() {
		return vehicleIdIncrementer;
	}

	@Override
	public Vehicle delete(Vehicle vehicle)
			throws OperationNotSupportedException {
		String query = " select * from trips where vehicleid = ? and enddate is null";
		Object qargs[] = new Object[] { vehicle.getId().getId() };
		int qtypes[] = new int[] { Types.BIGINT };
		List<Vehicle> vehicles = jdbcTemplate.query(query, qargs, qtypes,new TripRowMapper());

		if (vehicles.size() == 0) {
			String sql = "update vehicles set deleted = true where id = ?";
			Object args[] = new Object[] { vehicle.getId().getId() };
			int types[] = new int[] { Types.BIGINT };
			jdbcTemplate.update(sql, args, types);
			jdbcTemplate.execute("NOTIFY update");
			return vehicle;
		}
		vehicle = null;
		return vehicle;
	}

	@Override
	public Vehicle update(Vehicle vehicle) throws OperationNotSupportedException {
		String sql ;
		Object args[];
		int types[] ;
		if(vehicle.getImeiId() != 0){
			sql = "update vehicles set displayname=?,make=?, model=?, year=?, imeiid = ?,vehicle_icon_pic_id=? where id=?";
			LOG.debug("Update Veihcle Query "+sql);
			args = new Object[] { vehicle.getDisplayName(),vehicle.getMake(), vehicle.getModel(), vehicle.getModelYear(),
					vehicle.getImeiId(), vehicle.getVehicleIconPicId(),vehicle.getId().getId() };
			types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.INTEGER, Types.BIGINT };
		} else {
			sql = "update vehicles set displayname=?,make=?, model=?, year=?, vehicle_icon_pic_id=? where id=?";
			LOG.debug("In Else Query "+sql);
			args = new Object[] { vehicle.getDisplayName(),vehicle.getMake(), vehicle.getModel(), vehicle.getModelYear(),
					vehicle.getVehicleIconPicId(),vehicle.getId().getId() };
			types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.BIGINT };
		}


		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			vehicle = null;
		}
		jdbcTemplate.execute("NOTIFY update");
		return vehicle;
	}
	public boolean updateGroupId(Long vehicleId, Long groupId)
			throws OperationNotSupportedException {
		StringBuffer updateVehicleGroupID = new StringBuffer();
		updateVehicleGroupID.append("update vehicles set groupid=");
		updateVehicleGroupID.append(groupId);
		updateVehicleGroupID.append(" where id=");
		updateVehicleGroupID.append(vehicleId);
		int rowsUpdated = jdbcTemplate.update(updateVehicleGroupID.toString());
		if (rowsUpdated != 1) {
			return false;
		}
		jdbcTemplate.execute("NOTIFY update");
		return true;

	}

	public boolean updateFuelCalId(Long tripid, Long imeiid) {
		String sql = "UPDATE vehicles SET fuelcaliberationid=(SELECT distinct(calibrationid) FROM fuelcalibrationvalues where tripid=?) where imeiid = ?";
		Object args[] = new Object[] { tripid, imeiid };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT };
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated != 1) {
			return false;
		}
		jdbcTemplate.execute("NOTIFY update");
		return true;
	}

	public boolean updateVacantGroupId(Long vehicleId)
			throws OperationNotSupportedException {
		String vsql = "select * from trips where vehicleid=? and enddate is null";
		Object vargs[] = new Object[] { vehicleId };
		int vtypes[] = new int[] { Types.BIGINT };
		List<Trip> vtrips = jdbcTemplate.queryForList(vsql, vargs, vtypes);

		if (vtrips.size() == 0) {
			String sql = "update vehicles set groupid=0 where id=?";
			Object args[] = new Object[] { vehicleId };
			int types[] = new int[] { Types.BIGINT };
			int rowsAffected = jdbcTemplate.update(sql, args, types);
			if(rowsAffected != 1){
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public Vehicle insert(Vehicle vehicle)
			throws OperationNotSupportedException {
		Long id = vehicleIdIncrementer.nextLongValue();
		vehicle.setId(new LongPrimaryKey(id));
		String sql = "INSERT INTO vehicles( "
				+ "id, make, model, year, optional1, optional2, optional3, imeiid, "
				+ "fuelcaliberationid, odometer_updatedat, odometer_value, vehicle_icon_pic_id, "
				+ "groupid, displayname,ownerid) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, " + " ?, ?, ?, ?, "
				+ "?, ?,?) ";

		Object args[] = new Object[] { id, vehicle.getMake(),vehicle.getModel(), vehicle.getModelYear(),
				vehicle.getOptional1(), vehicle.getOptional2(),vehicle.getOptional3(), vehicle.getImeiId(),
				vehicle.getFuelcaliberationid(),vehicle.getOdometerUpdatedAt(), vehicle.getOdometerValue(),
				vehicle.getVehicleIconPicId(), vehicle.getGroupId(),vehicle.getDisplayName(),
				SessionUtils.getCurrentlyLoggedInUser().getId() };
		int types[] = new int[] { Types.BIGINT, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.BIGINT, Types.BIGINT, Types.DATE, Types.INTEGER,Types.INTEGER, Types.BIGINT, Types.VARCHAR, Types.BIGINT };
		jdbcTemplate.update(sql, args, types);

		LOG.debug("Inserting vehicle : "+id+" added just now into aclvehicle table ");
		String asql = "insert into aclvehicle values(?,?)";
		Object aargs[] = new Object[] { id,SessionUtils.getCurrentlyLoggedInUser().getId() };
		int atypes[] = new int[] { Types.BIGINT, Types.BIGINT };
		jdbcTemplate.update(asql, aargs, atypes);
		jdbcTemplate.execute("NOTIFY vehicles");
		return vehicle;
	}

	@Override
	public List<Vehicle> selectAll() {
		String sql = "select * from vehicles where deleted = false";
		List<Vehicle> result = jdbcTemplate.query(sql, new VehicleRowMapper());
		return result;
	}

	public List<Vehicle> selectAllOwned() {
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long userId = user.getId();
		String sql = "select * from vehicles where deleted = false and id in ( select vehicleid from aclvehicle where "
				+ "userid in ( select id from users where owner_id =? or id =?))";
		LOG.debug("Qeury --------- "+sql);
		Object[] args = new Object[] { userId, userId };
		int[] types = new int[] { Types.BIGINT, Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new VehicleRowMapper());
	}

	public List<Vehicle> getVacantVehicle(Long userId) {
		User user = LoadUserDetails.getInstance().retrieve(userId);
		String sql = "select * from vehicles where id not in( select vehicleid from aclvehicle where userid = ? ) and ownerid = ? " +
				"and deleted = false";
		LOG.debug("Vacant Vehicle Data "+sql);
		Object[] args = new Object[] { userId, user.getOwnerId() };
		int[] types = new int[] { Types.BIGINT, Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new VehicleRowMapper());
	}

	public List<Vehicle> getVacantVehicles(Long userId) {
		User user = LoadUserDetails.getInstance().retrieve(userId);

		String sql = "select * from vehicles where id in ((select vehicleid from aclvehicle where userid = "
				+user.getOwnerId()+") except ( select vehicleid from aclvehicle where userid = "+user.getOwnerId()+" and vehicleid not in " 
				+"((select vehicleid from trips where enddate is null) except (select vehicleid from trips where active = 't')))) " 
				+" and groupid <> 0 and deleted = false";

		LOG.debug("Tripsettings Vacant vehicle fetch query1 : "+sql);

		List<Vehicle> vacantVehicles = jdbcTemplate.query(sql, new VehicleRowMapper());

		sql = "select * from vehicles where id in( select vehicleid from aclvehicle where userid = "+ user.getId()
				+") and ownerid = "+user.getOwnerId()+" and id not in (select vehicleid from trips ) and groupid <> 0 and deleted = false";
		LOG.debug("Tripsettings Vacant vehicle fetch query2 : "+sql);
		List<Vehicle> vacantVehicleList = jdbcTemplate.query(sql, new VehicleRowMapper());

		for(Vehicle vehicle : vacantVehicleList){
			if(!vacantVehicles.contains(vehicle)){
				vacantVehicles.add(vehicle);
			}
		}
		return vacantVehicles;
	}

	public List<Vehicle> getAlertVacantVehicles(Long alertUserId) {
		String sql = "select * from vehicles where id not in (select vehicleid from aclalerts where alertuserid = "+ alertUserId
				+ " ) and groupid in(select id from group_values where groupid = "+SessionUtils.getCurrentlyLoggedInUser().getGroupId()
				+ " ) and deleted = false and id in (select vehicleid from aclvehicle where userid ="
				+ SessionUtils.getCurrentlyLoggedInUser().getId() + " )";
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getAlertAssignedVehicles(Long alertUserId) {
		String sql = "select * from vehicles where id in (select vehicleid from aclalerts where alertuserid = "
				+ alertUserId
				+ " ) and groupid in(select id from group_values where groupid = "+SessionUtils.getCurrentlyLoggedInUser().getGroupId()
				+ " ) and deleted = false";
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getReportVacantVehicles(Long reportId) {
		String sql = "select * from vehicles where id not in (select vehicleid from aclreports where reportid = "
				+ reportId
				+ " ) and groupid in(select id from group_values where groupid = "+SessionUtils.getCurrentlyLoggedInUser().getGroupId()
				+ " ) and deleted = false and id in (select vehicleid from aclvehicle where userid ="
				+ SessionUtils.getCurrentlyLoggedInUser().getId() + " )";
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getReportAssignedVehicles(Long reportId, Long groupId) {
		String sql = "select * from vehicles where id in (select vehicleid from aclreports where reportid = "+ reportId
				+ " ) and groupid in(select id from group_values where groupid = "+ groupId
				+" ) and deleted = false";
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getMobileVacantVehicles(Long mobileId) {
		String sql = "select * from vehicles where id not in (select vehicleid from aclmobile where mobileid = "+ mobileId
				+ " ) and groupid in(select id from group_values where groupid = "+SessionUtils.getCurrentlyLoggedInUser().getGroupId()
				+ " ) and deleted = false and id in (select vehicleid from aclvehicle where userid ="
				+ SessionUtils.getCurrentlyLoggedInUser().getId() + " )";
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getMobileAssignedVehicles(Long mobileId) {
		String sql = "select * from vehicles where id in (select vehicleid from aclmobile where mobileid = "+ mobileId
				+ ") and groupid in(select id from group_values where groupid = "+SessionUtils.getCurrentlyLoggedInUser().getGroupId()
				+" ) and deleted = false";
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getGroupVacantVehicles(ArrayList<Long> userId) {
		String query = "select * from vehicles where id in (select vehicleid from aclvehicle where userid in "
				+ userId + ") and groupid=0 and deleted = false";
		String newsql = query.replace("[", "(");
		String sql = newsql.replace("]", ")");
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getGeoFencingVacantVehicles(Long regionId) {
		User usr = SessionUtils.getCurrentlyLoggedInUser();
		Long userId = usr.getId();
		String sql = "select * from vehicles where id in (select vehicleid from aclvehicle where userid=?) and id not in "
				+ "(select vehicleid from vehicle_geofenceregions where regionid=?) and deleted=false";
		Object[] args = new Object[] { userId, regionId };
		int[] types = new int[] { Types.BIGINT, Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new VehicleRowMapper());
	}

	public List<Vehicle> getGeoFencingAssignedVehicles(Long regionId) {
		String sql = "select * from vehicles where id in (select vehicleid from aclvehicle where userid=?) and id in "
				+ "(select vehicleid from vehicle_geofenceregions where regionid=?) and deleted = false";
		Object[] args = new Object[] { SessionUtils.getCurrentlyLoggedInUser().getId(), regionId };
		int[] types = new int[] { Types.BIGINT, Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new VehicleRowMapper());
	}

	public List<LiveVehicleStatus> getActiveVehicles(Long vehicleId) {
		String sql = "select * from livevehiclestatus where tripid in (select id from trips where vehicleid=? and active='t')";
		Object[] args = new Object[] { vehicleId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types,new LiveVehicleStatusRowMapper());
	}

	/**
	 * 
	 * @param userId
	 * @return vehicle list assigned to the given user (maximum of 5 vehicles)
	 */
	public List<Vehicle> getAssignedVehicleForDashboard(Long userId) {
		List<Vehicle> assignedVehicleList = new ArrayList<Vehicle>();
		for(Long userLong : LoadAclVehicleDetails.getInstance().cacheAclVehicleDetails.keySet()){
			if(userLong == userId){
				Vector<Long> vehicleIdList = LoadAclVehicleDetails.getInstance().cacheAclVehicleDetails.get(userLong);
				for(Long vehicleId : vehicleIdList){
					Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
					if(!vehicle.isDeleted()){ 
						assignedVehicleList.add(vehicle);
					}
					if(assignedVehicleList.size() == 5){
						return assignedVehicleList;				
					}
				}
			}
		}
		return assignedVehicleList;
	}

	public List<Vehicle> getAssignedVehicles(Long userId) {
		String sql = "select * from vehicles where id in (select distinct(vehicleid) from aclvehicle where userid=?) and deleted = false ";
		LOG.debug("Assigned Vehicle Query "+sql);
		Object[] args = new Object[] { userId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types,new VehicleRowMapper());
	}

	public List<Vehicle> getGroupAssignedVehicle(Long groupId,ArrayList<Long> userIds) {
		String query = "select * from vehicles where id in (select vehicleid from aclvehicle where userid in "
				+ userIds + ") and groupid=? and deleted = false";
		String newsql = query.replace("[", "(");
		String sql = newsql.replace("]", ")");
		Object[] args = new Object[] { groupId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types,new VehicleRowMapper());
	}

	public List<Vehicle> isAssigned(Long vehicleid) {
		String sql = "select * from vehicles where id = " + vehicleid
				+ " and id in(select vehicleid from aclvehicle where userid = "
				+ SessionUtils.getCurrentlyLoggedInUser().getId()+ ") and deleted = false ";
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	@Override
	public List<Vehicle> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from vehicles where id = " + primaryKey.getId();
		LOG.debug("Selecting ID ------ asdasdasd------------- "+sql);
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> selectByPrimaryKey(List<LongPrimaryKey> primaryKey) {
		int i = 0;
		List<Long> vehicleidlist = new ArrayList<Long>();
		while (i < primaryKey.size()) {
			vehicleidlist.add(primaryKey.get(i).getId());
			i++;
		}
		String query;
		if (primaryKey.size() == 0) {
			query = "select * from vehicles where deleted = false";
		} else {
			query = "select * from vehicles where id in" + vehicleidlist+ " and deleted = false";
		}
		String newsql = query.replace("[", "(");
		String sql = newsql.replace("]", ")");
		return jdbcTemplate.query(sql, new VehicleRowMapper());
	}

	public List<Vehicle> getVehiclename(Long vehicleId) {
		String query = "select * from vehicles where id=" + vehicleId;
		LOG.debug("Select ID ---- "+query);
		return jdbcTemplate.query(query, new VehicleRowMapper());
	}

	public Vehicle getVehicleForTheTrip(LongPrimaryKey tripId) {
		String sql = "select * from vehicles where id = (select vehicleid from trips where id = ?)";
		int[] variableTypes = new int[] { Types.BIGINT };
		Object[] variableValues = new Object[] { tripId.getId() };
		List<Vehicle> vehicles = jdbcTemplate.query(sql, variableValues,variableTypes, new VehicleRowMapper());
		return (vehicles == null || vehicles.size() == 0) ? null : vehicles.get(0);
	}

	public Boolean isInvolvedInTrip(long vehicleId) {
		for(Long tripId : LoadTripDetails.getInstance().cachedTrips.keySet()){
			Trip trip = LoadTripDetails.getInstance().retrieve(tripId);
			if(trip.getVehicleId() == vehicleId){
				return true;
			}
		}
		return false;
	}

	public List<Vehicle> selectVehiclename(Long vehicleid) {
		String sql = "select * from vehicles where id=?";
		LOG.debug("Seleciing Vehicle Name -- "+sql);
		Object[] args = new Object[] { vehicleid };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types,new VehicleRowMapper());
	}

	public List<String> getVehicleNameList(List<Long> vehicleid) {
		String query = "select displayname from vehicles where id in "+ vehicleid;
		LOG.debug("Query for selecting Name "+query);
		String newsql = query.replace("[", "(");
		String sql = newsql.replace("]", ")");
		return jdbcTemplate.queryForList(sql, String.class);
	}

	/**
	 * Function to get Owner Id of the vehicle
	 */
	public Long getOwnerIdOfVehicle(Long vehicleId) {
		String sql = "select ownerid from vehicles where id=?";
		LOG.debug("getOwnerIdOfVehicle ----------- "+sql);
		Object[] args = new Object[] { vehicleId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.queryForLong(sql, args, types);
	}

	public List<Vehicle> getAssignedVehicleHavingTrip(Long userId) {
		String sql = "select v.*, l.maxspeed, l.vehiclelocation,l.gpsstrength,l.gsmstrength,l.batvolt,l.fuelad ,d.id as driverid ,"+
				" d.firstname, d.lastname, d.groupid as drivergroupid,vg.group_value as vehiclegroupvalue, dg.group_value as drivergroupvalue, l.tripid, l.lastupdatedat  "+
				" from vehicles v, livevehiclestatus l, drivers d, group_values vg, group_values dg where v.id in (select vehicleid from "+
				"aclvehicle where userid = ? ) and v.id in ( select vehicleid from trips) and v.deleted = false and l.tripid = (select "+
				"tripid from livevehiclestatus where tripid in (select id from trips where vehicleid=v.id ) order by lastupdatedat desc limit 1)"+
				" and d.id = (select driverid from trips where id = l.tripid) and vg.id = v.groupid and dg.id = d.groupid limit 5 ";
		Object[] args = new Object[] { userId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new VehicleDetailsRowMapper());
	}

	public List<Vehicle> getAssignedVehicleHavingTripForReports(Long userId) {

		LOG.debug("In Vehicle Dao Impl getAssignedVehicleHavingTripreports");
		String sql = "select v.*, l.maxspeed, l.vehiclelocation,l.gpsstrength,l.gsmstrength,l.batvolt,l.fuelad ,d.id as driverid ,"+
				" d.firstname, d.lastname, d.groupid as drivergroupid,vg.group_value as vehiclegroupvalue, dg.group_value as drivergroupvalue, l.tripid, l.lastupdatedat  "+
				" from vehicles v, livevehiclestatus l, drivers d, group_values vg, group_values dg where v.id in (select vehicleid from "+
				"aclvehicle where userid = ? ) and v.id in ( select vehicleid from trips) and v.deleted = false and l.tripid = (select "+
				"tripid from livevehiclestatus where tripid in (select id from trips where vehicleid=v.id ) order by lastupdatedat desc limit 1)"+
				" and d.id = (select driverid from trips where id = l.tripid) and vg.id = v.groupid and dg.id = d.groupid ";
		Object[] args = new Object[] { userId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new VehicleDetailsRowMapper());
	}

	public List<Vehicle> selectAllForCache() {
		String sql = "select vehicles.*, group_values.group_value as groupname, hardwaremodules.imei " +
				"as imei from vehicles, group_values, hardwaremodules where vehicles.groupid = group_values.id and " +
				"vehicles.imeiid = hardwaremodules.id";
		return jdbcTemplate.query(sql, new VehicleCacheRowMapper());
	}

	public List<Vehicle> selectByVehicleIdForCache(Long vehicleId) {
		String sql = "select vehicles.*, group_values.group_value as groupname, hardwaremodules.imei as"+
				" imei from vehicles, group_values, hardwaremodules where vehicles.groupid = group_values.id and "+
				"vehicles.imeiid = hardwaremodules.id and vehicles.id = ? ";
		Object[] args = new Object[] { vehicleId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new VehicleCacheRowMapper());
	}

}
