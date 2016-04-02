package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.ACLReports;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.idao.IACLReportsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public class ACLReportsDaoImp implements IACLReportsDAO{

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public ACLReports delete(ACLReports entity)
			throws OperationNotSupportedException {
		String sql="delete from aclreports where vehicleid=?";
		Object args []= new Object[] {entity.getVehicleid()};
        int types[] = new int[] { Types.BIGINT};
        jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public ACLReports insert(ACLReports entity)
			throws OperationNotSupportedException {
		String sql = "insert into aclreports values(?,?)";
		Object args []= new Object[] {entity.getReportId(),entity.getVehicleid()};
        int types[] = new int[] { Types.BIGINT, Types.BIGINT};
        jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<ACLReports> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public long getVehicleIDFromItsName(String vehicleName){
		String sql = "select id from vehicles where displayname=?";
		Object args[] = new Object[] {vehicleName};
		int types[] = new int[] {Types.VARCHAR};
		long vehId = jdbcTemplate.queryForLong(sql, args, types);
		return vehId;
	}

	@Override
	public List<ACLReports> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ACLReports update(ACLReports entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
