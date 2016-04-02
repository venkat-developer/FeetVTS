/**
 * 
 */
package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.VehicleToRoute;
import com.i10n.db.entity.primarykey.VehicleToRoutePrimaryKey;
import com.i10n.db.entity.rowmapper.VehicleToRouteRowMapper;
import com.i10n.db.idao.IVehicleToRouteDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author roopa.kn
 * 
 */
@SuppressWarnings("unchecked")
public class VehicleToRouteDaoimpl implements IVehicleToRouteDAO {

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public VehicleToRoute delete(VehicleToRoute driver)
	throws OperationNotSupportedException {
		String sql = "delete from vehicletoroute where vehicleid = ?";
		Object args[] = new Object[] { driver.getVehicleId() };
		int types[] = new int[] { Types.BIGINT };
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if (rowsDeleted < 1) {
			driver = null;
		}
		return driver;
	}

	@Override
	public VehicleToRoute insert(VehicleToRoute driverassigned)
	throws OperationNotSupportedException {
		String sql = "INSERT INTO vehicletoroute(vehicleid, routeid)VALUES (?, ?)";
		Object args[] = new Object[] { driverassigned.getVehicleId(), driverassigned.getRouteId() };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT };
		jdbcTemplate.update(sql, args, types);
		return driverassigned;
	}

	@Override
	public List<VehicleToRoute> selectAll() {
		String sql = "select * from vehicletoroute";
		return jdbcTemplate.query(sql, new VehicleToRouteRowMapper());
	}

	public List<VehicleToRoute> selectByRouteId(long routeId) {
		String sql = "select * from vehicletoroute where routeid=?";
		Object[] args = new Object[]{routeId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new VehicleToRouteRowMapper());
	}

	@Override
	public List<VehicleToRoute> selectByPrimaryKey(VehicleToRoutePrimaryKey primaryKey) {
		String sql = "select * from vehicletoroute where vehicleid = "+ primaryKey.getVehicleId() + " and routeid= "+ primaryKey.getRouteId();
		return jdbcTemplate.query(sql, new VehicleToRouteRowMapper());
	}

	@Override
	public VehicleToRoute update(VehicleToRoute entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VehicleToRoute> getRoutesOfVehicle(long vehicleId) {
		String sql = "select * from vehicletoroute where vehicleid = ?";
		Object[] args = new Object[]{vehicleId};
		int[] types = new int[]{Types.BIGINT};
		List<VehicleToRoute> vehicleToRouteList = jdbcTemplate.query(sql,args,types, new VehicleToRouteRowMapper());
		return ((vehicleToRouteList.size() == 0)?null : vehicleToRouteList);
	}

}
