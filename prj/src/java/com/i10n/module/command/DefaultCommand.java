package com.i10n.module.command;

/**
 * Default command class for unlikely command ids
 * @author dharmaraju
 *
 */
public class DefaultCommand implements ICommandBean {

	@Override
	public Command getCommandID() {
		return Command.DEFAULT;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
