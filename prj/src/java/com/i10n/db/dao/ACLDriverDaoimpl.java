package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.ACLDriver;
import com.i10n.db.entity.primarykey.ACLDriverPrimaryKey;
import com.i10n.db.entity.rowmapper.ACLDriverRowMapper;
import com.i10n.db.idao.IACLDriverDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author joshua
 * 
 */
public class ACLDriverDaoimpl implements IACLDriverDAO {

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<ACLDriver> selectByUserId(Long userId) {
		String sql = "select * from acldriver where userid = ?";
		Object args[] = new Object[] { userId };
		int types[] = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new ACLDriverRowMapper());
	}

	@Override
	public ACLDriver delete(ACLDriver driver) throws OperationNotSupportedException {
		String sql = "delete from acldriver where driverid = ?";
		Object args[] = new Object[] { driver.getDriverid() };
		int types[] = new int[] { Types.BIGINT };
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if (rowsDeleted < 1) {
			driver = null;
		}
		return driver;
	}

	@Override
	public ACLDriver insert(ACLDriver driverassigned) throws OperationNotSupportedException {
		String sql = "INSERT INTO acldriver(driverid, userid)VALUES (?, ?)";
		Object args[] = new Object[] { driverassigned.getDriverid(), driverassigned.getUserid() };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT };
		jdbcTemplate.update(sql, args, types);
		return driverassigned;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ACLDriver> selectAll() {
		String sql = "select * from acldriver";
		return jdbcTemplate.query(sql, new ACLDriverRowMapper());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ACLDriver> selectByPrimaryKey(ACLDriverPrimaryKey primaryKey) {
		String sql = "select * from aclvehicle where vehicleid = "+ primaryKey.getDriverid() + " and userid= "+ primaryKey.getUserid();
		return jdbcTemplate.query(sql, new ACLDriverRowMapper());
	}

	@Override
	public ACLDriver update(ACLDriver entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ACLDriver> SelectByUserId(Long userid) {
		// TODO Auto-generated method stub
		return null;
	}

}
