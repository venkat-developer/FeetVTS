package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.GeoFenceRegions;
import com.i10n.db.entity.primarykey.LongPrimaryKey;


public class GeoFenceRegionsRowMapper  implements RowMapper{  

    @Override
    public Object mapRow(ResultSet rs, int arg1) throws SQLException {
       PGgeometry geom = (PGgeometry)rs.getObject("polygon");
       Point p=new org.postgis.Point(12.971599, 77.594563);
       Geometry location = (Geometry)p;
      
       if(geom!=null){
    	   location=geom.getGeometry();
       }
        
        GeoFenceRegions newregion = new GeoFenceRegions(new LongPrimaryKey(rs.getLong("id")),
        		location,
                rs.getDouble("speed"),
                rs.getString("name"),
                rs.getLong("userid"),
                rs.getInt("shape"));
        return newregion;
    }
}