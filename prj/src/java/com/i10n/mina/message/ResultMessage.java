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
package com.i10n.mina.message;

import com.i10n.module.command.IResponse;

/**
 * <code>RESULT</code> message in SumUp protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ResultMessage extends AbstractMessage {
    private static final long serialVersionUID = 7371210248110219946L;

    private boolean ok;
    
    private IResponse commandPacketResponseMessage = null;

    public ResultMessage() {
    	ok = true;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
    
    public IResponse getCommandPacketResponseMessage() {
		return commandPacketResponseMessage;
	}

	public void setCommandPacketResponseMessage(IResponse commandResponseMessage) {
		commandPacketResponseMessage = commandResponseMessage;
	}

	@Override
    public String toString() {
        if (ok) {
            return ":RESULT(OK)";
        } else {
            return ":RESULT(ERROR)";
        }
    }
}