package com.i10n.module.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * Handles the response to be sent for the Gujrat LEDs specifically. 
 * 
 * @author dharmaraju
 * 
 */
public class ETAGujratResponse implements IResponse {

	private String busNumber;
	private String gujratiDestinationId;
	private String eta;
	private String etd;

	public ETAGujratResponse() {

	}

	public ETAGujratResponse(String busNumber, String gujratiDestinationId,
			String eta, String etd) {
		this.busNumber = busNumber;
		this.gujratiDestinationId = gujratiDestinationId;
		this.eta = eta;
		this.etd = etd;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}

	public String getGujratiDestinationId() {
		return gujratiDestinationId;
	}

	public void setGujratiDestinationId(String gujratiDestinationId) {
		this.gujratiDestinationId = gujratiDestinationId;
	}

	public String getEta() {
		return eta;
	}

	public void setEta(String eta) {
		this.eta = eta;
	}

	public String getEtd() {
		return etd;
	}

	public void setEtd(String etd) {
		this.etd = etd;
	}

	@Override
	public byte[] getResponse() throws IOException {
		ByteArrayOutputStream responseByteAOS = null;
		DataOutputStream responseDataOS = null;
		try {
			responseByteAOS = new ByteArrayOutputStream();
			responseDataOS = new DataOutputStream(responseByteAOS);
			/** Check whether there is data existing for the stopId requested. **/
			if (getBusNumber() != null) {
				/** Data existing .. **/
				responseDataOS.writeByte(5);// success response.
				/** Necessary Details to Display **/
				responseDataOS.writeBytes(getBusNumber());
				responseDataOS.writeBytes(getGujratiDestinationId());
				responseDataOS.writeBytes(getEta());
				responseDataOS.writeBytes(getEtd());
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
			return responseByteAOS.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(responseByteAOS);
			IOUtils.closeQuietly(responseDataOS);
		}
	}
}
