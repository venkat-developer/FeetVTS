package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.UserHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.UserHistoryRowMapper;
import com.i10n.db.idao.IUserHistoryDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class UserHistoryDaoImp implements IUserHistoryDAO{
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer userhistoryIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public DataFieldMaxValueIncrementer getUserhistoryIdIncrementer() {
		return userhistoryIdIncrementer;
	}

	public void setUserhistoryIdIncrementer(DataFieldMaxValueIncrementer userhistoryIdIncrementer) {
		this.userhistoryIdIncrementer = userhistoryIdIncrementer;
	}

	@Override
	public UserHistory delete(UserHistory user) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public UserHistory insert(UserHistory user) throws OperationNotSupportedException {
		Long id = userhistoryIdIncrementer.nextLongValue();
		user.setUserid(new LongPrimaryKey(id));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		user.setUpdatedat(sqlDate); 
		String sql = "insert into userhistory(userid,userstatus,updatedat) values (?,?,?)";
		Object args []= new Object[] {user.getUserid().getId(),user.getUserstatus(),user.getUpdatedat()};
		int types[] = new int[] {Types.BIGINT,Types.INTEGER,Types.TIMESTAMP};
		jdbcTemplate.update(sql, args, types);
		return user;
	}

	@Override
	public UserHistory update(UserHistory user) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<UserHistory> selectAll() {
		String sql = "select * from userhistory";
		return jdbcTemplate.query(sql, new UserHistoryRowMapper());
	}

	@Override
	public List<UserHistory> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from userhistory a, " +
		"users b where a.userid = ? and a.userid=b.id ";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<UserHistory> user = jdbcTemplate.query(sql, values, types, new UserHistoryRowMapper());
		return (user== null || user.size() == 0)?null:user;
	}
}