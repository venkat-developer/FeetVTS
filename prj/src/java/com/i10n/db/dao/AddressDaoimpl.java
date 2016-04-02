package com.i10n.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.AddressRowMapper;
import com.i10n.db.idao.IAddressDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author joshua
 *
 */
public class AddressDaoimpl implements IAddressDAO {

	private static Logger LOG = Logger.getLogger(AddressDaoimpl.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer addressIdIncrementer;
	public DataFieldMaxValueIncrementer getAddressIdIncrementer() {
		return addressIdIncrementer;
	}

	public void setAddressIdIncrementer(
			DataFieldMaxValueIncrementer addressIdIncrementer) {
		this.addressIdIncrementer = addressIdIncrementer;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Address> fetchLocationfromDB(Double x, Double y) {
		List<Address> addressList = new ArrayList<Address>();

		double fromX=Math.floor(((x*1000)/100))/10;
		double toX=Math.ceil(((x*1000)/100))/10;
		double fromY=Math.floor(((y*1000)/100))/10;
		double toY = Math.ceil(((y*1000)/100))/10;

		int radius = 1000; // 1 KM		

		String query = "SELECT * FROM (select * from address where lat < "+toX+ "  and lat >"+ fromX +" and" +
				" lng  < "+toY+" and lng > "+fromY+") addr"
				+ " WHERE st_dwithin(transform(setsrid(addr.latlon_point,4326),26986), transform(GeomFromText('POINT("
				+ y + " " + x + ")',4326),26986)," + radius + ") " + " order by st_distance(transform(setsri" +
				"d(addr.latlon_point,4326),26986), transform(GeomFromText('POINT(" + y + " " + x + ")',4326),26986)) " + " limit 1";

		LOG.debug("Address fetch query \n : "+query);

		Date startTime = new Date();
		addressList = jdbcTemplate.query(query, new AddressRowMapper());
		if(addressList != null && addressList.size() > 0 ){
			LOG.debug("Address fetch took "+ (new Date().getTime() - startTime.getTime())+" with match");
		}else{
			LOG.warn("Address fetch took "+ (new Date().getTime() - startTime.getTime())+" without match");
		}
		return addressList;
	}

	@Override
	public Address delete(Address entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Address insert(Address address) throws OperationNotSupportedException {
		Long id = addressIdIncrementer.nextLongValue();
		address.setId(id);
		StringBuffer sql=new StringBuffer();
		sql.append("insert into address (line1, line2, line3, line4, street, state, country, lat, lng, id, latlon_point) ");
		sql.append(" Values('");
		if(address.getLine1()!=null){
			sql.append(address.getLine1());
			sql.append("','");
		}else{
			sql.append("','");
		}if(address.getLine2()!=null){
			sql.append(address.getLine2());
			sql.append("','");
		}else{
			sql.append("','");
		}
		if(address.getLine3()!=null){
			sql.append(address.getLine3());
			sql.append("','");	
		}else{
			sql.append("','");
		}if(address.getLine4()!=null){
			sql.append(address.getLine4());
			sql.append("','");	
		}else{
			sql.append("','");
		}
		if(address.getStreet()!=null){
			sql.append(address.getStreet());
			sql.append("','");
		}else{
			sql.append("','");
		}
		if(address.getState()!=null){
			sql.append(address.getState());
			sql.append("','");	
		}else{
			sql.append("','");
		}
		if(address.getCountry()!=null){
			sql.append(address.getCountry());
			sql.append("',");	
		}else{
			sql.append("',");
		}
		sql.append(address.getLat());
		sql.append(",");
		sql.append(address.getLng());
		sql.append(",");
		sql.append(address.getId());
		sql.append(", GeometryFromText('POINT (");
		sql.append(address.getLng());
		sql.append(" ");
		sql.append(address.getLat());
		sql.append(")',-1))");
		jdbcTemplate.update(sql.toString());
		LOG.info("KML Address Insertion Query "+sql.toString());
		return address;
	}

	@Override
	public List<Address> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Address> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Address update(Address entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
