package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.HardwareModuleRowMapper;
import com.i10n.db.idao.IHardwareModuleDAO;
import com.i10n.dbCacheManager.LoadHardwareModuleDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class HardwareModuleDaoImp implements IHardwareModuleDAO {
	private static Logger LOG = Logger.getLogger(HardwareModuleDaoImp.class);


	private JdbcTemplate jdbcTemplate;

	private DataFieldMaxValueIncrementer hwModuleIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getHwModuleIdIncrementer() {
		return hwModuleIdIncrementer;
	}

	public void setHwModuleIdIncrementer(DataFieldMaxValueIncrementer hwModuleIdIncrementer) {
		this.hwModuleIdIncrementer = hwModuleIdIncrementer;
	}

	@Override
	public HardwareModule delete(HardwareModule entity) throws OperationNotSupportedException {
		String sql = "update hardwaremodules set deleted = true where id = ? and id not in (select imeiid from vehicles where deleted = false )";
		Object args[] = new Object[] { entity.getId().getId() };
		int types[] = new int[] { Types.BIGINT };
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if (rowsDeleted < 1) {
			entity = null;
		}
		jdbcTemplate.execute("NOTIFY update");
		LOG.info("Notifying the hardwaremodules");
		return entity;
	}
	/**
	 * insert function will create a connection with database and will insert the values into the hardwaremodules table
	 * @return Object of HardwareModule
	 */
	@Override
	public HardwareModule insert(HardwareModule hwModule)throws OperationNotSupportedException {
		Long id = hwModuleIdIncrementer.nextLongValue();
		hwModule.setId(new LongPrimaryKey(id));
		Long ownerId = SessionUtils.getCurrentlyLoggedInUser().getId();
		String sql = "insert into hardwaremodules (imei, moduleversion, modulecreatedat, statuslastupdatedat, "+
				"lastupdatedempid, modulestatus,firmwareversion,ownerid,mobilenumber,simid,simprovider) values (?,?,?,?,?,?,?,?,?,?,?)";
		Object args[] = new Object[] {hwModule.getImei(), hwModule.getModuleVersion(), hwModule.getCreatedAt(),
				hwModule.getStatusLastUpdatedAt(), hwModule.getLastUpdatedEmpIp(), hwModule.getStatus(), hwModule.getFirmwareversion(),
				ownerId,hwModule.getMobileNumber(),hwModule.getSimId(),hwModule.getSimProvider() };
		int types[] = new int[] {Types.VARCHAR, Types.REAL, Types.TIMESTAMP, Types.TIMESTAMP, Types.BIGINT, Types.INTEGER, Types.REAL, 
				Types.BIGINT,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR};
		jdbcTemplate.update(sql, args, types);
		jdbcTemplate.execute("NOTIFY hardwaremodules");
		LOG.info("Notifying the hardwaremodules");
		LOG.debug("INserted Hardware Details are "+hwModule.toString());
		return hwModule;
	}

	@Override
	public List<HardwareModule> selectAll() {
		Long ownerId = SessionUtils.getCurrentlyLoggedInUser().getId();
		String sql = "select * from hardwaremodules where deleted = false and ownerid in ( select id from users where owner_id = ? and role = 0 or id = ?)";
		Object args[] = new Object[] {ownerId,ownerId};
		int types[] = new int[] {Types.BIGINT, Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new HardwareModuleRowMapper());
	}

	public List<HardwareModule> loadAll() {
		String sql = "select * from hardwaremodules where deleted = false";
		return jdbcTemplate.query(sql, new HardwareModuleRowMapper());
	}

	public List<HardwareModule> selectForFuelCalAll() {
		Long ownerId = SessionUtils.getCurrentlyLoggedInUser().getId();
		String sql = "select * from hardwaremodules where deleted = false and fuelcal=false and ownerid in ( select "+ 
				"id from users where owner_id = ? and role = 0 or id = ?)";
		Object args[] = new Object[] {ownerId,ownerId};
		int types[] = new int[] {Types.BIGINT, Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new HardwareModuleRowMapper());
	}

	@Override
	public List<HardwareModule> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from hardwaremodules where id = "
				+ primaryKey.getId();
		return jdbcTemplate.query(sql, new HardwareModuleRowMapper());
	}

	@Override
	public HardwareModule update(HardwareModule entity)
			throws OperationNotSupportedException {
		String sql = "update hardwaremodules set imei=?, moduleversion=?, statuslastupdatedat=?, "
				+ "lastupdatedempid=?, firmwareversion = ?,mobilenumber = ?,simid = ?,simprovider = ? where id=?";
		Object args[] = new Object[] { entity.getImei(), entity.getModuleVersion(), entity.getStatusLastUpdatedAt(),
				entity.getLastUpdatedEmpIp(), entity.getFirmwareversion(), entity.getMobileNumber(),entity.getSimId(),entity.getSimProvider(),entity.getId().getId() };
		int types[] = new int[] { Types.VARCHAR, Types.FLOAT, Types.DATE, Types.BIGINT, Types.FLOAT,Types.BIGINT,Types.VARCHAR,Types.VARCHAR,Types.BIGINT };
		LOG.debug("sql : "+sql);
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			entity = null;
		}
		jdbcTemplate.execute("NOTIFY update");
		LOG.info("Notifying the hardwaremodules");
		return entity;
	}

	public List<HardwareModule> getIMEI() {
		String sql = " select * from hardwaremodules where id NOT IN(select imeiid from vehicles where deleted = false ) and deleted = false";
		return jdbcTemplate.query(sql, new HardwareModuleRowMapper());
	}

	public List<HardwareModule> selectFromFuelCalibrationValues() {
		String sql = " select * from hardwaremodules where id in (select imeiid from vehicles where id in (select vehicleid from trips where id in (select tripid from fuelcalibrationvalues)));";
		return jdbcTemplate.query(sql, new HardwareModuleRowMapper());
	}

	public void addfuelcal(long imeiid){
		String sql="update hardwaremodules set fuelcal=true where id="+imeiid;
		jdbcTemplate.update(sql);
	}

	public void deletefuelcal(long imeiid){
		String sql="update hardwaremodules set fuelcal=false where id="+imeiid;
		jdbcTemplate.update(sql);
		jdbcTemplate.execute("NOTIFY update");
		LOG.info("Notifying the hardwaremodules");
	}

	public HardwareModule checkIMEI(String imei) {
		HardwareModule hw = null;
		String sql = "select * from hardwaremodules where imei=? and deleted = false";
		Object args[] = new Object[] { imei };
		int types[] = new int[] { Types.VARCHAR };
		List<HardwareModule> result = jdbcTemplate.query(sql, args, types, new HardwareModuleRowMapper());
		if (result != null && result.size() == 1) {
			hw = result.get(0);
		}
		return hw;
	}
	/**
	 * Function checks for the duplication of SIM ID
	 * @param simId
	 * @return true if same SIM ID is detected
	 * @return false if same SIM ID is not detected
	 */
	public static boolean checkSimId(String simId) {
		for (Long hardwareId : LoadHardwareModuleDetails.getInstance().cacheHardwareModules.keySet()){
			HardwareModule hardwareModule=LoadHardwareModuleDetails.getInstance().retrieve(hardwareId);
			LOG.debug("Hardware Id in checkSimId : "+hardwareId +" is equal to  : "+hardwareModule.getId().getId());
			if(hardwareModule.getSimId()!=null){
				LOG.debug("HardwareModule.getSimId : "+hardwareModule.getSimId() +" SIM ID  : "+simId);
				if(hardwareModule.getSimId().equals(simId)){
					LOG.debug("HardwareModule.getSimId : "+hardwareModule.getSimId() +" SIM ID  is equal to : "+simId);
					if(hardwareModule.getSimId().equals("0")){
						return false;
					}
					return true;
				}	

			}
		}
		return false;
	}
	/**
	 * function to checks for conflict between oldSimId and NewSimId
	 * @param newSimId
	 * @param oldSimId
	 * @return true if same SIM ID is detected 
	 * @return false if same SIM ID is not detected
	 */
	public static boolean checkSimId(String oldSimId,String newSimId){
		for (Long hardwareId : LoadHardwareModuleDetails.getInstance().cacheHardwareModules.keySet()){
			HardwareModule hardwareModule=LoadHardwareModuleDetails.getInstance().retrieve(hardwareId);
			if(hardwareModule.getSimId()!=null){
				if(newSimId.equalsIgnoreCase(oldSimId)){
					//SimId Matches...
					LOG.error("SimID : "+newSimId+" already exist.");
					return false;
				}
				//If SimId Doesn't Matches
				else if(oldSimId != newSimId){
					// SIM ID Matches with already existed SIM ID returns an Error Message
					if(hardwareModule.getSimId().equals(newSimId)){
						if(hardwareModule.getSimId().equals("0")){
							return false;
						}
						return true;
					}	
				}
			}
		}
		return false;
	}
	/**
	 * function checks for the duplication of Mobile Number
	 * @param mobileNumber
	 * @return true if same MobileNumber is detected
	 * @return false if same MobileNumber is not detected
	 */

	public static boolean checkMobileNumber(String mobileNumber){
		LOG.debug("Mobile Number : "+mobileNumber);
		for (Long hardwareId : LoadHardwareModuleDetails.getInstance().cacheHardwareModules.keySet()){
			HardwareModule hardwareModule = LoadHardwareModuleDetails.getInstance().retrieve(hardwareId);
			LOG.debug("hardwareId : "+hardwareId);
			if(hardwareModule.getMobileNumber()!=null){
				LOG.debug("hardwareModule.getMobileNumber() : "+hardwareModule.getMobileNumber()+" - mobileNumber : "+mobileNumber);
				if(hardwareModule.getMobileNumber().equals(mobileNumber)){
					LOG.debug("hardwareModule.getMobileNumber() : "+hardwareModule.getMobileNumber()+" - is equal to : "+mobileNumber);
					if(hardwareModule.getMobileNumber().equalsIgnoreCase("0")){
						LOG.debug("MobileNumber : "+hardwareModule.getMobileNumber());
						return false;
					}
					return true;
				}

			}
		}
		return false;
	}
	/**
	 * checks conflict between the Old and New MobileNumbers
	 * @param oldImei
	 * @param newImei
	 * @return true if MobileNumbers are same
	 */

	public static boolean checkMobileNumber(Long oldMobileNumber,Long newMobileNumber){
		for (Long hardwareId : LoadHardwareModuleDetails.getInstance().cacheHardwareModules.keySet()){
			HardwareModule hardwareModule = LoadHardwareModuleDetails.getInstance().retrieve(hardwareId);
			if(hardwareModule.getMobileNumber()!=null){
				//MobileNumber Matches
				if(newMobileNumber.equals(oldMobileNumber)){
					LOG.debug("MOBILE NUMBER MATCHES");
					return false;
				}
				//Mobile Number Doesn't Matches....
				else if(oldMobileNumber!=newMobileNumber){
					//Matches with already existed returns an Error Message
					if(hardwareModule.getMobileNumber().equals(newMobileNumber)){
						if(hardwareModule.getMobileNumber().equalsIgnoreCase("0")){
							return false;
						}
						return true;
					}

				}

			}
		}
		return false;
	}
	/**
	 * checks the New Inserted IMEI with the old one...to avoid conflicts
	 * @param oldImei
	 * @param newImei
	 * @return true if IMEI are same
	 * @return false if IMEI are not same
	 */

	public static boolean checkImei(String oldImei,String newImei){
		for(Long hardwareId : LoadHardwareModuleDetails.getInstance().cacheHardwareModules.keySet()){
			HardwareModule hardwareModule = LoadHardwareModuleDetails.getInstance().retrieve(hardwareId);
			if(hardwareModule.getImei()!=null){
				//IMEI Matches.....
				if(newImei.equalsIgnoreCase(oldImei)){
					LOG.error("IMEI "+newImei+" already exist");
					return false;
				}
				//IMEI doesn't Matches....
				else if(newImei != oldImei){
					if(hardwareModule.getImei().equalsIgnoreCase(newImei)){
						return true;
					}
				}


			}
		}
		return false;
	}
}
