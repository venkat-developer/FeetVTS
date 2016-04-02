/**
 * 
 */
package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.ImeiSequenceMap;

/**
 * @author joshua
 *
 */
public class ImeiSequenceRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		ImeiSequenceMap imeiseq=new ImeiSequenceMap(rs.getString("imei"),
												    rs.getLong("seqno")
														);
		
		return imeiseq;
	}

}
