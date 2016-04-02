package com.i10n.module.command;

/**
 * ETA Command for Bus Stop Display For Chinese Display.
 * @author dharmaraju
 *
 */
public class ETAChineseCommand implements ICommandBean{

	private long busStopId;
	
	private int languageFlag;
	
	public ETAChineseCommand(long busStopId, int languageFlag) {
		setBusStopId(busStopId);
		setLanguageFlag(languageFlag);
	}

	public long getBusStopId() {
		return busStopId;
	}

	public void setBusStopId(long busStopId) {
		this.busStopId = busStopId;
	}

	public int getLanguageFlag() {
		return languageFlag;
	}

	public void setLanguageFlag(int languageFlag) {
		this.languageFlag = languageFlag;
	}

	@Override
	public Command getCommandID() {
		return Command.ETA_BHOPAL_CHINESE;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
