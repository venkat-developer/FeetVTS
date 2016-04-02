package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.BusRoutes;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.BusRoutesRowMapper;
import com.i10n.db.idao.IBusRoutesDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class BusRoutesDaoImp implements IBusRoutesDAO {

	private JdbcTemplate jdbcTemplate;
    private DataFieldMaxValueIncrementer busrouteIdIncrementer;
	
		public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getBusrouteIdIncrementer() {
		return busrouteIdIncrementer;
	}

	public void setBusrouteIdIncrementer(
			DataFieldMaxValueIncrementer busrouteIdIncrementer) {
		this.busrouteIdIncrementer = busrouteIdIncrementer;
	}

		@Override
	public BusRoutes delete(BusRoutes busroutes) throws OperationNotSupportedException {
			
        return null;

	}
    
	@Override
	public BusRoutes insert(BusRoutes busroute) throws OperationNotSupportedException {
		Long id = busrouteIdIncrementer.nextLongValue();
		busroute.setId(new LongPrimaryKey(id));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
        busroute.setExpectedtime(sqlDate); 
        String sql = "insert into busroutes(id,busstopid,busstopsequence,busroutenumber,expectedtime,shiftnumber) values (?,?,?,?,?,?)";
		Object args []= new Object[] {busroute.getId().getId(),busroute.getBusstopid(),busroute.getBusstopsequence(),busroute.getBusroutenumber(),busroute.getExpectedtime(),busroute.getShiftnumber()};
		int types[] = new int[] {Types.BIGINT, Types.BIGINT,Types.INTEGER,Types.INTEGER,Types.TIMESTAMP,Types.INTEGER};
		jdbcTemplate.update(sql, args, types);
 		return busroute;
	}
	
    @Override
    public BusRoutes update(BusRoutes busroute) throws OperationNotSupportedException {
        return null;
        }
    @Override
    public List<BusRoutes> selectByPrimaryKey(LongPrimaryKey primaryKey) {
        String sql = "select a.id as id,a.expectedtime as expectedtime,a.shiftnumber as shiftnumber,a.busstopsequence as busstopsequence,a.busroutenumber as busroutenumber, b.id as busstopid, b.busstopname as busstopname from busroutes a, " +
        		"busstops b where a.id = ? and a.busstopid = b.id";
        Object[] values = new Object[]{primaryKey.getId()};
        int[] types = new int[]{Types.BIGINT};
        List<BusRoutes> busroute = jdbcTemplate.query(sql, values, types, new BusRoutesRowMapper());
        return (busroute == null || busroute.size() == 0)?null:busroute;
    }
	@Override
	public List<BusRoutes> selectAll() {
		String sql = "select * from busroutes";
		return jdbcTemplate.query(sql, new BusRoutesRowMapper());
	}
	
	@Override
	public List<BusRoutes> selectByBusstopId(Long busstopid){
		String sql = "select * from busroutes where busstopid = " + busstopid;
		return jdbcTemplate.query(sql, new BusRoutesRowMapper());
	}

	

}		

