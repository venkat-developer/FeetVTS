package com.i10n.db.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.VehicleHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.VehicleHistoryRowMapper;
import com.i10n.db.idao.IVehicleHistoryDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class VehicleHistoryDaoImp implements IVehicleHistoryDAO{
	private static Logger LOG = Logger.getLogger(VehicleHistoryDaoImp.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer vehiclehistoryIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getVehiclehistoryIdIncrementer() {
		return vehiclehistoryIdIncrementer;
	}

	public void setVehiclehistoryIdIncrementer(DataFieldMaxValueIncrementer vehiclehistoryIdIncrementer) {
		this.vehiclehistoryIdIncrementer = vehiclehistoryIdIncrementer;
	}

	@Override
	public VehicleHistory delete(VehicleHistory vehicle) throws OperationNotSupportedException {
		return null;
	}

	public Vehicle insert(Vehicle vehicle) throws OperationNotSupportedException {
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		StringBuffer queryForVehicleHistoryInsert = new StringBuffer();
		queryForVehicleHistoryInsert.append("insert into vehiclehistory(vehicleid,imei,updatedtime,updatedbyuser) values (");
		queryForVehicleHistoryInsert.append(vehicle.getId().getId());
		queryForVehicleHistoryInsert.append(",");
		queryForVehicleHistoryInsert.append("'");
		queryForVehicleHistoryInsert.append(vehicle.getImei());
		queryForVehicleHistoryInsert.append("'");
		queryForVehicleHistoryInsert.append(",");
		queryForVehicleHistoryInsert.append("'");
		queryForVehicleHistoryInsert.append(sqlDate);
		queryForVehicleHistoryInsert.append("'");
		queryForVehicleHistoryInsert.append(",");
		queryForVehicleHistoryInsert.append("'");
		queryForVehicleHistoryInsert.append(SessionUtils.getCurrentlyLoggedInUser().getLogin());
		queryForVehicleHistoryInsert.append("'");
		queryForVehicleHistoryInsert.append(")");
		LOG.debug("Veihcle History "+queryForVehicleHistoryInsert.toString());
		jdbcTemplate.update(queryForVehicleHistoryInsert.toString());
		return vehicle;
	}
	/**
	 * this insertion query will be used for UI  
	 * @param vehicle
	 * @param batterychanged
	 * @param fusechange
	 * @param vehicleattended
	 * @return
	 * @throws OperationNotSupportedException
	 */
	public Vehicle updateVehicleHistory(Vehicle vehicle, boolean batterychanged, boolean fusechange, String vehicleattended) 
			throws OperationNotSupportedException {
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		LOG.debug("SQL Data Before Insertion "+sqlDate);
		StringBuffer vehicleHistoryInsertQuery = new StringBuffer(); 
		vehicleHistoryInsertQuery.append("insert into vehiclehistory");
		vehicleHistoryInsertQuery.append("(vehicleattended,battrychanged,fusechanged,updatedtime,vehicleid,imei,updatedbyuser");
		vehicleHistoryInsertQuery.append(")");
		vehicleHistoryInsertQuery.append("values('");
		vehicleHistoryInsertQuery.append(vehicleattended);
		vehicleHistoryInsertQuery.append("',");
		vehicleHistoryInsertQuery.append(batterychanged);
		vehicleHistoryInsertQuery.append(",");
		vehicleHistoryInsertQuery.append(fusechange);
		vehicleHistoryInsertQuery.append(",'");
		vehicleHistoryInsertQuery.append(sqlDate);
		vehicleHistoryInsertQuery.append("',");
		vehicleHistoryInsertQuery.append(vehicle.getId().getId());
		vehicleHistoryInsertQuery.append(",'");
		vehicleHistoryInsertQuery.append(vehicle.getImei());
		vehicleHistoryInsertQuery.append("','");
		vehicleHistoryInsertQuery.append(SessionUtils.getCurrentlyLoggedInUser().getLogin());
		vehicleHistoryInsertQuery.append("')");
		LOG.info("Veihcle Insertion Query "+vehicleHistoryInsertQuery.toString());
		jdbcTemplate.update(vehicleHistoryInsertQuery.toString());
		return vehicle;
	}

	@Override
	public VehicleHistory update(VehicleHistory vehicle) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<VehicleHistory> selectAll() {
		StringBuffer selectAllVehicles = new StringBuffer();
		selectAllVehicles.append("select * from vehiclehistory");
		return jdbcTemplate.query(selectAllVehicles.toString(), new VehicleHistoryRowMapper());
	}


	@Override
	public List<VehicleHistory> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		StringBuffer selectByPrimaryKey = new StringBuffer();
		selectByPrimaryKey.append("select a.vehicleid as vehicleid,a.updatedtime as updatedtime,a.vehiclestatus as vehiclestatus,a.updatedbyuser as updatedbyuser from vehiclehistory a, " +
				"users b,vehicles c where a.vehicleid = ");
		selectByPrimaryKey.append(primaryKey.getId());
		selectByPrimaryKey.append(" and a.updatedbyuser = b.id and a.vehicleid=c.id");
		return jdbcTemplate.query(selectByPrimaryKey.toString(), new VehicleHistoryRowMapper());
	}

	@Override
	public VehicleHistory insert(VehicleHistory entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VehicleHistory> getHistoryForSelectedVehicle(Long vehicleId) {
		LOG.info("Vehicle ID "+vehicleId);
		StringBuffer historyForSelectedVehicle = new StringBuffer();
		historyForSelectedVehicle.append("select * from vehiclehistory where vehicleid=");
		historyForSelectedVehicle.append(vehicleId);
		historyForSelectedVehicle.append(" order by updatedtime DESC limit 1");
		LOG.info("Selected Vehicle LIST "+historyForSelectedVehicle.toString());
		LOG.info("Vehicle IDDDDDD Selected "+vehicleId);
		return jdbcTemplate.query(historyForSelectedVehicle.toString(), new VehicleHistoryRowMapper());
	}

	public List<VehicleHistory> getHistoryForSelectedVehicleForUIDisplay(Long vehicleId,Date startDate, Date endDate) {
		LOG.info("Vehicle ID "+vehicleId);
		StringBuffer historyForSelectedVehicleForUIDisplay = new StringBuffer();
		historyForSelectedVehicleForUIDisplay.append("select * from vehiclehistory where vehicleid=");
		historyForSelectedVehicleForUIDisplay.append(vehicleId);
		historyForSelectedVehicleForUIDisplay.append(" and updatedtime > '");
		historyForSelectedVehicleForUIDisplay.append(new Timestamp(startDate.getTime()));
		historyForSelectedVehicleForUIDisplay.append("'  and updatedtime < '");
		historyForSelectedVehicleForUIDisplay.append(new Timestamp(endDate.getTime()));
		historyForSelectedVehicleForUIDisplay.append("' order by updatedtime DESC");
		LOG.info("Selected Vehicle LIST "+historyForSelectedVehicleForUIDisplay.toString());
		LOG.info("Vehicle IDDDDDD Selected "+vehicleId);
		return jdbcTemplate.query(historyForSelectedVehicleForUIDisplay.toString(), new VehicleHistoryRowMapper());
	}
}