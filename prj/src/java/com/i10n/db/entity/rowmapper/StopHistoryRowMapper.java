
package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.StopHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class StopHistoryRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		StopHistory stopHistory = new StopHistory(new LongPrimaryKey(rs.getLong("id")), 
				rs.getLong("stopid"), 
				rs.getLong("vehicleid"), 
				rs.getLong("routeid"), 
				rs.getString("routescheduleid"),
				rs.getTime("expectedtime"), 
				rs.getTime("actualtime"), 
				rs.getFloat("estimateddistance"), 
				rs.getFloat("actualdistance"),
				rs.getBoolean("active"),
				rs.getInt("seqno"),
				rs.getLong("trackhistoryid"));
		return stopHistory;
	}

}
