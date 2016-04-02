package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import com.i10n.db.entity.ImeiSequenceMap;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.ImeiSequenceRowMapper;
import com.i10n.db.idao.IImeiSequenceMapDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * 
 * @author Dharmaraju V
 *
 */
@SuppressWarnings("unchecked")
public class ImeiSequenceMapDaoImpl implements IImeiSequenceMapDAO{

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public ImeiSequenceMap delete(ImeiSequenceMap entity)throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImeiSequenceMap insert(ImeiSequenceMap imeiseq) throws OperationNotSupportedException {
		String sql="insert into imeisequencemap (imei,seqno) values (?,?)";
		Object args[] = new Object[] {imeiseq.getImei(),imeiseq.getSequenceNumber()};
		int types[] = new int[] {Types.VARCHAR,Types.BIGINT };
		jdbcTemplate.update(sql, args, types);	
		return imeiseq;
	}

	@Override
	public List<ImeiSequenceMap> selectAll() {
		String sql="select * from imeisequencemap";
		return jdbcTemplate.query(sql, new ImeiSequenceRowMapper());
	}
	public List<ImeiSequenceMap> selectByIMEI(String imei){
		String sql="select * from imeisequencemap where imei=?";
		Object args[] = new Object[] {imei};
		int types[] = new int[] {Types.VARCHAR};
		return jdbcTemplate.query(sql, args, types, new ImeiSequenceRowMapper());
	} 

	@Override
	public List<ImeiSequenceMap> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImeiSequenceMap update(ImeiSequenceMap imeiseq) throws OperationNotSupportedException {
		String sql="update imeisequencemap set seqno=? where imei=? ";
		Object args[] = new Object[] {imeiseq.getSequenceNumber(),imeiseq.getImei()};
		int types[] = new int[] {Types.BIGINT,Types.VARCHAR };
		jdbcTemplate.update(sql, args, types);	
		return imeiseq;
	}

	@Override
	public ImeiSequenceMap saveOrUpdate(ImeiSequenceMap imeiseq) {
		List<ImeiSequenceMap> existing = selectByIMEI(imeiseq.getImei());

		if(existing.size()>0){
			try {
				update(imeiseq);
			} catch (OperationNotSupportedException e) {
				e.printStackTrace();
			}
		}else{
			try {
				insert(imeiseq);
			} catch (OperationNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return imeiseq;
	}

}
