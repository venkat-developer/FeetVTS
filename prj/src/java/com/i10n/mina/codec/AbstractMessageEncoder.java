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
package com.i10n.mina.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.i10n.mina.message.AbstractMessage;

/**
 * A {@link MessageEncoder} that encodes message header and forwards
 * the encoding of body to a subclass.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class AbstractMessageEncoder<T extends AbstractMessage> implements MessageEncoder<T> {
    
    protected AbstractMessageEncoder() {
    }

    public void encode(IoSession session, T message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buf = IoBuffer.allocate(1);
        // Encode a body
        encodeBody(session, message, buf);
        buf.flip();
        out.write(buf);
    }

    protected abstract void encodeBody(IoSession session, T message, IoBuffer out);
}
