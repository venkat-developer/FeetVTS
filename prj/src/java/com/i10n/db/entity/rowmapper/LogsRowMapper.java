package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Logs;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
public class LogsRowMapper implements RowMapper {
 @Override
    public Object mapRow(ResultSet rs, int arg1) throws SQLException {
        Logs newLog = new Logs(
        		new LongPrimaryKey(rs.getLong("id")),
        				rs.getLong("userid"),
        	    new Date(rs.getTimestamp("date").getTime()),
                rs.getString("ipaddress"),
                rs.getString("olddata"),
                rs.getString("newdata"));
                
        return newLog;
    

}
}
