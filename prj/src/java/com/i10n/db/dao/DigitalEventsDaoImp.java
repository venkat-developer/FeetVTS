package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.DigitalEvents;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.DigitalEventsRowMapper;
import com.i10n.db.idao.IDigitalEventsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class DigitalEventsDaoImp implements IDigitalEventsDAO {
	
    private JdbcTemplate jdbcTemplate;
    private DataFieldMaxValueIncrementer digitalIdIncrementer;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DataFieldMaxValueIncrementer getDigitalIdIncrementer() {
        return digitalIdIncrementer;
    }

    public void setDigitalIdIncrementer(DataFieldMaxValueIncrementer digitalIdIncrementer) {
        this.digitalIdIncrementer = digitalIdIncrementer;
    }
 
	@Override
	public DigitalEvents delete(DigitalEvents entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalEvents insert(DigitalEvents newdigital) throws OperationNotSupportedException {
		final Long did = digitalIdIncrementer.nextLongValue();
        newdigital.setId(new LongPrimaryKey(did));
        String sql = "insert into digitalevents (id,eventname,labelfortrue,labelforfalse) values (?,?,?,?)";
        Object args []= new Object[] {newdigital.getId().getId(),newdigital.getEventname(),newdigital.getLabelfortrue(),newdigital.getLabelforfalse()};
        int types[] = new int[] { Types.BIGINT, Types.VARCHAR,  Types.INTEGER, Types.INTEGER};
        jdbcTemplate.update(sql, args, types);
        return newdigital;
    }

	@Override
	public List<DigitalEvents> selectAll() {
		String sql = "select * from digitalevents";
        return jdbcTemplate.query(sql, new DigitalEventsRowMapper());
	}

	@Override
	public List<DigitalEvents> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from digitalevents where id =? ";
        Object[] values = new Object[]{primaryKey.getId()};
        int[] types = new int[]{Types.BIGINT};
         List<DigitalEvents> newdig = jdbcTemplate.query(sql, values, types, new DigitalEventsRowMapper());
         return (newdig == null ||newdig .size() == 0)?null:newdig;
     }

	@Override
	public DigitalEvents update(DigitalEvents entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}