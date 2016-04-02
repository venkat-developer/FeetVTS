package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Students;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.StudentsRowMapper;
import com.i10n.db.idao.IStudentsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class StudentsDaoImp implements IStudentsDAO{

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer studentsIdIncrementer;

	/**
	 * @return the studentsIdIncrementer
	 */
	public DataFieldMaxValueIncrementer getStudentsIdIncrementer() {
		return studentsIdIncrementer;
	}

	/**
	 * @param studentsIdIncrementer 
	 */
	public void setStudentsIdIncrementer(DataFieldMaxValueIncrementer studentsIdIncrementer) {
		this.studentsIdIncrementer = studentsIdIncrementer;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Students delete(Students entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Students insert(Students entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Students> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Students> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from students where id=?";
		Object[] args = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StudentsRowMapper());
	}

	@Override
	public Students update(Students entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Students> selectByStudentIdAndRouteId(Long studentId, long routeId) {
		String sql = "select * from students where id=? and routeid = ?";
		Object[] args = new Object[]{studentId, routeId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StudentsRowMapper());	
	}

}
