package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.BusStops;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.BusStopsRowMapper;
import com.i10n.db.idao.IBusStopsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class BusStopsDaoImp implements IBusStopsDAO{
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer busstopsIdIncrementer;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getBusstopsIdIncrementer() {
		return busstopsIdIncrementer;
	}

	public void setBusstopsIdIncrementer(
			DataFieldMaxValueIncrementer busstopsIdIncrementer) {
		this.busstopsIdIncrementer = busstopsIdIncrementer;
	}
	
	@Override
	public BusStops delete(BusStops busstops) throws OperationNotSupportedException {

		return null;
	}

	@Override
	public BusStops insert(BusStops busstop) throws OperationNotSupportedException {
		Long id = busstopsIdIncrementer.nextLongValue();
		busstop.setId(new LongPrimaryKey(id));
		String sql = "insert into busstops(id,busstopname,location) values (?,?,GeometryFromText('POINT (" 
			+ busstop.getLocation().getFirstPoint().getY() + " " + busstop.getLocation().getFirstPoint().getX() + ")',-1))";
		Object args []= new Object[] {busstop.getId().getId(),busstop.getBusstopName()};
		int types[] = new int[] {Types.BIGINT, Types.VARCHAR};
		jdbcTemplate.update(sql, args, types);
		return busstop;
	}

	@Override
	public BusStops update(BusStops busstop) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<BusStops> selectAll() {
		String sql = "select * from busstops";
		return jdbcTemplate.query(sql, new BusStopsRowMapper());
	}

	@Override
	public List<BusStops> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from busstops  where id =? ";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<BusStops> busstop = jdbcTemplate.query(sql, values, types, new BusStopsRowMapper());
		return (busstop == null || busstop.size() == 0)?null:busstop;
	}
}		

