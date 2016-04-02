package com.i10n.db.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.VehicleStatusCount;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.HardwareModuleRowMapper;
import com.i10n.db.entity.rowmapper.LiveVehicleStatusCacheRowMapper;
import com.i10n.db.entity.rowmapper.LiveVehicleStatusRowMapper;
import com.i10n.db.entity.rowmapper.VehicleStatusCountRowMapper;
import com.i10n.db.idao.ILiveVehicleStatusDAO;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;

@SuppressWarnings("unchecked")
public class LiveVehicleStatusDaoImp implements ILiveVehicleStatusDAO {

	private static Logger LOG = Logger.getLogger(LiveVehicleStatusDaoImp.class);

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public LiveVehicleStatus delete(LiveVehicleStatus entity)
			throws OperationNotSupportedException {
		return null;
	}

	@Override
	public LiveVehicleStatus insert(LiveVehicleStatus entity){
		try{
			String sql = "INSERT INTO livevehiclestatus( tripid, vehiclelocation, gsmstrength, gpsstrength, batvolt, distance, "
					+ "maxspeed, cc, sqd, sqg, mrs, course, isidle, fuelad, cdc_counter, "
					+ "cumulativedistance, mailsent, prevsqd, cid, lac, geolocation, "
					+ "lastupdatedat, moduleupdatetime, rs) " + "VALUES (?, "
					+ "GeometryFromText('POINT ("
					+ entity.getLocation().getFirstPoint().getX() + " "
					+ entity.getLocation().getFirstPoint().getY() + ")',-1) "
					+ " , ?, ?, ?, ?," + "?, ?, ?, ?, ?, ?, ?, ?, ?,"
					+ "?, ?, ?, ?, ?, ?," + "?, ?,?)";

			Object args[] = new Object[] { entity.getTripId().getId(), entity.getGsmStrength(), entity.getGpsStrength(),
					entity.getBatteryVoltage(), entity.getDistance(), entity.getMaxSpeed(), entity.isChargerConnected(),
					entity.getSqd(), entity.getSqg(), entity.getMrs(), entity.getCourse(), entity.isIdle(), entity.getFuelAd(),
					entity.getCdcCounter(), entity.getCumulativeDistance(), entity.isMailSent(), entity.getPrevSqd(),
					entity.getCellId(), entity.getLocationAreaCode(), entity.getLocationString(), entity.getLastUpdatedAt(), 
					entity.getModuleUpdateTime(), entity.getRs() };

			jdbcTemplate.update(sql, args);

		}catch(Exception e){
			LOG.error(e);
		}
		return entity;

	}


	public List<LiveVehicleStatus> fetchLiveVehicleStatusOfUser(long userId) {
		List<LiveVehicleStatus> liveVehicleStatus = null;

		String sql = "select * from livevehiclestatus where tripid in " + "("
				+ "select id from trips where active='t' and vehicleid in "
				+ "(" + "select vehicleid from aclvehicle where userid = "
				+ userId + ")" + ")";
		liveVehicleStatus = (List<LiveVehicleStatus>) jdbcTemplate.query(sql,
				new LiveVehicleStatusRowMapper());

		return liveVehicleStatus;

	}

	public List<LiveVehicleStatus> selectByTripName(String tripname) {
		String sql = "select * from livevehiclestatus where tripid in(select id from trips where tripname=?) order by lastupdatedat desc limit 1";
		Object[] arg = new Object[]{tripname};
		int[] type = new int[]{Types.VARCHAR};
		return jdbcTemplate.query(sql, arg, type, new LiveVehicleStatusRowMapper());
	}

