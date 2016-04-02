package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.postgis.Point;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.GeoFenceRegions;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.GeoFenceRegionsCacheRowMapper;
import com.i10n.db.entity.rowmapper.GeoFenceRegionsRowMapper;
import com.i10n.db.idao.IGeoFenceRegionsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class GeoFenceRegionsDaoImp implements IGeoFenceRegionsDAO {
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer geoFenceRegionsIdIncrementer;

	public DataFieldMaxValueIncrementer getGeoFenceRegionsIdIncrementer() {
		return geoFenceRegionsIdIncrementer;
	}

	public void setGeoFenceRegionsIdIncrementer(DataFieldMaxValueIncrementer geoFenceRegionsIdIncrementer) {
		this.geoFenceRegionsIdIncrementer = geoFenceRegionsIdIncrementer;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public GeoFenceRegions delete(GeoFenceRegions entity) throws OperationNotSupportedException {
		String query = "delete from vehicle_geofenceregions where regionid=?";
		Object vargs[] = new Object[] { entity.getId().getId() };
		int vtypes[] = new int[] { Types.BIGINT };
		jdbcTemplate.update(query, vargs, vtypes);

		String sql= " delete from geofenceregions where id =?";
		Object args []= new Object[] {entity.getId().getId()};
		int types[] = new int[] {Types.BIGINT};
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if(rowsDeleted < 1){
			entity = null;
		}
		return entity;
	}

	@Override
	public GeoFenceRegions insert(GeoFenceRegions entity) throws OperationNotSupportedException {
		Long id = geoFenceRegionsIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "insert into geofenceregions(id,name,speed,userid,shape) values (?,?,?,?,?)";
		Object args[] = new Object[] { id, entity.getRegionName(), entity.getSpeed(), entity.getUserId(), entity.getShape() };
		int[] types = new int[] {Types.BIGINT, Types.VARCHAR, Types.DOUBLE, Types.BIGINT, Types.INTEGER};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<GeoFenceRegions> selectAll() {
		String sql="select * from geofenceregions";
		return jdbcTemplate.query(sql, new GeoFenceRegionsRowMapper());
	}

	public List<GeoFenceRegions> selectAllOwned() {
		String sql="select * from geofenceregions where userid  ="+SessionUtils.getCurrentlyLoggedInUser().getId();
		return jdbcTemplate.query(sql, new GeoFenceRegionsRowMapper());
	}

	public GeoFenceRegions updateRegion(GeoFenceRegions entity, Point[] points) throws OperationNotSupportedException {
		String query = "update geofenceregions set polygon = GeometryFromText('POLYGON((";
		for (int i = 0; i < points.length; i++)
			query += points[i].x + " " + points[i].y + ",";
		query += points[0].x + " " + points[0].y;
		query += "))'),  name=?,speed=?, shape=? where id=?;";
		Object args[] = new Object[] { entity.getRegionName(), entity.getSpeed(), entity.getShape(), entity.getId().getId() };
		jdbcTemplate.update(query, args);
		return entity;
	}

	@Override
	public List<GeoFenceRegions> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql ="select * from geofenceregions where id="+primaryKey.getId();
		return jdbcTemplate.query(sql, new GeoFenceRegionsRowMapper());
	}

	@Override
	public GeoFenceRegions update(GeoFenceRegions entity) throws OperationNotSupportedException {
		String sql="UPDATE geofenceregions   SET  name=?, speed=?, polygon="
				+ "GeometryFromText('POINT ("+ entity.getPolygon().getFirstPoint().getY()+ " "
				+ entity.getPolygon().getFirstPoint().getX()+ ")',-1)" + "WHERE id=?";
		Object args []= new Object[] {entity.getRegionName(),entity.getSpeed(),entity.getId().getId()};
		jdbcTemplate.update(sql, args);
		return entity;
	}

	public GeoFenceRegions getRegion(long Regionid) {
		GeoFenceRegions geofence=null;
		String sql ="select * from geofenceregions where id="+Regionid;
		List<GeoFenceRegions> list= jdbcTemplate.query(sql, new GeoFenceRegionsRowMapper());
		if(list.size() != 0){
			geofence=list.get(0);
		}
		return geofence;
	}

	public List<GeoFenceRegions> selectByRegionName(String regionName) {
		String sql = "select * from geofenceregions where name = '"+regionName+"' order by id desc limit 1";
		return jdbcTemplate.query(sql, new GeoFenceRegionsRowMapper());
	}
	public List<GeoFenceRegions> selectAllForCache() {
		String sql ="select vg.vehicleid, vg.regionid, vg.insideregion," +
				"gr.name, gr.speed, gr.userid, gr.shape, gr.polygon, gr.regiontype,gr.regioncode from vehicle_geofenceregions vg, geofenceregions gr " +
				"where vg.vehicleid in (select vehicleid from aclvehicle where userid=gr.userid) and vg.regionid=gr.id";
		return jdbcTemplate.query(sql, new GeoFenceRegionsCacheRowMapper());
	}

	public List<GeoFenceRegions> selectByVehicleIdForCache(Long vehicleId) {
		String sql = "select vg.vehicleid, vg.regionid, vg.insideregion," +
				"gr.name, gr.speed, gr.userid, gr.shape, gr.polygon, gr.regiontype,gr.regioncode from vehicle_geofenceregions vg, geofenceregions gr " +
				"where vg.vehicleid = "+vehicleId+" and vg.regionid=gr.id";
		return jdbcTemplate.query(sql, new GeoFenceRegionsCacheRowMapper());
	}
}