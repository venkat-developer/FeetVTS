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
package com.i10n.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.i10n.mina.codec.decoder.GWTrackProtocolCodecFactory;

/**
 * (<strong>Entry Point</strong>) Starts Packet server.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class PacketServer {

	public static boolean shutdownInitiated = false;
	private static Logger LOG = Logger.getLogger(PacketServer.class);
    private static final int SERVER_PORT = 8060;
    private static final int PROCESSOR_COUNT = 500;

    // Set this to false to use object serialization instead of custom codec.
    private static final boolean USE_CUSTOM_CODEC = true;

    private static NioSocketAcceptor acceptor; 

    public static void initialize () {
    	
    	acceptor = new NioSocketAcceptor(PROCESSOR_COUNT);
    	// Prepare the service configuration.
        if (USE_CUSTOM_CODEC) {
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new GWTrackProtocolCodecFactory(true)));
        } else {
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        }
        // Disabling the NioProcessor LOGS. 
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        try {
        	acceptor.setHandler(new ServerSessionHandler());
        } catch (Exception e){
        	LOG.error(e);
        }
        try {
        	acceptor.setCloseOnDeactivation(true);
			acceptor.bind(new InetSocketAddress(SERVER_PORT));
		} catch (IOException e) {
			LOG.fatal("Binding exception in MINA ", e);
		}

        LOG.info("Listening on port " + SERVER_PORT);
    }
    
    
    public static void shutdown () {
    	shutdownInitiated = true;
    	acceptor.unbind();
    	acceptor.dispose();
   }
    
    // UnComment this while testing locally. 
    public static void main(String[] args) {
  	PacketServer.initialize();
	}
}