	@Override
	public LiveVehicleStatus update(LiveVehicleStatus lvStatus)
			throws OperationNotSupportedException {

		StringBuffer sql = new StringBuffer(); 
		sql.append("UPDATE livevehiclestatus SET vehiclelocation= ");
		sql.append("GeometryFromText('POINT (");
		sql.append(lvStatus.getLocation().getFirstPoint().getX());
		sql.append(" ");
		sql.append(lvStatus.getLocation().getFirstPoint().getY());
		sql.append(")',-1) , gsmstrength = ");
		sql.append(lvStatus.getGsmStrength());
		sql.append(", gpsstrength = ");
		sql.append(lvStatus.getGpsStrength());
		sql.append(", batvolt = ");
		sql.append(lvStatus.getBatteryVoltage());
		sql.append(", distance=");
		sql.append(lvStatus.getDistance());
		sql.append(", maxspeed = "); 
		sql.append(lvStatus.getMaxSpeed());
		sql.append(", cc = ");
		sql.append(lvStatus.isChargerConnected());
		sql.append(", sqd = ");
		sql.append(lvStatus.getSqd());
		sql.append(", sqg = ");
		sql.append(lvStatus.getSqg());
		sql.append(", mrs = ");
		sql.append(lvStatus.getMrs());
		sql.append(", course = ");
		sql.append(lvStatus.getCourse());
		sql.append(", isidle = ");
		sql.append(lvStatus.isIdle());
		sql.append(", fuelad = ");
		sql.append(lvStatus.getFuelAd());
		sql.append(", cumulativedistance = ");
		sql.append(lvStatus.getCumulativeDistance());
		sql.append(", prevsqd = ");
		sql.append(lvStatus.getPrevSqd());
		sql.append(", cid = ");
		sql.append(lvStatus.getCellId());
		sql.append(", lac = ");
		sql.append(lvStatus.getLocationAreaCode());
		sql.append(", geolocation = '");
		sql.append(lvStatus.getLocationString());
		sql.append("', lastupdatedat = '"); 
		sql.append(DateUtils.convertJavaDateToSQLDate(lvStatus.getLastUpdatedAt()));
		sql.append("', rs = ");
		sql.append(lvStatus.getRs());
		sql.append(", moduleupdatetime = '");
		sql.append(DateUtils.convertJavaDateToSQLDate(lvStatus.getModuleUpdateTime()));
		sql.append("', firmwareversion = ");
		sql.append(lvStatus.getFirmwareVersion());
		sql.append(", latestbuttonpressed  = ");
		sql.append(lvStatus.getLatestButtonPressed());
		sql.append(", buttonsequence  = '");
		sql.append(lvStatus.getButtonSequence());
		sql.append("' WHERE tripid = ");
		sql.append(lvStatus.getTripId().getId());

		LOG.debug("LVS Update Query : "+sql.toString());
		
		jdbcTemplate.update(sql.toString());

		return lvStatus;
	}

	
	public int updateIsOffRoadStatus(boolean isOffRoad,long vehicleId)	throws OperationNotSupportedException {

		StringBuffer sql = new StringBuffer(); 
		sql.append("UPDATE livevehiclestatus SET ");
		sql.append(" isoffroad  = '");
		sql.append(isOffRoad);
		sql.append("' WHERE tripid in ( select id from trips where vehicleid = "+vehicleId+" )");

		LOG.debug("LVS Update Query : "+sql.toString());
		

		return jdbcTemplate.update(sql.toString());
	}
	
	/**
	 * Retrieving from cache the list of LVSO objects corresponding to keys
	 */
	@Override
	public List<LiveVehicleStatus> fetchLiveVehicleStatusByTripIDs(
			List<Long> tripIds) {
		List <LiveVehicleStatus> lvsArray= new ArrayList <LiveVehicleStatus>();
		for (Long tripId : tripIds) {
			LiveVehicleStatus lvs = LoadLiveVehicleStatusRecord.getInstance().retrieveByTripId(tripId);
			if (lvs!=null) {
				lvsArray.add(lvs);
			}
		}	
		return lvsArray;
	}


