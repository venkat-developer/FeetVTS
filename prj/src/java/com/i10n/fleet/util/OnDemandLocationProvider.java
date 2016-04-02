package com.i10n.fleet.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.Address.ADDRESS_STATUS_ENUM;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.RequestParameters;

public class OnDemandLocationProvider implements IDataProvider{
	private static Logger LOG = Logger.getLogger(OnDemandLocationProvider.class);
	public static final int ACTIVITY_REPORT=1;
	public static final int VIOLATION_REPORT=2;
	public static final int VEHICLE_STATS_REPORT=3;
	public static final int DRIVER_IDLE_POINTS_REPORT=4;
	public static final int VEHICLE_IDLE_POINTS_REPORT=5;
	public static final int VEHICLEHISTORY_REPORT=6;
	public static final String ACTIVITY_REPORT_STRING="acitivity";
	public static final String VEHICLE_STATS_REPORT_STRING="vehiclestats";
	public static final String VIOLATION_REPORT_STRING="violation";
	public static final String DRIVER_IDLE_REPORT_STRING="driveridlepoints";
	public static final String VEHICLE_IDLE_REPORT_STRING="vehicleidlepoints";
	public static final String VEHICLEHISTORY_REPORT_STRING="vehiclehistory";
	String[] activity_report_headers = new String[] { "TIME","LOCATION", "LATITUDE", "LONGITUDE", "SPEED","CUMULATIVE DISTANCE" };
	String[] violation_report_headers = new String[] { "S_NO","VEHICLE NAME", "DRIVER NAME", "ALERT TIME", "ALERT LOCATION","ALERT TYPE"," ALERT VALUE" };
	String[] vehiclestats_report_headers = new String[] { "S_NO","VEHICLE NAME", "START TIME", "START LOCATION", "END TIME","CURRENT LOCATION","MAXIMUM SPEED","AVERAGE SPEED","DISTANCE" };
	String[] idle_points_report_headers = new String[] { "START TIME", "END TIME","IDLE TIME","LOCATION"};
	String[] vehicle_history_report_headers = new String[] { "S_NO", "UPDATED IMEI","UPDATED TIME","UPDATED BY","ATTENDED BY","BATTERY CHANGED","FUSE CHANGED"};
	@Override
	public String getName() {
		return "ondemandlocationfetch";
	}

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String lat = params.getRequestParameter("lat");
		String lng = params.getRequestParameter("long");
		StringBuffer address = new StringBuffer();
		Address locationFetch = GeoUtils.fetchNearestLocation(Double.parseDouble(lat),Double.parseDouble(lng), false);
		if(locationFetch !=null){
			if(!locationFetch.getLine1().isEmpty()){
				address.append(locationFetch.getLine1());
			}
			if(!locationFetch.getLine2().isEmpty()){
				address.append(","+locationFetch.getLine2());
			}
			if(!locationFetch.getLine3().isEmpty()){
				address.append(","+locationFetch.getLine3());
			}
			if(!locationFetch.getLine4().isEmpty()){
				address.append(","+locationFetch.getLine4());
			}
		}else{
			address.append(fetchAddressUsingOpenGIS(params.getRequestParameter("lat"), params.getRequestParameter("long")));
		}
		result.put("address",address.toString());
		result.put("column", params.getRequestParameter("column"));
		result.put("row", params.getRequestParameter("row"));
		return result;
	}



	@SuppressWarnings("deprecation")
	private static String fetchAddressUsingOpenGIS(String x, String y) {
		Address address = new Address();
		try{
			String OpenGIS_URL="http://open.mapquestapi.com/geocoding/v1/reverse?key=Fmjtd|luub2h0721%2Crw%3Do5-9utxgy&callback=renderReverse&location="+ x + "," + y;
			URL url=new URL(OpenGIS_URL);
			LOG.info("OpenGIS_URL"+OpenGIS_URL);
			try{
				URLConnection connection=url.openConnection();
				connection.setConnectTimeout(1000);
				InputStream is=connection.getInputStream();
				DataInputStream bis=new DataInputStream(is);
				String string="";
				StringBuffer stb=new StringBuffer();
				while ((string = bis.readLine())!=null) {
					stb.append(string);
				}
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject = new JSONObject(stb.toString().substring(stb.indexOf("{")));
					//System.out.println("Json Object"+jsonObject);
					JSONObject ss=jsonObject.getJSONArray("results").getJSONObject(0);
					LOG.debug("JSON Object"+ss);
					JSONObject location=ss.getJSONArray("locations").getJSONObject(0);
					String street=location.getString("street");
					String city=location.getString("adminArea5");
					String district=location.getString("adminArea4");
					String state=location.getString("adminArea3");
					address.setLine1(street);
					address.setLine2(city);
					address.setLine3(district+","+state);
					address.setLine4("India");
					address.setLat(Double.parseDouble(x));
					address.setLng(Double.parseDouble(y));
					address.setState(state);
					address.setStreet(street);
					address.setCountry("India");
					address.setFetchStatus(ADDRESS_STATUS_ENUM.FETCHED);
					is.close();
					bis.close();
					LOG.info("OpneGIS Address Fetch "+address.toString());
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return address.getLine1()+","+address.getLine2()+","+address.getLine3()+","+address.getLine4();	
	}
}
