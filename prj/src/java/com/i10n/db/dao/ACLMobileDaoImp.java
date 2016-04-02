package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.ACLMobile;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.ACLMobileRowMapper;
import com.i10n.db.idao.IACLMobileDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class ACLMobileDaoImp implements IACLMobileDAO{

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public ACLMobile delete(ACLMobile entity) throws OperationNotSupportedException {
		String sql="delete from aclmobile where vehicleid=?";
		Object args []= new Object[] {entity.getVehicleid()};
        int types[] = new int[] { Types.BIGINT};
        jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public ACLMobile insert(ACLMobile entity) throws OperationNotSupportedException {
		String sql = "insert into aclmobile values(?,?)";
		Object args []= new Object[] {entity.getMobileId(),entity.getVehicleid()};
        int types[] = new int[] { Types.BIGINT, Types.BIGINT};
        jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<ACLMobile> selectAll() {
		String sql = "select * from aclmobile";
		return jdbcTemplate.query(sql, new ACLMobileRowMapper());
	}

	public long getVehicleIDFromItsName(String vehicleName){
		String sql = "select id from vehicles where displayname=?";
		Object args[] = new Object[] {vehicleName};
		int types[] = new int[] {Types.VARCHAR};
		long vehId = jdbcTemplate.queryForLong(sql, args, types);
		return vehId;
	}
	
	@Override
	public List<ACLMobile> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ACLMobile update(ACLMobile entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Long> getMobileUserIds(Long vehicleId){
		String sql = "select mobileid from aclmobile where vehicleid=?";
		Object[] args = new Object[]{vehicleId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.queryForList(sql, args, types, Long.class);
	}
	public List<ACLMobile> selectByMobileId(Long mobileId) {
		String sql = "select * from aclmobile where mobileid = ?";
		Object args[] = new Object[] {mobileId};
		int types[] = new int[] {Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new ACLMobileRowMapper());
	}
}
