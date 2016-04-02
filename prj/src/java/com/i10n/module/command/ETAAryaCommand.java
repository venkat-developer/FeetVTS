package com.i10n.module.command;

import com.i10n.module.command.Command;
import com.i10n.module.command.ICommandBean;

/**
 * ETA Command for Bus Stop Display Arya Display.
 * @author dharmaraju
 *
 */
public class ETAAryaCommand implements ICommandBean{

	private long busStopId;
	
	private int languageFlag;
	
	private String IMEI;

	public ETAAryaCommand(long busStopId, int languageFlag, String IMEI) {
		setBusStopId(busStopId);
		setLanguageFlag(languageFlag);
		setIMEI(IMEI);
		
	}

	public long getBusStopId() {
		return busStopId;
	}

	public void setBusStopId(long busStopId) {
		this.busStopId = busStopId;
	}

	public void setLanguageFlag(int languageFlag) {
		this.languageFlag = languageFlag;
	}
	
	public int getLanguageFlag() {
		return languageFlag;
	}
	
	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	@Override
	public Command getCommandID() {
		return Command.ETA_BHOPAL_ARYA;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString(){
		StringBuffer toStringBuffer = new StringBuffer();
		toStringBuffer.append("ETAAryaCommand : BusStopId = ");
		toStringBuffer.append(busStopId);
		toStringBuffer.append(", LanguageFlag = ");
		toStringBuffer.append(languageFlag);
		toStringBuffer.append(", IMEI = ");
		toStringBuffer.append(IMEI);
		return toStringBuffer.toString();
	}
}
