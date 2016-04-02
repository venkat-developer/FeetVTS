package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.ACLAlerts;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.ACLAlertsRowMapper;
import com.i10n.db.idao.IACLAlertsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public class ACLAlertsDAOImp implements IACLAlertsDAO{
	private Logger LOG =Logger.getLogger(ACLAlertsDAOImp.class);

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public ACLAlerts delete(ACLAlerts entity)
			throws OperationNotSupportedException {
		String sql="delete from aclalerts where vehicleid=?";
		Object args []= new Object[] {entity.getVehicleId()};
		int types[] = new int[] { Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public ACLAlerts insert(ACLAlerts entity)
			throws OperationNotSupportedException {
		String sql = "insert into aclalerts values(?,?)";
		LOG.info("ACLAlerts insert query is "+sql+" alertUserId is "+entity.getalertUserId()+" , Vehicle Id is "+entity.getVehicleId());
		Object args []= new Object[] {entity.getalertUserId(),entity.getVehicleId()};
		int types[] = new int[] { Types.BIGINT, Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List<ACLAlerts> selectAll() {
		String sql = "select * from aclalerts";
		return jdbcTemplate.query(sql, new ACLAlertsRowMapper());
	}

	public long getVehicleIDFromItsName(String vehicleName){
		String sql = "select id from vehicles where displayname=?";
		Object args[] = new Object[] {vehicleName};
		int types[] = new int[] {Types.VARCHAR};
		long vehId = jdbcTemplate.queryForLong(sql, args, types);
		return vehId;
	}

	@Override
	public List<ACLAlerts> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ACLAlerts update(ACLAlerts entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getAlertUserIds(Long vehicleId){
		String sql = "select alertuserid from aclalerts where vehicleid=?";
		Object[] args = new Object[]{vehicleId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.queryForList(sql, args, types, Long.class);
	}
	@SuppressWarnings("unchecked")
	public List<ACLAlerts> selectByAlertUserId(Long alertuserId) {
		LOG.info("AlertuserId in selectByAlertUserId is "+alertuserId);
		String sql = "select * from aclalerts where alertuserid=?";
		Object[] args = new Object[]{alertuserId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new ACLAlertsRowMapper());
	}
}
