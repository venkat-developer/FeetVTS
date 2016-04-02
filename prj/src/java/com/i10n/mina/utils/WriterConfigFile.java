package com.i10n.mina.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.i10n.mina.codec.Constants;


/**
 * The responsibility of this class is to keep track of where
 * the last write was done.
 * 
 * @author vishnu
 *
 */
public class WriterConfigFile {

	private static Logger LOG = Logger.getLogger(WriterConfigFile.class);
	
	Preferences config;
	
	/* Represents the file name prefix which is currently under use */
	int fileIdUnderProcess;
	
	private static WriterConfigFile _instance = null;

	/**
	 * Constructor
	 */
	private WriterConfigFile () {
		load();
	}
	
	/**
	 * Gets the singleton instance
	 * @return
	 */
	public static WriterConfigFile getInstance () {
		if (_instance == null) {
			_instance = new WriterConfigFile();
		}
		return _instance;
	}

	/**
	 * Loads the configuration from the system
	 */
	public void load () {
		LOG.error("Loading config file");

		try {
			Reader fileReader = new FileReader (Constants.CONFIG_FILE_LOCATION);
		  	BufferedReader bir = new BufferedReader(fileReader);
		  	String configLine = bir.readLine();
		  	bir.close();
		  	fileIdUnderProcess = Integer.parseInt(configLine);
		  	
		} catch (Exception e) {
			LOG.error("Could not load data from the config file", e);
		}
	}

	/**
	 * Method to be called from context unload
	 */
	public void cleanUp () {
		LOG.error("Closing config file");
		fileIdUnderProcess++;
		// Ensures same file is not touched again
		try {
			Writer file = new FileWriter (Constants.CONFIG_FILE_LOCATION, false);
		  	BufferedWriter bwr = new BufferedWriter(file);
		  	String config = ""+fileIdUnderProcess;
		  	bwr.write(config);
		  	bwr.close();
		} catch (Exception e) {
			LOG.error("Could not write data to the config file", e);
		}
	}
	
	@Override
	public String toString() {
		return "Loaded config : File prefix - "+fileIdUnderProcess;
	}

	/**
	 * Getters and setter methods
	 */
	public int getFileIdUnderProcess() {
		return fileIdUnderProcess;
	}

	public void setFileIdUnderProcess(int fileIdUnderProcess) {
		this.fileIdUnderProcess = fileIdUnderProcess;
	}



	/**
	 * Test method
	 * @param args
	 */
	public static void main(String[] args) {
		WriterConfigFile configFile = new WriterConfigFile();
		configFile.load ();
	}

}