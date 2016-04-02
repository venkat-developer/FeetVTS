package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.UserFeatureList;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.UserFeatureListRowMapper;
import com.i10n.db.idao.IUserFeatureListDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class UserFeatureListDaoImp  implements IUserFeatureListDAO{
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer userFeatureListIdIncrementer;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public DataFieldMaxValueIncrementer getUserFeatureListIdIncrementer() {
		return userFeatureListIdIncrementer;
	}
	
	public void setUserFeatureListIdIncrementer(DataFieldMaxValueIncrementer userFeatureListIdIncrementer) {
		this.userFeatureListIdIncrementer = userFeatureListIdIncrementer;
	}
	
	@Override
	public UserFeatureList delete(UserFeatureList entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public UserFeatureList insert(UserFeatureList newuser)
	throws OperationNotSupportedException {
		final Long did = userFeatureListIdIncrementer.nextLongValue();
		newuser.setUserid(new LongPrimaryKey(did));
		String sql = "insert into userfeaturelist (userid,featureid) values (?,?)";
		Object args []= new Object[] {newuser.getUserid().getId(),newuser.getFeatureid()};
		int types[] = new int[] { Types.BIGINT, Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return newuser;
	}
	
	@Override
	public List<UserFeatureList> selectAll() {
		String sql = "select * from userfeaturelist";
		return jdbcTemplate.query(sql, new UserFeatureListRowMapper());
	}
	
	@Override
	public List<UserFeatureList> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql="select a.userid,a.featureid from userfeaturelist a,users b where a.userid=? and a.userid=b.id";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<UserFeatureList> userfeaturelist = jdbcTemplate.query(sql, values, types, new UserFeatureListRowMapper());
		return (userfeaturelist == null || userfeaturelist.size() == 0)?null:userfeaturelist;
	}
	
	@Override
	public UserFeatureList update(UserFeatureList entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
