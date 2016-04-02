package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.DriverCreationAndStatusInfo;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.DriverCreationAndStatusInfoRowMapper;
import com.i10n.db.idao.IDriverCreationAndStatusInfoDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class DriverCreationAndStatusInfoDaoImp implements IDriverCreationAndStatusInfoDAO {

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer drivercreationandstatusinfoIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getDrivercreationandstatusinfoIdIncrementer() {
		return drivercreationandstatusinfoIdIncrementer;
	}

	public void setDrivercreationandstatusinfoIdIncrementer(DataFieldMaxValueIncrementer drivercreationandstatusinfoIdIncrementer) {
		this.drivercreationandstatusinfoIdIncrementer = drivercreationandstatusinfoIdIncrementer;
	}

	@Override
	public DriverCreationAndStatusInfo delete(DriverCreationAndStatusInfo driver) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public DriverCreationAndStatusInfo insert(DriverCreationAndStatusInfo driver) throws OperationNotSupportedException {
		Long id = drivercreationandstatusinfoIdIncrementer.nextLongValue();
		driver.setDriverid(new LongPrimaryKey(id));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		driver.setCreatedat(sqlDate); 
		String sql = "insert into drivercreationandstatusinfo(driverid,createdat,lastupdateduserid,status,createduserid,currentownerid) values (?,?,?,?,?,?)";
		Object args []= new Object[] {driver.getDriverid().getId(),driver.getCreatedat(),driver.getLastupdateduserid(),driver.getStatus(),driver.getCreateduserid(),driver.getCurrentownerid()};
		int types[] = new int[] {Types.BIGINT, Types.TIMESTAMP,Types.BIGINT,Types.INTEGER,Types.BIGINT,Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return driver;
	}

	@Override
	public DriverCreationAndStatusInfo update(DriverCreationAndStatusInfo driver) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<DriverCreationAndStatusInfo> selectAll() {
		String sql = "select * from drivercreationandstatusinfo";
		return jdbcTemplate.query(sql, new DriverCreationAndStatusInfoRowMapper());
	}

	@Override
	public List<DriverCreationAndStatusInfo> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select a.driverid,a.createdat,a.lastupdateduserid,a.status,a.createduserid,a.currentownerid from drivercreationandstatusinfo a,users b,drivers c " +
		" where a.driverid = ? and a.driverid = c.id and a.createduserid=b.id and a.currentownerid=b.id and a.lastupdateduserid=b.id";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<DriverCreationAndStatusInfo> Driver = jdbcTemplate.query(sql, values, types, new DriverCreationAndStatusInfoRowMapper());
		return (Driver == null || Driver.size() == 0)?null:Driver;
	}
}



