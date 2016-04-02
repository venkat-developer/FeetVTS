package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.IdlePoints;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class IdlePointsRowMapper implements RowMapper{

       @Override
       public Object mapRow(ResultSet rs, int currentRowNumber) throws SQLException {
               PGgeometry geom = (PGgeometry)rs.getObject("idlelocation");
               return new IdlePoints(new LongPrimaryKey(rs.getLong("id")),
                                                       rs.getLong("tripid"),
                                                       geom.getGeometry(),
                                                       new Date(rs.getTimestamp("starttime").getTime()),
                                                       new Date(rs.getTimestamp("endtime").getTime()),
                                                       rs.getString("locationname"));
       }
}