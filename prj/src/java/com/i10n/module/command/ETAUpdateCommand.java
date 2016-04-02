package com.i10n.module.command;

/**
 * ETA Command for Bus Stop Display.
 * @author dharmaraju
 *
 */
public class ETAUpdateCommand implements ICommandBean{

	private int busStopId;
	
	public ETAUpdateCommand(int command) {
		setBusStopId(command);
	}

	public int getBusStopId() {
		return busStopId;
	}

	public void setBusStopId(int busStopId) {
		this.busStopId = busStopId;
	}

	@Override
	public Command getCommandID() {
		return Command.ETA_GUJRAT;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
