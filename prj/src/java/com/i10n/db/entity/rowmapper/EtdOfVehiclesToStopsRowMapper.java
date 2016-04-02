package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.EtdOfVehiclesToStops;

public class EtdOfVehiclesToStopsRowMapper implements RowMapper{
	
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    	EtdOfVehiclesToStops etdOfVehiclesToStop = new EtdOfVehiclesToStops(rs.getLong("vehicleid"),
    			rs.getLong("stopid"),
    			rs.getInt("etdminutes"));
        return etdOfVehiclesToStop;
    }

}
