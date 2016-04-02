package com.i10n.db.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.LEDToBusStop;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.LEDToBusStopRowMapper;
import com.i10n.db.idao.ILEDToBusStopDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public class LEDToBusStopDAOImpl implements ILEDToBusStopDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Override
	public LEDToBusStop insert(LEDToBusStop entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LEDToBusStop delete(LEDToBusStop entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LEDToBusStop update(LEDToBusStop entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LEDToBusStop> selectAll() {
		String sql = "select * from ledtobusstop";
		return jdbcTemplate.query(sql, new LEDToBusStopRowMapper());
	}

	@Override
	public List<LEDToBusStop> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<LEDToBusStop> selectByDeviceId(String deviceId) {
		String sql = "select * from ledtobusstop where deviceid like '"+deviceId+"'";
		return jdbcTemplate.query(sql, new LEDToBusStopRowMapper());
	}
}
