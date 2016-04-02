
package com.i10n.db.entity.rowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.DigitalEvents;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class DigitalEventsRowMapper implements RowMapper {
 @Override
    public Object mapRow(ResultSet rs, int arg1) throws SQLException {
        DigitalEvents newDig = new DigitalEvents(new LongPrimaryKey(rs.getLong("id")),
                rs.getString("eventname"),
                rs.getInt("labelfortrue"),
                rs.getInt ("labelforfalse"));
        return newDig;
    

}
}