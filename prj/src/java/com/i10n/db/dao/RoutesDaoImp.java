package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Routes;
import com.i10n.db.entity.VehicleToRoute;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.RoutesRowMapper;
import com.i10n.db.entity.rowmapper.VehicleToRouteRowMapper;
import com.i10n.db.idao.IRouteDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class RoutesDaoImp implements IRouteDAO{
	private Logger LOG = Logger.getLogger(RoutesDaoImp.class);

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer routesIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setRoutesIdIncrementer(DataFieldMaxValueIncrementer bookIncrementer) {
		this.routesIdIncrementer = bookIncrementer;
	}

	public DataFieldMaxValueIncrementer getRoutesIdIncrementer() {
		return routesIdIncrementer;
	}

	@Override
	public Routes delete(Routes entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Routes insert(Routes entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	 public List<Routes> selectAll() {
	  StringBuffer sql = new StringBuffer();
	  sql.append("select *");
	  sql.append(",encode(routebitmap, 'hex') as routebitmaphex,encode(destinationbitmap, 'hex') as");
	  sql.append(" destinationbitmaphex , englishrouteanddestinationbitmap as ");
	  sql.append(" englishrouteanddestinationbitmaphex,localrouteanddestinationbitmap as");
	  sql.append(" localrouteanddestinationbitmaphex from routes");
	  LOG.debug("query to get all the routes is "+sql);
	  List<Routes> routeList=jdbcTemplate.query(sql.toString(), new RoutesRowMapper());
	  return routeList;
	 }

	 @Override
	 public List<Routes> selectByPrimaryKey(LongPrimaryKey primaryKey) {
	  StringBuffer sql = new StringBuffer();
	  sql.append("select *");
	  sql.append(",encode(routebitmap, 'hex') as routebitmaphex,encode(destinationbitmap, 'hex') as");
	  sql.append(" destinationbitmaphex , englishrouteanddestinationbitmap as ");
	  sql.append(" englishrouteanddestinationbitmaphex,localrouteanddestinationbitmap as");
	  sql.append(" localrouteanddestinationbitmaphex from routes where id=");
	  sql.append(primaryKey.getId());
	  return jdbcTemplate.query(sql.toString(), new RoutesRowMapper());
	 }
	@Override
	public Routes update(Routes entity) throws OperationNotSupportedException {
		String sql = "update routes set routename=?, startpoint=?, endpoint=?, starttime=?, endtime = ?, groupid = ? where id=?";
		Object args[] = new Object[] { entity.getRouteName(), entity.getStartPoint(), entity.getEndPoint(),
				entity.getStartTime(), entity.getEndTime(), entity.getId().getId() };
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIME, Types.TIME, Types.BIGINT };
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			entity = null;
		}
		return entity;
	}

	public List<VehicleToRoute> getRoutesForVehicle(long vehicleid){
		String sql = "select * from vehicletoroute where vehicleid=?";
		Object[] args = new Object[]{vehicleid};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new VehicleToRouteRowMapper());
	}

	public String getRouteName(long routeId) {
		String sql = "select routename from routes where id=?";
		Object[] args = new Object[]{routeId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.queryForObject(sql, args, types, String.class).toString();
	}
	/**
	 * Function to get the List of Existed Routes from the Routes Table
	 * @author Gobi
	 * @return list of Routes
	 */
	public List<Routes> getRouteNameList(long ownerid){
		 String sql ="select *,encode(routebitmap, 'hex') as routebitmaphex,encode(destinationbitmap, 'hex') " +
		 		"as destinationbitmaphex , englishrouteanddestinationbitmap as englishrouteanddestinationbitmaphex," +
		 		"localrouteanddestinationbitmap as localrouteanddestinationbitmaphex from routes where ownerid = "+ownerid;
		return jdbcTemplate.query(sql, new RoutesRowMapper());
	}

}



