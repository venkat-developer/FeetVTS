package com.i10n.db.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.StopDeviation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.StopDeviationRowMapper;
import com.i10n.db.idao.IStopDeviationDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class StopDeviationDaoImp implements IStopDeviationDAO{
	
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(StopDeviationDaoImp.class);
	private JdbcTemplate jdbcTemplate;

	private DataFieldMaxValueIncrementer StopDeviationIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getStopDeviationIdIncrementer() {
		return StopDeviationIdIncrementer;
	}

	public void setStopDeviationIdIncrementer(DataFieldMaxValueIncrementer StopDeviationIdIncrementer) {
		this.StopDeviationIdIncrementer = StopDeviationIdIncrementer;
	}

	@Override
	public StopDeviation delete(StopDeviation entity)
	throws OperationNotSupportedException {
		String sql = "delete from violation_stopdeviation where id="+entity.getId().getId();
		jdbcTemplate.update(sql);
		return entity;
	}

	@Override
	public StopDeviation insert(StopDeviation entity)
	throws OperationNotSupportedException {
		Long id = StopDeviationIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "INSERT INTO violation_stopdeviation(id, vehicleid, vehiclename, stopid, missedstopname,"
			+ "expectedtime,routeid,occurredat) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[] { id, entity.getVehicleId(), entity.getVehicleName(), entity.getStopId(),
				entity.getMissedStopName(), entity.getExpectedTime(), entity.getRouteId(), entity.getOccurredat()};
		jdbcTemplate.update(sql, args);
		return entity;
	}

	@Override
	public List<StopDeviation> selectAll() {
		String sql = "select * from violation_stopdeviation";
		return jdbcTemplate.query(sql, new StopDeviationRowMapper());
	}

	@Override
	public List<StopDeviation> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from violation_stopdeviation where id = "+primaryKey.getId();
		return jdbcTemplate.query(sql, new StopDeviationRowMapper());
	}

	@Override
	public StopDeviation update(StopDeviation entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StopDeviation> selectByUserId(long userId, Date startDate, Date endDate) {
		String sql = "select * from violation_stopdeviation where vehicleid in(select vehicleid from aclvehicle where userid = ? )"
				+ " and occurredat >= ? and occurredat <= ? order by occurredat";
		Object[] args = new Object[]{userId, startDate, endDate};
		int[] types = new int[]{Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP};
		return jdbcTemplate.query(sql, args, types, new StopDeviationRowMapper());
	}

	public List<StopDeviation> selectByVehicleId(long vehicleId, Date startDate, Date endDate) {
		String sql = "select * from violation_stopdeviation where vehicleid = ? and occurredat >= ? and occurredat <= ? order by occurredat";
		Object[] args = new Object[]{vehicleId, startDate, endDate};
		int[] types = new int[]{Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP};
		return jdbcTemplate.query(sql, args, types, new StopDeviationRowMapper());
	}
}
