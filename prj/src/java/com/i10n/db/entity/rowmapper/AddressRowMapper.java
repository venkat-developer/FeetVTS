/**
 * 
 */
package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Address;

/**
 * @author joshua
 *
 */
public class AddressRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		PGgeometry geom = (PGgeometry)rs.getObject("latlon_point");
		
		Address address=new Address( rs.getDouble("lat"),
                                     rs.getDouble("lng"),
				                    rs.getString("line1"),
				                    rs.getString("line2"),
				                    rs.getString("line3"),
				                    rs.getString("line4"),
				                    rs.getString("street"),
				                    rs.getString("state"),
				                    rs.getString("country"),
				                   
				                    geom.getGeometry());
	 // TODO Auto-generated method stub
		return address;
	}

}
