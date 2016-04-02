/*
 *  Licensed to the Apache SofRtware Foundation (ASF) under one
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

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.mina.message.ResultMessage;
import com.i10n.module.command.IResponse;
import com.i10n.module.dataprocessor.DataPool;

/**
 * {@link IoHandler} for SumUp server.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ServerSessionHandler extends IoHandlerAdapter {

	private final static Logger LOG = Logger
			.getLogger(ServerSessionHandler.class);

	@Override
	public void sessionOpened(IoSession session) {
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
		LOG.debug("Opened connection from " + session.getRemoteAddress());
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		// return the result message
		ResultMessage rm = new ResultMessage();
		try {
			if (message instanceof GWTrackModuleDataBean) {
				rm.setOk(true);
			} else if (message instanceof IResponse) {
				rm.setCommandPacketResponseMessage((IResponse) message);
			}
			try {
				if (rm != null && session != null) {
					session.write(rm);
				}
				if (session != null) {
					session.close(true);
				}
			} catch (Exception e) {
				LOG.error("Catching general exception while closing the session ",e);
			}
			if (message instanceof GWTrackModuleDataBean) {
				GWTrackModuleDataBean obj = (GWTrackModuleDataBean) message;
				if (obj != null) {
					DataPool.addPacket(obj);
				}
				return;
			} else if (message instanceof IResponse) {
				return;
			}
			rm = null;
			session = null;
		} catch (Exception e) {
			rm.setOk(true);
			// Looking for any class cast exceptions
			LOG.error("Problem in converting object", e);
			LOG.error("Error!!!! \n Still sending success response");
		} finally {
			try {
				if (rm != null && session != null) {
					session.write(rm);
				}
				if (session != null) {
					session.close(true);
				}
			} catch (Exception e) {
				LOG.error("Catching general exception while closing the session ",e);
			}
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		try{
			LOG.error("Sending failure response as connection from "
					+ session.getRemoteAddress() + " timedout");
			ResultMessage rm = new ResultMessage();
			rm.setOk(false);
			session.write(rm);
			session.close(true);
		} catch (Exception e){
			LOG.error("Error in method sessionIdle ", e);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		try{
			// close the connection on exceptional situation
			ResultMessage rm = new ResultMessage();
			LOG.debug("Sending failure respose on account of exception while processing pkt to "
							+ session.getRemoteAddress(), cause);
			rm.setOk(false);
			session.write(rm);
			session.close(true);
		} catch (Exception e){
			LOG.error("Error in method exceptionCaught ", e);
		}
	}
}