package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.ShiftTimings;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.ShiftTimingsRowMapper;
import com.i10n.db.idao.IShiftTimingsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class ShiftTimingsDaoImp implements IShiftTimingsDAO{

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer shiftTimingsIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getShiftTimingsIdIncrementer() {
		return shiftTimingsIdIncrementer;
	}

	public void setShiftTimingsIdIncrementer(
			DataFieldMaxValueIncrementer shiftTimingsIdIncrementer) {
		this.shiftTimingsIdIncrementer = shiftTimingsIdIncrementer;
	}

	@Override
	public ShiftTimings delete(ShiftTimings entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShiftTimings insert(ShiftTimings shift)
	throws OperationNotSupportedException {
		return shift;
		// TODO Auto-generated method stub

	}

	@Override
	public List<ShiftTimings> selectAll() {
		String sql = "select * from shifttimings";
		return jdbcTemplate.query(sql, new ShiftTimingsRowMapper());

	}

	@Override
	public List<ShiftTimings> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		return null;
	}

	@Override
	public ShiftTimings update(ShiftTimings entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ShiftTimings> getShiftTimings(Long vehicleId) {
		String sql = "select * from shifttimings where vehicleid = ?";
		Object[] values = new Object[]{vehicleId};
		int [] types = new int[]{Types.BIGINT};
		List<ShiftTimings> shift = jdbcTemplate.query(sql,values,types, new ShiftTimingsRowMapper());
		return ((shift == null || shift.size() == 0)?null:shift);
	}
}
