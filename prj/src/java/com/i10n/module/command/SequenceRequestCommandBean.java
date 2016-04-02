package com.i10n.module.command;

import com.i10n.dbCacheManager.LoadImeiSequenceMap;
/**
 *
 * @author dharmaraju
 *
 */
public class SequenceRequestCommandBean implements ICommandBean {

	private SequenceRequestResponse response;
	
	public SequenceRequestCommandBean(String requestedImei) {
		Integer seqNumber = LoadImeiSequenceMap.getInstance().retrieve(requestedImei);
		int lastSequenceNumber = ( null!= seqNumber ? seqNumber.intValue() : 0); 
		response = new SequenceRequestResponse(lastSequenceNumber);
	}

	@Override
	public Command getCommandID() {
		return Command.SEQUENCEREQUEST;
	}

	@Override
	public String getDescription() {
		return "SequenceRequest";
	}

	public SequenceRequestResponse getResponse() {
		return response;
	}
	
	public String toString(){
		return response.toString();
	}


}
