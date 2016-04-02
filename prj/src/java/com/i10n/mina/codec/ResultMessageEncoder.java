package com.i10n.mina.codec;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.i10n.mina.message.ResultMessage;
import com.mchange.lang.ByteUtils;

/**
 * A {@link MessageEncoder} that encodes {@link ResultMessage}.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ResultMessageEncoder<T extends ResultMessage> extends AbstractMessageEncoder<T> {

	private static final Logger LOG = Logger.getLogger(ResultMessageEncoder.class);

	private static final byte SUCCESS_RESPONSE = 0x01;
	
	/** 
	 * TODO: Deliberately changing it 
	 * private static final byte FAILURE_RESPONSE = 0x04;
	 */
	private static final byte FAILURE_RESPONSE = 0x04;

	public ResultMessageEncoder() {
		super();
	}

	@Override
	protected void encodeBody(IoSession session, T message, IoBuffer out) {
		if(message.getCommandPacketResponseMessage() != null && message.isOk()){
			out.setAutoExpand(true);
			try {
				LOG.debug("Sending command packet response. Response = "+ByteUtils.toHexAscii(message.getCommandPacketResponseMessage().getResponse()));
				out.put(message.getCommandPacketResponseMessage().getResponse());
			} catch (IOException e) {
				LOG.fatal("Error While sending the response to command packet");
			}finally{
				out.setAutoExpand(false);
			}
		}else{
			if (message.isOk()) {
				out.put(SUCCESS_RESPONSE);
			} else {
				out.put(FAILURE_RESPONSE);
			}
		}
	}

	public void dispose() throws Exception {
	}
}
