/**
 * 
 */
package com.i10n.module.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * @author sreekanth
 *
 */
public class DeleteDriverPollResponse implements IResponse {

    private byte m_opcode = 3;
    private int m_driverid;
    
    public DeleteDriverPollResponse(int mDriverid) {
		super();
		m_driverid = mDriverid;
	}
    
	@Override
	public byte[] getResponse() throws IOException {
		ByteArrayOutputStream responseByteAOS = null;
        DataOutputStream responseDataOS = null;
        try {
            responseByteAOS = new ByteArrayOutputStream();
            responseDataOS = new DataOutputStream(responseByteAOS);
            responseDataOS.writeByte(m_opcode); 
            responseDataOS.writeInt(m_driverid);
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