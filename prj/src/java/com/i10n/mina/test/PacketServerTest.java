/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.i10n.mina.test;

import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.stream.FileImageInputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.mchange.lang.ByteUtils;

/**
 * TODO Add documentation
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class PacketServerTest {
	
	static Logger LOG = Logger.getLogger(PacketServerTest.class);


    private ConfigurableApplicationContext appContext;
    
    @Before
    public void tearDown() throws Exception {
        if (appContext != null) {
            appContext.close();
        }
    }

    @Test
    public void testContext() {	
    	Socket socket = null;
    	try {
    		BasicConfigurator.configure();
    		LOG.debug("inside text Context.");
	        File inFile = new File ("C:/Users/dharmaraju/Desktop/etamodule.hex");
	    	FileImageInputStream fis = new FileImageInputStream(inFile);
	    	socket = new Socket("localhost", 8060);
	    	DataInputStream dis = new DataInputStream(socket.getInputStream());
	    	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
	    	 
	    	long length = 0;
	    	length = fis.length();
	    	LOG.debug("length :"+length);
	    	// Writing module bytes into the socket	    
	    	for (int i=0; i<length; i++) {
	    		byte data = fis.readByte();
	    		LOG.debug("data : "+ByteUtils.toHexAscii(data));
	    		dos.write(data);
	    		
	    	}
	    	fis.close();
	    	
	    	length = dis.available();
	    	
	    	LOG.debug("length available :"+length);

	    	// Waiting for response
	    	while (dis.available()==0);
	    	
	    	// Reading server response
	    	byte data = dis.readByte();
	    	LOG.debug("Result Data :"+ data);    		
	    	dis.close();
	    	dos.close();
	    	
            assertTrue(true);
    	}
    	catch (Exception e) {
    		LOG.error("Exception in executing the test", e);
    	} finally {
    		if(socket != null){
    			try {
					socket.close();
				} catch (IOException e) {
					LOG.error(e);
				}
    		}
    	}
    }
}