	/**
	 * Retrieving from cache the LVSO object corresponding to key 
	 */
	@Override
	public List<LiveVehicleStatus> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		List <LiveVehicleStatus> lvsArray= new ArrayList <LiveVehicleStatus>();
		LiveVehicleStatus lvs = LoadLiveVehicleStatusRecord.getInstance().retrieveByTripId(primaryKey.getId());
		if (lvs!=null) {
			lvsArray.add(lvs);
		}
		return lvsArray;
	}


	/**
	 * Returns the info from cache
	 */
	@Override
	public List<LiveVehicleStatus> selectAll() {
		return LoadLiveVehicleStatusRecord.getInstance().retrieveAll();
	}

	public String getImeiUsingTripId(long tripId){
		String sql="select * from hardwaremodules where id in((select imeiid from vehicles where id in(select vehicleid from trips where id="+tripId+")))";
		List<HardwareModule> imeiList = jdbcTemplate.query(sql,new HardwareModuleRowMapper());

		String imei = null;
		if(imeiList != null && imeiList.size() > 0)
			imei = imeiList.get(0).getImei();
		return imei;

	}

	public LiveVehicleStatus updateFromAlertPacket(LiveVehicleStatus liveVehicleStatus)
			throws OperationNotSupportedException {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE livevehiclestatus ");
		sql.append("SET vehiclelocation = ");
		sql.append("GeometryFromText('POINT (");
		sql.append(liveVehicleStatus.getLocation().getFirstPoint().getX());
		sql.append(" ");
		sql.append(liveVehicleStatus.getLocation().getFirstPoint().getY());
		sql.append(")',-1) ");
		sql.append(", lastupdatedat = '");
		sql.append(DateUtils.convertJavaDateToSQLDate(liveVehicleStatus.getLastUpdatedAt()));
		sql.append("', moduleupdatetime = '"+DateUtils.convertJavaDateToSQLDate(liveVehicleStatus.getModuleUpdateTime()));
		sql.append("' WHERE tripid = "+liveVehicleStatus.getTripId().getId());
		LOG.debug("updateFromAlertPacket query is "+sql.toString());
		jdbcTemplate.update(sql.toString());
		return liveVehicleStatus;
	}

	public List<LiveVehicleStatus> selectAllForCache() {
		String sql = "select hm.imei, lv.tripid, lv.cumulativedistance, lastupdatedat, lv.moduleupdatetime, gsmstrength, gpsstrength, lv.sqd, lv.sqg,"
				+" 	lv.vehiclelocation, batvolt, lv.isoffroad, cc, maxspeed, v.id as vehicleid, t.speedlimit, t.scheduledtrip, "
				+"  t.tripstartdate, t.enddate, t.driverid, 0 as digital1, 0 as digital2, 0 as pingcounter, 0 as gps_fix_information, v.displayname,"
				+"	lv.latestbuttonpressed, lv.isidle, lv.course "
				+"  from livevehiclestatus lv "
				+"  left join trips t on lv.tripid = t.id "
				+"  left join vehicles v on t.vehicleid= v.id "
				+"  left join hardwaremodules hm on v.imeiid=hm.id  "
				+" where t.active='t' and t.enddate is null";
		return jdbcTemplate.query(sql, new LiveVehicleStatusCacheRowMapper());
	}

	public List<LiveVehicleStatus> selectAllForCacheByImei(String imei) {
		String sql = "select hm.imei, lv.tripid, lv.cumulativedistance, lastupdatedat, lv.moduleupdatetime, gsmstrength, gpsstrength, lv.sqd, lv.sqg,"
				+" 	lv.vehiclelocation, batvolt, lv.isoffroad, cc, maxspeed, v.id as vehicleid, t.speedlimit, t.scheduledtrip, "
				+"  t.tripstartdate, t.enddate, t.driverid, 0 as digital1, 0 as digital2, 0 as pingcounter, 0 as gps_fix_information, v.displayname,"
				+"	lv.latestbuttonpressed, lv.isidle, lv.course "
				+"  from livevehiclestatus lv "
				+"  left join trips t on lv.tripid = t.id "
				+"  left join vehicles v on t.vehicleid= v.id "
				+"  left join hardwaremodules hm on v.imeiid=hm.id  "
				+" where t.active='t' and hm.imei='"+imei+"' and t.enddate is null";
		return jdbcTemplate.query(sql, new LiveVehicleStatusCacheRowMapper());
	}

	public LiveVehicleStatus getVehicleStatusCount(Long userId) {
		int onlineVehiclesCount = 0;
		int totalOfflineVehicleCount=0;
		try{
			String sql = "select (date_part('days',now()-lastupdatedat))>0 as lastupdatedatdiff, (date_part('days',now()-moduleupdatetime))>0 as moduleupdatetimediff  from" +
					" livevehiclestatus where tripid in (select id from trips where vehicleid " +
					"in (select id from vehicles where vehicleid in (select vehicleid from aclvehicle where userid="+userId+")  and deleted='f'));";
			List<VehicleStatusCount> vehicleStatusCountList = jdbcTemplate.query(sql, new VehicleStatusCountRowMapper());

			LOG.debug("OnLine/Offline vehicles count query is "+sql);

			if(vehicleStatusCountList == null){
				return null;
			}
			for(VehicleStatusCount vehicleStatusCount : vehicleStatusCountList){
				if(vehicleStatusCount.isLastupdatedatdiff() && vehicleStatusCount.isModuleupdatetimediff()){
					totalOfflineVehicleCount=totalOfflineVehicleCount+1;
				}else{
					onlineVehiclesCount=onlineVehiclesCount+1;
				}
			}
		} catch(Exception e){
			LOG.error("Error while processing offline and online count", e);
		}
		
		int offroadCount = getOffroadCount(userId);
		LiveVehicleStatus lvos = new LiveVehicleStatus(onlineVehiclesCount,totalOfflineVehicleCount, offroadCount);
		LOG.debug("\nTotal count of Online vehicle is : "+lvos.getOnLineCount()
				+"\nOffline Count is : "+lvos.getTotalOffLineCount()+" Offroad count : "+offroadCount+"\ntotal vehicles are "
				+(lvos.getOnLineCount()+lvos.getTotalOffLineCount()));
		return lvos;
	}

	public int getOffroadCount(Long userId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*)  from livevehiclestatus where tripid in (select id from trips where vehicleid ");
		sql.append("in (select id from vehicles where vehicleid in (select vehicleid from aclvehicle where userid=");
		sql.append(userId);
		sql.append(")  and deleted='f')) and isoffroad is true;");
		return jdbcTemplate.queryForInt(sql.toString());
	}
	
	public List<LiveVehicleStatus> selectAllForCacheByVehicleId(Long vehicleId) {
		String sql = "select hm.imei, lv.tripid, lv.cumulativedistance, lastupdatedat, lv.moduleupdatetime, gsmstrength, gpsstrength, lv.sqd, lv.sqg,"
				+" 	lv.vehiclelocation, batvolt, lv.isoffroad, cc, maxspeed, v.id as vehicleid, t.speedlimit, t.scheduledtrip, "
				+"  t.tripstartdate, t.enddate, t.driverid, 0 as digital1, 0 as digital2, 0 as pingcounter, 0 as gps_fix_information, v.displayname,"
				+"	lv.latestbuttonpressed, lv.isidle, lv.course "
				+"  from livevehiclestatus lv "
				+"  left join trips t on lv.tripid = t.id "
				+"  left join vehicles v on t.vehicleid= v.id "
				+"  left join hardwaremodules hm on v.imeiid=hm.id  "
				+" where t.active='t' and v.id="+vehicleId+" and t.enddate is null";
		return jdbcTemplate.query(sql, new LiveVehicleStatusCacheRowMapper());
	}

	
	public List<LiveVehicleStatus> selectAllForCacheByDriverId(Long driverId) {
		String sql = "select hm.imei, lv.tripid, lv.cumulativedistance, lastupdatedat, lv.moduleupdatetime, gsmstrength, gpsstrength, lv.sqd, lv.sqg,"
				+" 	lv.vehiclelocation, batvolt, lv.isoffroad, cc, maxspeed, v.id as vehicleid, t.speedlimit, t.scheduledtrip, "
				+"  t.tripstartdate, t.enddate, t.driverid, 0 as digital1, 0 as digital2, 0 as pingcounter, 0 as gps_fix_information, v.displayname,"
				+"	lv.latestbuttonpressed, lv.isidle, lv.course "
				+"  from livevehiclestatus lv "
				+"  left join trips t on lv.tripid = t.id "
				+"  left join vehicles v on t.vehicleid= v.id "
				+"  left join hardwaremodules hm on v.imeiid=hm.id  "
				+" where t.active='t' and t.driverid="+driverId+" and t.enddate is null";
		return jdbcTemplate.query(sql, new LiveVehicleStatusCacheRowMapper());
	}

	
	public List<LiveVehicleStatus> selectAllForCacheByTripId(Long tripId) {
		String sql = "select hm.imei, lv.tripid, lv.cumulativedistance, lastupdatedat, lv.moduleupdatetime, gsmstrength, gpsstrength, lv.sqd, lv.sqg,"
				+" 	lv.vehiclelocation, batvolt, lv.isoffroad, cc, maxspeed, v.id as vehicleid, t.speedlimit, t.scheduledtrip, "
				+"  t.tripstartdate, t.enddate, t.driverid, 0 as digital1, 0 as digital2, 0 as pingcounter, 0 as gps_fix_information, v.displayname,"
				+"	lv.latestbuttonpressed, lv.isidle, lv.course "
				+"  from livevehiclestatus lv "
				+"  left join trips t on lv.tripid = t.id "
				+"  left join vehicles v on t.vehicleid= v.id "
				+"  left join hardwaremodules hm on v.imeiid=hm.id  "
				+" where t.active='t' and t.id="+tripId+" and t.enddate is null";
		return jdbcTemplate.query(sql, new LiveVehicleStatusCacheRowMapper());
	}

}