package com.i10n.module.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Praveen DS (c) Versata 2009
 */
public class PollCommandResponse implements IResponse {

    private static int LENGTH = 8; // Excluding driver length;
    private byte m_opcode = 2;
    private short m_length;
    private short m_pollId;
    private byte m_driverNameLength;
    private String m_driverName;
    private byte m_mode;

    public PollCommandResponse(short pollId, byte driverNameLength, String driverName, byte mode) {
        m_pollId = pollId;
        m_driverNameLength = driverNameLength;
        m_driverName = driverName;
        m_mode = mode;
        m_length = (byte) (LENGTH + m_driverName.length());
    }

    @Override
    public byte[] getResponse() throws IOException {
        ByteArrayOutputStream responseByteAOS = null;
        DataOutputStream responseDataOS = null;
        try {
            responseByteAOS = new ByteArrayOutputStream();
            responseDataOS = new DataOutputStream(responseByteAOS);
            responseDataOS.writeByte(m_opcode); // failure
            responseDataOS.writeShort(m_length);
            responseDataOS.writeShort(m_pollId);
            responseDataOS.writeByte(m_driverNameLength);
            responseDataOS.writeBytes(m_driverName);
            responseDataOS.writeByte(m_mode);
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
