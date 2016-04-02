/**
 * 
 */
package com.i10n.module.command;

import java.io.IOException;

/**
 * @author joshua
 *
 */
public class SequenceRequestResponse implements IResponse{

	
	private byte sequenceNumber;
	private static final byte opcode = 0x04;
	private byte crc;
	
	public SequenceRequestResponse(int lastSequenceNumber) {
		this.sequenceNumber = (byte) lastSequenceNumber;
		crc = (byte) (opcode ^ sequenceNumber);
	}
	
	@Override
	public byte[] getResponse() throws IOException {
		byte responseArray[] = new byte[3];
		responseArray[0] = opcode;
		responseArray[1] = sequenceNumber;
		responseArray[2] = crc;
		return responseArray;
	}
	
	public String toString(){
		return "" + opcode + sequenceNumber + crc;
	}
}
