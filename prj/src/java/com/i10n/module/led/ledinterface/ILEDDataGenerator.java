package com.i10n.module.led.ledinterface;

import com.i10n.module.command.ICommandBean;
import com.i10n.module.command.IResponse;

public interface ILEDDataGenerator {

	public IResponse getLEDData(ICommandBean cmd) ;
}
