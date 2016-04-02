package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.UserCreditDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.UserCreditDetailsRowMapper;
import com.i10n.db.idao.IUserCreditDetailsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class UserCreditDetailsDaoImp implements IUserCreditDetailsDAO{
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer usercreditdetailsIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getUsercreditdetailsIdIncrementer() {
		return usercreditdetailsIdIncrementer;
	}

	public void setUsercreditdetailsIdIncrementer(DataFieldMaxValueIncrementer usercreditdetailsIdIncrementer) {
		this.usercreditdetailsIdIncrementer = usercreditdetailsIdIncrementer;
	}

	@Override
	public UserCreditDetails delete(UserCreditDetails user) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public UserCreditDetails insert(UserCreditDetails user) throws OperationNotSupportedException {
		Long id = usercreditdetailsIdIncrementer.nextLongValue();
		user.setUserid(new LongPrimaryKey(id));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		user.setLastcreditpurchasedate(sqlDate); 
		String sql = "insert into usercreditdetails(userid,totalcreditpurchased,creditused,lastcreditpurchasedate,lastupdatedbyemp) values (?,?,?,?,?)";
		Object args []= new Object[] {user.getUserid().getId(),user.getTotalcreditpurchased(),user.getCreditused(),user.getLastcreditpurchasedate(),user.getLastupdatedbyemp()};
		int types[] = new int[] {Types.BIGINT, Types.INTEGER,Types.INTEGER,Types.TIMESTAMP,Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return user;
	}

	@Override
	public UserCreditDetails update(UserCreditDetails user) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<UserCreditDetails> selectAll() {
		String sql = "select * from usercreditdetails";
		return jdbcTemplate.query(sql, new UserCreditDetailsRowMapper());
	}

	@Override
	public List<UserCreditDetails> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from usercreditdetails a, " +
		"users b,employees c where a.userid = ? and a.userid=b.id and a.lastupdatedbyemp = c.id ";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<UserCreditDetails> user = jdbcTemplate.query(sql, values, types, new UserCreditDetailsRowMapper());
		return ( user== null || user.size() == 0)?null:user;
	}
}		

