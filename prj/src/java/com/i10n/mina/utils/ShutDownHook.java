package com.i10n.mina.utils;

import org.apache.log4j.Logger;

/**
 * Class to clean up all file related operations
 * 
 * @author vishnu
 *
 */
public class ShutDownHook extends Thread {
  
	Logger LOG = Logger.getLogger(ShutDownHook.class);
	/**
	 * Constructor
	 */
	public ShutDownHook() {
      
    }
	
	/**
	 * Thread
	 */
    public void run() {
    	WriterFileManager.getInstance().closeFile();
    	WriterConfigFile.getInstance().cleanUp();
   }
}