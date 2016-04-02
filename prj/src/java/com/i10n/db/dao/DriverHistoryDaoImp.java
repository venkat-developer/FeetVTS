package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.DriverHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.DriverHistoryRowMapper;
import com.i10n.db.idao.IDriverHistoryDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class DriverHistoryDaoImp implements IDriverHistoryDAO{
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer driverhistoryIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getDriverhistoryIdIncrementer() {
		return driverhistoryIdIncrementer;
	}

	public void setDriverhistoryIdIncrementer(DataFieldMaxValueIncrementer driverhistoryIdIncrementer) {
		this.driverhistoryIdIncrementer = driverhistoryIdIncrementer;
	}

	@Override
	public DriverHistory delete(DriverHistory driver) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public DriverHistory insert(DriverHistory vehicle) throws OperationNotSupportedException {
		Long id = driverhistoryIdIncrementer.nextLongValue();
		vehicle.setDriverid(new LongPrimaryKey(id));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		vehicle.setUpdatedtime(sqlDate); 
		String sql = "insert into driverhistory(driverid,updatedtime,driverstatus,updatedbyuser) values (?,?,?,?)";
		Object args []= new Object[] {vehicle.getDriverid().getId(),vehicle.getUpdatedtime(),vehicle.getDriverstatus(),vehicle.getUpdatedbyuser()};
		int types[] = new int[] {Types.BIGINT, Types.TIMESTAMP,Types.INTEGER,Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return vehicle;
	}

	@Override
	public DriverHistory update(DriverHistory driver) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<DriverHistory> selectAll() {
		String sql = "select * from driverhistory";
		return jdbcTemplate.query(sql, new DriverHistoryRowMapper());
	}

	@Override
	public List<DriverHistory> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from driverhistory a, " +"users b,drivers c where a.driverid = ? and a.driverid=c.id and a.updatedbyuser = b.id ";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<DriverHistory> driver = jdbcTemplate.query(sql, values, types, new DriverHistoryRowMapper());
		return (driver== null || driver.size() == 0)?null:driver;
	}
}		

