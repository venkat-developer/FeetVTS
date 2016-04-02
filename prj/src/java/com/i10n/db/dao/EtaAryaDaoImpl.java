package com.i10n.db.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.EtaArya;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.EtaAryaRowMapper;
import com.i10n.db.idao.IEtaAryaDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public  class EtaAryaDaoImpl implements IEtaAryaDAO  {
	private static Logger LOG = Logger.getLogger(EtaAryaDaoImpl.class);

	private DataFieldMaxValueIncrementer etaAryaIdIncrementer;
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public void setEtaAryaIdIncrementer(
			DataFieldMaxValueIncrementer bookIncrementer) {
		this.etaAryaIdIncrementer = bookIncrementer;
	}

	public DataFieldMaxValueIncrementer getEtaAryaIdIncrementer() {
		return etaAryaIdIncrementer;
	}

	@SuppressWarnings("unchecked")
	public List<EtaArya> selectedStopIdForArya(long busStopId) throws SQLException {
		LOG.debug("SelectedStopIdForArya()");
		String sql ="select * from etaarya where stopid="+busStopId+" order by rec_time desc limit 1";
		LOG.debug("SQL:"+sql);
		return jdbcTemplate.query(sql,new EtaAryaRowMapper());
	}

	@Override
	public EtaArya insert(EtaArya entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EtaArya delete(EtaArya entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EtaArya update(EtaArya entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<EtaArya> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<EtaArya> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
