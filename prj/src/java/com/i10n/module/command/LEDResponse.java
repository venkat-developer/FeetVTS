package com.i10n.module.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * General class to respond back LED Display device
 * @author Dharmaraju V
 *
 */
public class LEDResponse implements IResponse {

	private byte[] LEDBitMap;

	public LEDResponse() {
		LEDBitMap = null;			 
	}
	
	public LEDResponse(byte[] LEDBitMap){
		this.LEDBitMap = LEDBitMap;
	}
	
	public byte[] getLEDBitMap() {
		return LEDBitMap;
	}

	public void setLEDBitMap(byte[] lEDBitMap) {
		LEDBitMap = lEDBitMap;
	}
	
	@Override
	public byte[] getResponse() throws IOException {
		ByteArrayOutputStream responseByteAOS = null;
		DataOutputStream responseDataOS = null;
		try {
			responseByteAOS = new ByteArrayOutputStream();
			responseDataOS = new DataOutputStream(responseByteAOS);
			/** Check whether there is data existing for the stopId requested. **/
			if (LEDBitMap != null) {
				/** Data existing .. **/
				responseDataOS.writeByte(5);// success response.
				/** Necessary Details to Display **/
				responseDataOS.writeShort(LEDBitMap.length);
				responseDataOS.write(LEDBitMap);
			} else {
				/** Data not existing .. **/
				responseDataOS.writeByte(6);// failure response.
			}
			/** Calculate CRC **/
			byte[] temp = responseByteAOS.toByteArray();
			byte crc = temp[0];
			for (int i = 1; i < temp.length; i++) {
				crc ^= temp[i];
			}
			responseDataOS.writeByte(crc);
			responseDataOS.writeByte(13);
			return responseByteAOS.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(responseByteAOS);
			IOUtils.closeQuietly(responseDataOS);
		}
	}
}
