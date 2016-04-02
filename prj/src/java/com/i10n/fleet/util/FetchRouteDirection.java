//package com.i10n.fleet.util;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import net.minidev.json.JSONObject;
//import net.minidev.json.JSONValue;
//
//import org.apache.log4j.Logger;
//import org.postgis.Point;
//
//public class FetchRouteDirection {
//
//	private static final String DESTINATION = "$destination";
//	private static final String SOURCE = "$source";
//	
//	public static String url_sample = "http://maps.google.co.in/maps?mra=dm&mrsp=0&sz=12&jsv=358a&sll=12.930806,77.669563&sspn=0.0957,0.338173&output=dragdir&doflg=s&saddr=$source&daddr=$destination";
//	
//	private static Logger LOG = Logger.getLogger(FetchRouteDirection.class);
//	
//	/**
//	 * Fetches the distance asking google 
//	 * 
//	 * @param source
//	 * @param destination
//	 * @return Distance in KM
//	 */
//	public double fetchDistanceFromGoogle(Point source, Point destination){
//		
//		InputStreamReader in = null;
//		BufferedReader bReader = null;
//		String response = null;
//		String url = url_sample;
//		double distance = 0.0;
//		
//		url = url.replace(DESTINATION, destination.getFirstPoint().x+","+destination.getFirstPoint().y);
//		url = url.replace(SOURCE, source.getFirstPoint().x+","+source.getFirstPoint().y);
//		
//		try{
//			URL u = new URL(url);
//			in = new InputStreamReader(u.openStream());
//			bReader = new BufferedReader(in);
//			response = bReader.readLine();
//			Object jsonData = JSONValue.parse(response);
//			JSONObject obj = (JSONObject)jsonData;
//			String tooltip = (String)obj.get("tooltipHtml");
//			String units = tooltip.substring(tooltip.lastIndexOf(';') + 1, tooltip.indexOf('/', tooltip.lastIndexOf(';')));
//			units = units.trim();
//			units = units.toLowerCase();
//			distance = Double.parseDouble(tooltip.substring(tooltip.lastIndexOf('(') + 1, tooltip.indexOf('#', tooltip.lastIndexOf('('))));
//			if(units.equals("km")){
//				//	do nothing 
//			} else{
//				//	convert the distance to kilometer 
//				distance/=100;
//			}
//			
//		}
//		catch(MalformedURLException e){
//			LOG.error(e);
//		}
//		catch(IOException e){
//			LOG.error(e);
//		}
//		catch(NumberFormatException e){
//			LOG.error(e);
//		}
//		
//		return distance;
//	}
//	
//}
