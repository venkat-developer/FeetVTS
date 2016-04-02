package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.SmsProviders;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.SmsProvidersRowMapper;
import com.i10n.db.idao.ISmsProvidersDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class SmsProvidersDaoImp  implements ISmsProvidersDAO{

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer smsIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getSmsIdIncrementer() {
		return smsIdIncrementer;
	}

	public void setSmsIdIncrementer(DataFieldMaxValueIncrementer smsIdIncrementer) {
		this.smsIdIncrementer = smsIdIncrementer;
	}

	@Override
	public SmsProviders delete(SmsProviders entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SmsProviders insert(SmsProviders sms)
	throws OperationNotSupportedException {
		final Long did = smsIdIncrementer.nextLongValue();
		sms.setProviderid(new LongPrimaryKey(did));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		sms.setLastsmssentat(sqlDate); 
		String sql = "insert into smsproviders (providerid,providername,multiplesmssupport,reportsupport,username , password,numberofsmssent,lastsmssentat,lastsmssenttomobile,lastsmstext) values (?,?,?,?,?,?,?,?,?,?)";
		Object args []= new Object[] {sms.getProviderid().getId(),sms.getProvidername(),sms.getMultiplesmssupport(),sms.getReportsupport(),sms.getUsername(),
				sms.getPassword(),sms.getNumberofsmssent(),sms.getLastsmssentat(),sms.getLastsmssenttomobile(),sms.getLastsmstext()};
		int types[] = new int[] { Types.BIGINT, Types.VARCHAR, Types.BOOLEAN, Types.BOOLEAN, Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.TIMESTAMP,Types.VARCHAR,Types.VARCHAR};
		jdbcTemplate.update(sql, args, types);
		return sms;
	}

	@Override
	public List<SmsProviders> selectAll() {
		String sql = "select * from smsproviders";
		return jdbcTemplate.query(sql, new SmsProvidersRowMapper());
	}

	@Override
	public List<SmsProviders> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql="select providerid,providername,multiplesmssupport,reportsupport,username , password,numberofsmssent,lastsmssentat,lastsmssenttomobile,lastsmstext "+" from smsproviders where providerid=?";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<SmsProviders> sms = jdbcTemplate.query(sql, values, types, new SmsProvidersRowMapper());
		return (sms == null || sms.size() == 0)?null:sms;

	}

	@Override
	public SmsProviders update(SmsProviders entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
