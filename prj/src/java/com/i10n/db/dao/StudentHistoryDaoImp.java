package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.StudentHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.StudentHistoryRowMapper;
import com.i10n.db.idao.IStudentHistoryDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class StudentHistoryDaoImp implements IStudentHistoryDAO{

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(StudentHistoryDaoImp.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer studentHistoryIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setStudentHistoryIdIncrementer(DataFieldMaxValueIncrementer bookIncrementer) {
		this.studentHistoryIdIncrementer = bookIncrementer;
	}

	public DataFieldMaxValueIncrementer getStudentHistoryIdIncrementer() {
		return studentHistoryIdIncrementer;
	}

	@Override
	public StudentHistory delete(StudentHistory entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StudentHistory insert(StudentHistory entity)
	throws OperationNotSupportedException {
		Long studentHistoryId = studentHistoryIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(studentHistoryId));
		String sql = "INSERT INTO studenthistory(id, studentid, stopid, vehicleid, routeid, expectedtime, smssenttime,"+ 
		"alertmins)VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[]{entity.getId().getId(),entity.getStudentId(),entity.getStopId(),entity.getVehicleId(),entity.getRouteId(),
				entity.getExpectedTime(),entity.getSmsSentTime(),entity.getAlertMin()};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP,Types.INTEGER};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<StudentHistory> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StudentHistory> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StudentHistory update(StudentHistory entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StudentHistory> getStopSubscribedStudentForActualTimeUpdate(long stopId, long routeId, long vehicleId) {
		String sql = "select * from studenthistory where stopid = ? and routeid = ? and vehicleid = ? and actualtime is null";
		Object[] args = new Object []{stopId,routeId,vehicleId};
		int[] types = new int[] {Types.BIGINT,Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StudentHistoryRowMapper());
	}

	public List<StudentHistory> updateActualTimeForRouteOnRouteDeactivation(long routeId, long vehicleId) {
		String sql = "select * from studenthistory where routeid = ? and vehicleid = ? and actualtime is null";
		Object[] args = new Object []{routeId,vehicleId};
		int[] types = new int[] {Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StudentHistoryRowMapper());
	}

	public List<StudentHistory> checkForSMSSent(long vehicleId, Long studentId, long stopId, long routeId) {
		String sql = "select * from studenthistory where studentid = ? and stopid = ? and routeid = ? and vehicleid = ? and "+
		"actualtime is null";
		Object[] args = new Object []{studentId,stopId,routeId,vehicleId};
		int[] types = new int[] {Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StudentHistoryRowMapper());
	}

	public List<StudentHistory> getLatestStudentHistoryEntry(long vehicleId, Long studentId, long stopId, long routeId) {
		String sql = "select * from studenthistory where studentid = ? and stopid = ? and routeid = ? and vehicleid = ? and "+
		"actualtime is null order by id desc limit 1";
		Object[] args = new Object []{studentId,stopId,routeId,vehicleId};
		int[] types = new int[] {Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StudentHistoryRowMapper());
	}

}
