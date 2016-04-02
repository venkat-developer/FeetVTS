package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleToBusRouteAssociation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
	public class VehicleToBusRouteAssociationRowMapper implements RowMapper {
	 @Override
	    public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		 VehicleToBusRouteAssociation newDig = new VehicleToBusRouteAssociation(new LongPrimaryKey(rs.getLong("id")),
				 rs.getString("busroutenumber"),
	              rs.getLong("vehicleid"),
	              rs.getInt("lastcrossedstopsequence"),
	              rs.getInt("currentshift"),
                   rs.getInt("initialdelayduration")

	                );
	        return newDig;

}

	

}