/**
 * 
 */
package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.VehicleToStudent;
import com.i10n.db.entity.primarykey.VehicleToStudentPrimaryKey;
import com.i10n.db.entity.rowmapper.VehicleToStudentRowMapper;
import com.i10n.db.idao.IVehicleToStudentDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author roopa.kn
 * 
 */
@SuppressWarnings("unchecked")
public class VehicleToStudentDaoimpl implements IVehicleToStudentDAO {

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public VehicleToStudent delete(VehicleToStudent vehicleToStudent)
	throws OperationNotSupportedException {
		String sql = "delete from vehicletostudent where vehicleid = ?";
		Object args[] = new Object[] { vehicleToStudent.getVehicleid() };
		int types[] = new int[] { Types.BIGINT };
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if (rowsDeleted < 1) {
			vehicleToStudent = null;
		}
		return vehicleToStudent;

	}

	@Override
	public VehicleToStudent insert(VehicleToStudent driverassigned)
	throws OperationNotSupportedException {
		String sql = "INSERT INTO vehicletostudent(vehicleid, studentid)VALUES (?, ?)";
		Object args[] = new Object[] { driverassigned.getVehicleid(), driverassigned.getStudentid() };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT };
		jdbcTemplate.update(sql, args, types);
		return driverassigned;
	}

	@Override
	public List<VehicleToStudent> selectAll() {
		String sql = "select * from vehicletostudent";
		return jdbcTemplate.query(sql, new VehicleToStudentRowMapper());
	}

	@Override
	public List<VehicleToStudent> selectByPrimaryKey(VehicleToStudentPrimaryKey primaryKey) {
		String sql = "select * from vehicletostudent where vehicleid = "+ primaryKey.getVehicleid() + " and studentid= "+ primaryKey.getStudentid();
		return jdbcTemplate.query(sql, new VehicleToStudentRowMapper());
	}

	@Override
	public VehicleToStudent update(VehicleToStudent entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VehicleToStudent> getVehicleAssignedStudents(long vehicleId) {
		String sql = "select * from vehicletostudent where vehicleid = ?";
		Object[] args = new Object[]{vehicleId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new VehicleToStudentRowMapper());
	}

}
