package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.ACLVehicle;
import com.i10n.db.entity.primarykey.ACLVehiclePrimaryKey;
import com.i10n.db.entity.rowmapper.ACLVehicleRowMapper;
import com.i10n.db.idao.IACLVehicleDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class ACLVehicleDaoImp implements IACLVehicleDAO {

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public ACLVehicle delete(ACLVehicle entity) throws OperationNotSupportedException {
		String sql="delete from aclvehicle where vehicleid=?";
		Object args []= new Object[] {entity.getVehicleid()};
		int types[] = new int[] { Types.BIGINT};
		int rowsAffected = jdbcTemplate.update(sql, args, types);
		if(rowsAffected != 0){
			return entity;
		}
		return null;
	}

	@Override
	public ACLVehicle insert(ACLVehicle entity) throws OperationNotSupportedException {

		String sql = "insert into aclvehicle values(?,?)";
		Object args []= new Object[] {entity.getVehicleid(),entity.getUserid()};
		int types[] = new int[] { Types.BIGINT, Types.BIGINT};
		int rowsAffected = jdbcTemplate.update(sql, args, types);
		if(rowsAffected == 1){
			return entity;
		}
		return null;

	}
	public Long selectVehicleCountForUsers(Long userId){

		String sql = "select count(vehicleid) from aclvehicle  where userid="+userId+" ";
		Long  count=jdbcTemplate.queryForLong(sql);

		return count;
	}

	@Override
	public ACLVehicle update(ACLVehicle entity) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<ACLVehicle> selectAll() {
		String sql = "select * from aclvehicle order by userid";
		return jdbcTemplate.query(sql, new ACLVehicleRowMapper());
	}

	@Override
	public List<ACLVehicle> selectByPrimaryKey(ACLVehiclePrimaryKey primaryKey) {
		String sql = "select * from aclvehicle where vehicleid = " + primaryKey.getVehicleid() +
				" and userid= " + primaryKey.getUserid();
		return jdbcTemplate.query(sql, new ACLVehicleRowMapper());
	}

	@Override
	public List<ACLVehicle> selectByUserId(Long userId){
		String sql = "select * from aclvehicle where userid = " + userId;
		return jdbcTemplate.query(sql, new ACLVehicleRowMapper());
	}
	public List<Long> getUserIds(Long vehicleId){
		String sql = "select userid from aclvehicle where vehicleid=?";
		Object[] args = new Object[]{vehicleId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.queryForList(sql, args, types, Long.class);
	}

}		
