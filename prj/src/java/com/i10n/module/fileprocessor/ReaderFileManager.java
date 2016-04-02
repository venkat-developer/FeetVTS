package com.i10n.module.fileprocessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.module.dataprocessor.DataPool;

/**
 * The responsibility of the class is to provide an interface to
 * packet processing code to persist the updates into a file
 * 
 * @author vishnu
 *
 */
public class ReaderFileManager {
	
	private static Logger LOG = Logger.getLogger(ReaderFileManager.class);

	/* Ensures that too many rows are not written in one file **/
	int rowsReadFromFile = 0;

	/* Stream to push objects */
	ObjectInputStream objectInputStream;

	/* File handling object */
	FileInputStream fileInputStream ;

	/* Singleton */
	private static ReaderFileManager _instance = null;

	/**
	 * Constructor
	 */
	private ReaderFileManager () {
		loadFile();
	}
	
	/**
	 * Gets the singleton instance
	 * @return
	 */
	public static ReaderFileManager getInstance () {
		if (_instance == null) {
			_instance = new ReaderFileManager();
		}
		return _instance;
	}

	/**
	 * Loads the configuration from the system
	 */
	public void loadFile () {
		GWTrackModuleDataBean dataBean;
		String fileName = (EnvironmentInfo.getProperty("DATA_FILE_PREFIX"))+ReaderConfigFile.getInstance().
				getFileIdUnderProcess()+(EnvironmentInfo.getProperty("DATA_FILE_SUFFIX"));
		LOG.debug("Loading data file "+fileName);
		try {
			fileInputStream = new FileInputStream(fileName);
			objectInputStream = new ObjectInputStream(fileInputStream);
			for (int i=0; i<ReaderConfigFile.getInstance().getObjectsReadFromFile(); i++) {
				dataBean = (GWTrackModuleDataBean) objectInputStream.readObject();
				if (dataBean.getCellId()==Integer.MIN_VALUE) {
					// Reached end of file
				}
			}
		} catch (Exception e) {
			LOG.error("Could not write data to the config file", e);
		}
	}
	
	/**
	 * Adds packet to the file that is currently open
	 * @param dataBean
	 */
	public void loadPacketsIntoPool () {
		try {	
			boolean reachedEOF = false;
			while(!reachedEOF) {
				GWTrackModuleDataBean dataBean = (GWTrackModuleDataBean) objectInputStream.readObject();	
				LOG.debug("Read object");
				ReaderConfigFile.getInstance().incrementObjectsReadFromFile();
				DataPool.addPacket(dataBean);
				dataBean = (GWTrackModuleDataBean) objectInputStream.readObject();
				LOG.debug("Read object "+dataBean);
				if (dataBean.getCellId()==Integer.MIN_VALUE) {
					// Reached end of file
					reachedEOF = true;
					LOG.debug("Reached intentional EOF");
				}
			}
			LOG.debug("Reached unintentional EOF");
			
		} catch (Exception e) {
			LOG.error("Problem in deserializing file", e);
		}
	}

	/**
	 * Closes the open file
	 */
	public void closeFile () {
		LOG.debug("Closing data file");
		try {
			objectInputStream.close();
		} catch (IOException e) {
			LOG.error("Exception while closing the Stream",e);
		}
		try {
			fileInputStream.close();
		} catch (IOException e) {
			LOG.error("Exception while closing the file",e);
		}
		
	}

	public static void main(String[] args) {
		ReaderConfigFile.getInstance();
		ReaderFileManager.getInstance().loadPacketsIntoPool();
	}
}
