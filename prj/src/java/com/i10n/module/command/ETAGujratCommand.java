package com.i10n.module.command;

/**
 * ETA Command for Bus Stop Display.
 * @author dharmaraju
 *
 */
public class ETAGujratCommand implements ICommandBean{

	private long stopId;
	private long routeId;
	
	public ETAGujratCommand(long routeId, long stopId) {
		setRouteId(routeId);
		setStopId(stopId);
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
	}

	public long getRouteId() {
		return routeId;
	}

	public void setRouteId(long routeId) {
		this.routeId = routeId;
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
