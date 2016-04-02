package com.i10n.mina.codec.decoder;

import java.io.IOException;

/**
 * 
 */


public interface IModuleDataProcessor {

	/**
	 * 
	 * @param firmwareVersion
	 * @return int - indicating the response
	 * @throws ModuleDataException
	 * @throws IOException
	 */
    public int processData(int firmwareVersion) throws ModuleDataException, IOException;

}
