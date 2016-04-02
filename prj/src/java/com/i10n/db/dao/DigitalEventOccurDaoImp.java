package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.DigitalEventOccur;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.DigitalEventOccurRowMapper;
import com.i10n.db.idao.IDigitalEventOccurDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class DigitalEventOccurDaoImp implements IDigitalEventOccurDAO {
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer digitaleventoccurIdIncrementer;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public DataFieldMaxValueIncrementer getDigitaleventoccurIdIncrementer() {
		return digitaleventoccurIdIncrementer;
	}
	
	public void setDigitaleventoccurIdIncrementer(DataFieldMaxValueIncrementer digitaleventoccurIdIncrementer) {
		this.digitaleventoccurIdIncrementer = digitaleventoccurIdIncrementer;
	}
	
	@Override
	public DigitalEventOccur delete(DigitalEventOccur entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalEventOccur insert(DigitalEventOccur newdigitaloccur) throws OperationNotSupportedException {
		Long did = digitaleventoccurIdIncrementer.nextLongValue();
		newdigitaloccur.setTripid(new LongPrimaryKey(did));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		newdigitaloccur.setTruedate(sqlDate); 
		newdigitaloccur.setFalsedate(sqlDate);
		String sql = "insert into digitaleventoccurences( tripid,eventid,truedate,falsedate) values (?,?,?,?)";
		Object args []= new Object[] {newdigitaloccur.getTripid().getId(),newdigitaloccur.getEventid(),newdigitaloccur.getTruedate(),newdigitaloccur.getFalsedate()};
		int types[] = new int[] { Types.BIGINT, Types.BIGINT,  Types.TIMESTAMP, Types.TIMESTAMP};
		jdbcTemplate.update(sql, args, types);
		return newdigitaloccur;
	}

	@Override
	public DigitalEventOccur update(DigitalEventOccur entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DigitalEventOccur> selectAll() {
		String sql = "select * from digitaleventoccurences" ;
		return jdbcTemplate.query(sql, new DigitalEventOccurRowMapper());
	}
	
	@Override
	public List<DigitalEventOccur> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select a.tripid,a.eventid,a.truedate,a.falsedate from digitaleventoccurences a ,digitalevents b ,trips c where a.tripid=? and a.tripid=c.id and a.eventid=b.id " ;
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<DigitalEventOccur> newdig = jdbcTemplate.query(sql, values, types, new DigitalEventOccurRowMapper());
		return (newdig == null || newdig.size() == 0)?null:newdig;
	}
}

