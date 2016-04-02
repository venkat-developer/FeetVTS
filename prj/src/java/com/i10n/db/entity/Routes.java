package com.i10n.db.entity;

import java.sql.Time;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.mchange.lang.ByteUtils;
/**
 * Entity Class for Routes Table.
 * @author Dharmaraju V
 *
 */
public class Routes implements IEntity<Routes>{
	private LongPrimaryKey id;
	private String routeName;
	private String startPoint;
	private String endPoint;
	private Time startTime;
	private Time endTime;
	private long ownerId;
	private int spanningDays;
	private byte[] routeBitMap;
	private byte[] destinationBitMap;

	private String englishRouteBitMap;
	private String localLanguageRouteBitMap;
	private String englishDestinationBitMap;
	private String localLanguageDestinationBitMap;

	// One row data for Route Name and Destination in English and local language
	private String englishRouteAndDestinationBitMap;
	private String localRouteAndDestinationBitMap;


	public Routes(Long id, String routeName,String startPoint,String endPoint,Time startTime , Time endTime, long ownerId,
			int spanningDays, String routeBitMap, String destinationBitMap, 
			String englishRouteAndDestinationBitMap, String localRouteAndDestinationBitMap){
		this.id = new LongPrimaryKey(id);
		this.routeName = routeName;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startTime = startTime;
		this.endTime = endTime;
		this.ownerId = ownerId;
		this.spanningDays = spanningDays;
		if(routeBitMap !=null){
		this.routeBitMap = ByteUtils.fromHexAscii(routeBitMap.trim());
		}
		if(destinationBitMap != null){
		this.destinationBitMap = ByteUtils.fromHexAscii(destinationBitMap.trim());
		}
		this.englishRouteAndDestinationBitMap = englishRouteAndDestinationBitMap;
		this.localRouteAndDestinationBitMap = localRouteAndDestinationBitMap;
	}

	public Routes(Long id, String routeName,String startPoint,String endPoint,Time startTime , Time endTime){
		this.id = new LongPrimaryKey(id);
		this.routeName = routeName;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Routes() {
		// TODO Auto-generated constructor stub
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}


	public String getRouteName() {
		return routeName;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getStartPoint() {
		return this.startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	@Override
	public boolean equalsEntity(Routes object) {
		return false;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public int getSpanningDays() {
		return spanningDays;
	}

	public void setSpanningDays(int spanningDays) {
		this.spanningDays = spanningDays;
	}

	public byte[] getRouteBitMap() {
		return routeBitMap;
	}

	public void setRouteBitMap(byte[] routeBitMap) {
		this.routeBitMap = routeBitMap;
	}

	public byte[] getDestinationBitMap() {
		return destinationBitMap;
	}

	public void setDestinationBitMap(byte[] destinationBitMap) {
		this.destinationBitMap = destinationBitMap;
	}

	public String getEnglishRouteBitMap() {
		return englishRouteBitMap;
	}

	public void setEnglishRouteBitMap(String englishRouteBitMap) {
		this.englishRouteBitMap = englishRouteBitMap;
	}

	public String getLocalLanguageRouteBitMap() {
		return localLanguageRouteBitMap;
	}

	public void setLocalLanguageRouteBitMap(String localLanguageRouteBitMap) {
		this.localLanguageRouteBitMap = localLanguageRouteBitMap;
	}

	public String getEnglishDestinationBitMap() {
		return englishDestinationBitMap;
	}

	public void setEnglishDestinationBitMap(String englishDestinationBitMap) {
		this.englishDestinationBitMap = englishDestinationBitMap;
	}

	public String getLocalLanguageDestinationBitMap() {
		return localLanguageDestinationBitMap;
	}

	public void setLocalLanguageDestinationBitMap(
			String localLanguageDestinationBitMap) {
		this.localLanguageDestinationBitMap = localLanguageDestinationBitMap;
	}

	public String getEnglishRouteAndDestinationBitMap() {
		return englishRouteAndDestinationBitMap;
	}

	public void setEnglishRouteAndDestinationBitMap(
			String englishRouteAndDestinationBitMap) {
		this.englishRouteAndDestinationBitMap = englishRouteAndDestinationBitMap;
	}

	public String getLocalRouteAndDestinationBitMap() {
		return localRouteAndDestinationBitMap;
	}

	public void setLocalRouteAndDestinationBitMap(
			String localRouteAndDestinationBitMap) {
		this.localRouteAndDestinationBitMap = localRouteAndDestinationBitMap;
	}

	public String toString(){
		StringBuffer returnString = new StringBuffer();
		returnString.append("RouteId = ");
		returnString.append(id.getId());
		returnString.append("RouteName = ");
		returnString.append(routeName);
		returnString.append(", StartPoint = ");
		returnString.append(startPoint);
		returnString.append(", EndPoint = ");
		returnString.append(endPoint);
		returnString.append(", SpanningDays = ");
		returnString.append(spanningDays);
		/*returnString.append(", RouteBitMap = ")
		 returnString.append(ByteUtils.toHexAscii(routeBitMap));
		 returnString.append(", DestinationBitMap = ");
		 returnString.append(ByteUtils.toHexAscii(destinationBitMap));*/;
		 return returnString.toString();
	}


}
