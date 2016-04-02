package com.i10n.mina.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.mina.codec.Constants;

/**
 * The responsibility of the class is to provide an interface to
 * packet processing code to persist the updates into a file
 * 
 * @author vishnu
 *
 */
public class WriterFileManager {
	
	Logger LOG = Logger.getLogger(WriterFileManager.class);

	/* Ensures that too many rows are not written in one file **/
	int rowsWrittenIntoFile = 0;

	/* Stream to push objects */
	ObjectOutputStream objectOutputStream;

	/* File handling object */
	FileOutputStream fileOutputStream ;

	/* Singleton */
	private static WriterFileManager _instance = null;

	/**
	 * Constructor
	 */
	private WriterFileManager () {
		loadFile();
	}
	
	/**
	 * Gets the singleton instance
	 * @return
	 */
	public static WriterFileManager getInstance () {
		if (_instance == null) {
			_instance = new WriterFileManager();
		}
		return _instance;
	}

	/**
	 * Loads the configuration from the system
	 */
	public void loadFile () {
		String fileName = Constants.DATA_FILE_PREFIX+WriterConfigFile.getInstance().getFileIdUnderProcess()+Constants.DATA_FILE_SUFFIX;
		LOG.error("Loading data file "+fileName);
		try {
			fileOutputStream = new FileOutputStream(fileName, true);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
		} catch (Exception e) {
			LOG.error("Could not write data to the config file", e);
		}
	}
	
	/**
	 * Adds packet to the file that is currently open
	 * @param dataBean
	 */
	public void addPacket (GWTrackModuleDataBean dataBean) {
		try {
			rowsWrittenIntoFile++;
			if (rowsWrittenIntoFile > Constants.DATA_OBJECTS_PER_FILE) {
				closeFile();
				WriterConfigFile.getInstance().cleanUp();
				loadFile();
			}
			objectOutputStream.writeObject(dataBean);
			objectOutputStream.flush();
		} catch (IOException e) {
			LOG.error("Problem in serializing file", e);
		}
	}

	/**
	 * Closes the open file
	 */
	public void closeFile () {
		LOG.error("Closing data file");
		try {
			//Exceeded the amount of rows per file so we are writing an EOF identifier				
			GWTrackModuleDataBean endOfFileDescriptor = new GWTrackModuleDataBean();
			endOfFileDescriptor.setCellId(Integer.MIN_VALUE);
			objectOutputStream.writeObject(endOfFileDescriptor);
		} catch (IOException e) {
			LOG.error("Exception while closing the file",e);
		} finally {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				LOG.error(e);
			}
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
	}
	
}
