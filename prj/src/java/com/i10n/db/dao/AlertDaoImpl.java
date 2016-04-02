package com.i10n.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.AlertOrViolationRowMapper;
import com.i10n.db.idao.IAlertDao;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class AlertDaoImpl implements IAlertDao {
	
	private static Logger LOG = Logger.getLogger(AlertDaoImpl.class);

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer alertIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setAlertIdIncrementer(
			DataFieldMaxValueIncrementer alertIncrementer) {
		this.alertIdIncrementer = alertIncrementer;
	}

	public DataFieldMaxValueIncrementer getAlertIdIncrementer() {
		return alertIdIncrementer;
	}

	@Override
	public AlertOrViolation delete(AlertOrViolation entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AlertOrViolation insert(AlertOrViolation entity) throws OperationNotSupportedException {
			
		entity.setId(new LongPrimaryKey(alertIdIncrementer.nextLongValue()));

		StringBuffer alertInsertQuery = new StringBuffer();
		alertInsertQuery.append("INSERT INTO alertorviolation(id, vehicleid, driverid, isdisplayed,alerttype, alerttypevalue, alerttime,"); 
		alertInsertQuery.append("occurredat, alertlocation, alertlocationreferenceid, alertlocationtext)");
		alertInsertQuery.append("VALUES (");
		alertInsertQuery.append(entity.getId().getId());
		alertInsertQuery.append(" , ");
		alertInsertQuery.append(entity.getVehicleId());
		alertInsertQuery.append(" , ");
		alertInsertQuery.append(entity.getDriverId());
		alertInsertQuery.append(" , ");
		alertInsertQuery.append(false);
		alertInsertQuery.append(" , ");
		alertInsertQuery.append(entity.getAlertType().getValue());
		alertInsertQuery.append(" , '");
		if(entity.getAlertTypeValue() == null){
			alertInsertQuery.append("");
		} else{
			alertInsertQuery.append(entity.getAlertTypeValue());
		}
		alertInsertQuery.append("' , '");
		alertInsertQuery.append(DateUtils.convertJavaDateToSQLDate(entity.getAlertTime()));
		alertInsertQuery.append("' , '");
		alertInsertQuery.append(DateUtils.convertJavaDateToSQLDate(entity.getOccurredAt()));
		alertInsertQuery.append("' , ");
		alertInsertQuery.append("GeometryFromText('POINT ("+ entity.getAlertLocation().getFirstPoint().x + " " + entity.getAlertLocation().getFirstPoint().y +")',-1)");
		alertInsertQuery.append(" , ");
		alertInsertQuery.append(entity.getAlertLocationReferenceId());
		alertInsertQuery.append(" , '");
		alertInsertQuery.append(entity.getAlertLocationText());
		alertInsertQuery.append("' )");

		jdbcTemplate.execute(alertInsertQuery.toString());

		return entity;
	}

	@Override
	public List<AlertOrViolation> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AlertOrViolation> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AlertOrViolation update(AlertOrViolation entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AlertOrViolation> selectByUserIdAndDuration(long userId, Date startDate, Date endDate) {
		List<AlertOrViolation> alertOrViolationList = new ArrayList<AlertOrViolation>();		

		StringBuffer selectByUserIdAndDuration = new StringBuffer();
		selectByUserIdAndDuration.append("select * from alertorviolation where vehicleid in (select vehicleid from aclvehicle where userid = ");
		selectByUserIdAndDuration.append(userId);
		selectByUserIdAndDuration.append(") and alerttime > '");
		selectByUserIdAndDuration.append(DateUtils.convertJavaDateToSQLDate(startDate));
		selectByUserIdAndDuration.append("' and alerttime < '");
		selectByUserIdAndDuration.append(DateUtils.convertJavaDateToSQLDate(endDate));
		selectByUserIdAndDuration.append("' order by alerttime desc");
		LOG.debug("SelectByUserIdAndDuration Query : "+selectByUserIdAndDuration.toString());

		alertOrViolationList = jdbcTemplate.query(selectByUserIdAndDuration.toString(), new AlertOrViolationRowMapper());
			
		return alertOrViolationList;
	}
	
	/**
	 * Selects first fifteen violation based the details provided like userid,start date and end date
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AlertOrViolation> selectByUserIdAndDurationWithLimit(long userId, Date startDate, Date endDate) {
		List<AlertOrViolation> alertOrViolationList = new ArrayList<AlertOrViolation>();		

		StringBuffer selectByUserIdAndDuration = new StringBuffer();
		selectByUserIdAndDuration.append("select * from alertorviolation where vehicleid in (select vehicleid from aclvehicle where userid = ");
		selectByUserIdAndDuration.append(userId);
		selectByUserIdAndDuration.append(") and alerttime > '");
		selectByUserIdAndDuration.append(DateUtils.convertJavaDateToSQLDate(startDate));
		selectByUserIdAndDuration.append("' and alerttime < '");
		selectByUserIdAndDuration.append(DateUtils.convertJavaDateToSQLDate(endDate));
		selectByUserIdAndDuration.append("' order by alerttime desc limit 15");
		LOG.debug("SelectByUserIdAndDuration Query : "+selectByUserIdAndDuration.toString());

		alertOrViolationList = jdbcTemplate.query(selectByUserIdAndDuration.toString(), new AlertOrViolationRowMapper());
			
		return alertOrViolationList;
	}

	
	/**
	 * List of alert left un displayed on UI 
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AlertOrViolation> selectUndisplayedAlertsForUser(long userId) {
		List<AlertOrViolation> alertOrViolationList = new ArrayList<AlertOrViolation>();		
		StringBuffer selectUndisplayedAlertsForUser = new StringBuffer();
		selectUndisplayedAlertsForUser.append("select * from alertorviolation where vehicleid in (select vehicleid from aclvehicle where userid = ");
		selectUndisplayedAlertsForUser.append(userId);
		selectUndisplayedAlertsForUser.append(") and isdisplayed = false ");
		selectUndisplayedAlertsForUser.append(" order by alerttime desc");
		LOG.debug("SelectUndisplayedAlertsForUser Query : "+selectUndisplayedAlertsForUser.toString());

		alertOrViolationList = jdbcTemplate.query(selectUndisplayedAlertsForUser.toString(), new AlertOrViolationRowMapper());

		return alertOrViolationList;
	}

	public void updateAlertsNotifiedStatus(List<AlertOrViolation> alertOrViolationList) {
			
		for(AlertOrViolation alert : alertOrViolationList){
			StringBuffer updateAlertsNotifiedStatus = new StringBuffer();
			updateAlertsNotifiedStatus.append("update alertorviolation set isdisplayed = true where id = ");
			updateAlertsNotifiedStatus.append(alert.getId().getId());
			LOG.debug("UpdateAlertsNotifiedStatus Query : "+updateAlertsNotifiedStatus.toString());
			jdbcTemplate.update(updateAlertsNotifiedStatus.toString());
		}

	}

	@SuppressWarnings("unchecked")
	public List<AlertOrViolation> selectLiveAlerts(Long userId) {
		List<AlertOrViolation> alertOrViolationList = new ArrayList<AlertOrViolation>();		
		StringBuffer selectLiveAlertsForUser = new StringBuffer();
		selectLiveAlertsForUser.append("select * from alertorviolation where vehicleid in (select vehicleid from aclvehicle where userid = ");
		selectLiveAlertsForUser.append(userId);
		selectLiveAlertsForUser.append(") order by alerttime desc");
		LOG.debug("SelectLiveAlertsForUser Query : "+selectLiveAlertsForUser.toString());

		alertOrViolationList = jdbcTemplate.query(selectLiveAlertsForUser.toString(), new AlertOrViolationRowMapper());
			
		return alertOrViolationList;
	}
	
	/**
	 * Update the location string and its reference with the map value of the same
	 * @param alert
	 */
	public void updateAlertLocation(AlertOrViolation alert) {
		StringBuffer updateAlertLocation = new StringBuffer();
		updateAlertLocation.append("update alertorviolation set alertlocationtext = '");
		updateAlertLocation.append(alert.getAlertLocationText());
		updateAlertLocation.append("', alertlocationreferenceid = ");
		updateAlertLocation.append(alert.getAlertLocationReferenceId());
		updateAlertLocation.append(" where id = ");
		updateAlertLocation.append(alert.getId().getId());
		jdbcTemplate.update(updateAlertLocation.toString());
	}

	@SuppressWarnings("unchecked")
	public List<AlertOrViolation> selectAlertsByUserId(long userId) {
		List<AlertOrViolation> alertOrViolationList = new ArrayList<AlertOrViolation>();		
		StringBuffer selectLiveAlertsForUser = new StringBuffer();
		selectLiveAlertsForUser.append("select distinct(vehicleid) as vehicleid, id,alertlocation,driverid,alerttime,occurredat,alerttype,alerttypevalue," +
				"alertlocationreferenceid,alertlocationtext,isdisplayed,isusernotified from alertorviolation where vehicleid in (select vehicleid from aclvehicle where userid = ");
		selectLiveAlertsForUser.append(userId);
		selectLiveAlertsForUser.append(") and isdisplayed = false order by alerttime desc limit 5");
		LOG.debug("SelectLiveAlertsForUser Query : "+selectLiveAlertsForUser.toString());

		alertOrViolationList = jdbcTemplate.query(selectLiveAlertsForUser.toString(), new AlertOrViolationRowMapper());

		return alertOrViolationList;
	}

	public long getMaxId() {
		String sql = "select max (id) as max_id from alertorviolation";
		return jdbcTemplate.queryForLong(sql);
	}

}
