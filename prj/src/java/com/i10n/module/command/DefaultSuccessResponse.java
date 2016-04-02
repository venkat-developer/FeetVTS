package com.i10n.module.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Praveen DS (c) Versata 2009
 */
public class DefaultSuccessResponse implements IResponse {

    public DefaultSuccessResponse() {
    }

    @Override
    public byte[] getResponse() throws IOException {
        ByteArrayOutputStream responseByteAOS = null;
        DataOutputStream responseDataOS = null;
        try {
            responseByteAOS = new ByteArrayOutputStream();
            responseDataOS = new DataOutputStream(responseByteAOS);
            responseDataOS.writeByte(1);// success
            responseDataOS.writeShort(4);
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
