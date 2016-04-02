package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.ProvidersDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.ProvidersDetailsRowMapper;
import com.i10n.db.idao.IProvidersDetailsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class ProvidersDetailsDaoImp implements IProvidersDetailsDAO{

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer providersDetailsIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getProvidersDetailsIdIncrementer() {
		return providersDetailsIdIncrementer;
	}

	public void setProvidersDetailsIdIncrementer(DataFieldMaxValueIncrementer providersDetailsIdIncrementer) {
		this.providersDetailsIdIncrementer = providersDetailsIdIncrementer;
	}

	@Override
	public ProvidersDetails delete(ProvidersDetails entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProvidersDetails insert(ProvidersDetails provider)
	throws OperationNotSupportedException {
		final Long did = providersDetailsIdIncrementer.nextLongValue();
		provider.setId(new LongPrimaryKey(did));
		String sql = "insert into providerdetails (id, isget ,baseurl ,mobilenumberparamname,messageparamname ,multiplenumberdelimiter,  userparamname,passparamname ,creditcheckurl ) values (?,?,?,?,?,?,?,?,?)";
		Object args []= new Object[] {provider.getId().getId(),provider.isIsget(),provider.getBaseurl(),provider.getMobilenumberparamname(),
				provider.getMessageparamname(),provider.getMultiplenumberdelimiter(),provider.getUserparamname(),provider.getPassparamname(),provider.getCreditcheckurl()};
		int types[] = new int[] { Types.BIGINT, Types.BOOLEAN,  Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR};
		jdbcTemplate.update(sql, args, types);
		return provider;
	}
	
	@Override
	public List<ProvidersDetails> selectAll() {
		String sql = "select * from providerdetails";
		return jdbcTemplate.query(sql, new ProvidersDetailsRowMapper());
	}
	
	@Override
	public List<ProvidersDetails> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql="select a.id,a.isget ,a.baseurl ,a.mobilenumberparamname,a.messageparamname ,a.multiplenumberdelimiter,  a.userparamname,a.passparamname ,a.creditcheckurl "+" from providerdetails a,smsproviders b where a.id=? and a.id=b.providerid";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<ProvidersDetails> providersdetails = jdbcTemplate.query(sql, values, types, new ProvidersDetailsRowMapper());
		return (providersdetails == null || providersdetails.size() == 0)?null:providersdetails;

	}
	
	@Override
	public ProvidersDetails update(ProvidersDetails entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}


}