package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.OverrideFuelCalibration;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.OverrideFuelCalibrationRowMapper;
import com.i10n.db.idao.IOverrideFuelCalibrationDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class OverrideFuelCalibrationDaoImp  implements IOverrideFuelCalibrationDAO{

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer overrideIdIncrementer;


	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getOverrideIdIncrementer() {
		return overrideIdIncrementer;
	}

	public void setOverrideIdIncrementer(
			DataFieldMaxValueIncrementer overrideIdIncrementer) {
		this.overrideIdIncrementer = overrideIdIncrementer;
	}

	@Override
	public OverrideFuelCalibration delete(OverrideFuelCalibration entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OverrideFuelCalibration insert(OverrideFuelCalibration override)
	throws OperationNotSupportedException {
		Long id = overrideIdIncrementer.nextLongValue();
		override.setTripid(new LongPrimaryKey(id));
		String sql = "insert into overridefuelcalibration(tripid , calibrationid) values(?,?)";
		Object args []= new Object[] {override.getTripid().getId(),override.getCalibrationid()};
		int types[] = new int[] {Types.BIGINT, Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return override;
	}

	@Override
	public List<OverrideFuelCalibration> selectAll() {
		String sql = "select * from overridefuelcalibration";
		return jdbcTemplate.query(sql, new OverrideFuelCalibrationRowMapper());
	}

	@Override
	public List<OverrideFuelCalibration> selectByPrimaryKey(
			LongPrimaryKey primaryKey) {
		String sql="select o.tripid,o.calibrationid from Overridefuelcalibration o,trips t,fuelcalibrationdetails f " +
		" where o.tripid = ? and o.tripid = t.id and o.calibrationid=f.id ";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<OverrideFuelCalibration> override = jdbcTemplate.query(sql, values, types, new OverrideFuelCalibrationRowMapper());
		return (override == null || override.size() == 0)?null:override;

	}

	@Override
	public OverrideFuelCalibration update(OverrideFuelCalibration entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
