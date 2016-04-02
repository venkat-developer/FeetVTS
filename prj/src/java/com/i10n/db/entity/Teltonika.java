package com.i10n.db.entity;

import java.util.Vector;

import com.i10n.db.entity.IEntity.IEntity;

/**
 * This class is an entity for Teltonika device data
 * The data goes as below 
 * 
 * Four zeros| Data length | Data | Crc
 * 
 * Data format is as below 
 * 
 * Timestamp 	Priority 	GPS Element 	IO Element
 * 8 Bytes 		1 Byte 		15 Bytes 		...
 * 
 * Priority values are 
 * 0	Low
 * 1	High
 * 2	Panic
 * 3 	Security
 * 
 * 
 * GPS Element Structure is 
 * 
 * Longitude 	Latitude 	Altitude 	Angle 		Satellites 	Speed
 * 4 Bytes 		4 Bytes 	2 Bytes 	2 Bytes 	1 Byte 		2 Bytes
 * 
 * @author DVasudeva
 *
 */
public class Teltonika implements IEntity<Teltonika>{
	
	
	private int codecId = 0;
	private int dataPacketLength = 0;
	// The following is the Data part of the Teltonika packet
	private Vector<TeltonikaAVLData> avlDataArray ;
	
	// The data packet length is padded at the end of Data array. 
	private int dataPacketLenghtPadding = 0;
		
	private int crc = 0;
	
	public Teltonika() {
		
	}
	
	public int getCodecId() {
		return codecId;
	}

	public void setCodecId(int codecId) {
		this.codecId = codecId;
	}

	public int getDataPacketLength() {
		return dataPacketLength;
	}

	public void setDataPacketLength(int dataPacketLength) {
		this.dataPacketLength = dataPacketLength;
	}

	public int getDataPacketLenghtPadding() {
		return dataPacketLenghtPadding;
	}

	public void setDataPacketLenghtPadding(int dataPacketLenghtPadding) {
		this.dataPacketLenghtPadding = dataPacketLenghtPadding;
	}

	public Vector<TeltonikaAVLData> getAvlDataArray() {
		return avlDataArray;
	}

	public void setAvlDataArray(Vector<TeltonikaAVLData> avlDataArray) {
		this.avlDataArray = avlDataArray;
	}

	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

	@Override
	public boolean equalsEntity(Teltonika object) {
		// TODO Auto-generated method stub
		return false;
	}
}
