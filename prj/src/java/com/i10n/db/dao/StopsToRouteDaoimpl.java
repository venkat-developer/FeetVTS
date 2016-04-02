package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.StopsToRoute;
import com.i10n.db.entity.primarykey.StopsToRoutePrimaryKey;
import com.i10n.db.entity.rowmapper.StopsToRouteRowMapper;
import com.i10n.db.idao.IStopsToRouteDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author roopa.kn
 * 
 */
@SuppressWarnings("unchecked")
public class StopsToRouteDaoimpl implements IStopsToRouteDAO {

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public StopsToRoute delete(StopsToRoute driver)
	throws OperationNotSupportedException {
		String sql = "delete from stopstoroute where stopid = ?";
		Object args[] = new Object[] { driver.getStopid() };
		int types[] = new int[] { Types.BIGINT };
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if (rowsDeleted < 1) {
			driver = null;
		}
		return driver;
	}

	@Override
	public StopsToRoute insert(StopsToRoute driverassigned)
	throws OperationNotSupportedException {
		String sql = "INSERT INTO stopstoroute(stopid, routeid)VALUES (?, ?)";
		Object args[] = new Object[] { driverassigned.getStopid(), driverassigned.getRouteid() };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT };
		jdbcTemplate.update(sql, args, types);
		return driverassigned;
	}

	@Override
	public List<StopsToRoute> selectAll() {
		String sql = "select * from stopstoroute";
		return jdbcTemplate.query(sql, new StopsToRouteRowMapper());
	}

	@Override
	public List<StopsToRoute> selectByPrimaryKey(StopsToRoutePrimaryKey primaryKey) {
		String sql = "select * from stopstoroute where stopid = "+ primaryKey.getStopid() + " and routeid= "+ primaryKey.getRouteid();
		return jdbcTemplate.query(sql, new StopsToRouteRowMapper());
	}

	public List<StopsToRoute> selectByRouteId(long routeid) {
		String sql = "select * from stopstoroute where routeid=?";
		Object[] args = new Object[]{routeid};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StopsToRouteRowMapper());
	}

	@Override
	public StopsToRoute update(StopsToRoute entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
