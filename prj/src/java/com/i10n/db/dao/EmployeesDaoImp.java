package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Employees;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.EmployeesRowMapper;
import com.i10n.db.idao.IEmployeesDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class EmployeesDaoImp implements IEmployeesDAO  {

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer employeesIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public DataFieldMaxValueIncrementer getEmployeesIdIncrementer() {
		return employeesIdIncrementer;
	}
	
	public void setEmployeesIdIncrementer(DataFieldMaxValueIncrementer employeesIdIncrementer) {
		this.employeesIdIncrementer = employeesIdIncrementer;
	}
	
	@Override
	public Employees delete(Employees entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Employees insert(Employees emply) throws OperationNotSupportedException {
		final Long did = employeesIdIncrementer.nextLongValue();
		emply.setId(new LongPrimaryKey(did));
		String sql = "insert into employees (id, name ) values (?,?)";
		Object args []= new Object[] {emply.getId().getId(),emply.getName()};
		int types[] = new int[] { Types.BIGINT, Types.VARCHAR};
		jdbcTemplate.update(sql, args, types);
		return emply;

	}
	
	@Override
	public List<Employees> selectAll() {
		String sql = "select * from employees";
		return jdbcTemplate.query(sql, new EmployeesRowMapper());
	}
	
	@Override
	public List<Employees> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql="select id,name from employees where id=?";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<Employees> override = jdbcTemplate.query(sql, values, types, new EmployeesRowMapper());
		return (override == null || override.size() == 0)?null:override;
	}
	
	@Override
	public Employees update(Employees entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
