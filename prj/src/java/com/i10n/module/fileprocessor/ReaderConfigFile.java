package com.i10n.module.fileprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.i10n.fleet.util.EnvironmentInfo;

/**
 * The responsibility of this class is to keep track of where
 * the last write was done.
 * 
 * @author vishnu
 *
 */
public class ReaderConfigFile {

	private static Logger LOG = Logger.getLogger(ReaderConfigFile.class);
	
	Preferences config;
	
	/* Represents the file name prefix which is currently under use */
	int fileIdUnderProcess;
	
	/* Represents the no of bytes written into that file */
	long ObjectsReadFromFile;
	
	private static ReaderConfigFile _instance = null;

	/**
	 * Constructor
	 */
	private ReaderConfigFile () {
		load();
	}
	
	/**
	 * Gets the singleton instance
	 * @return
	 */
	public static ReaderConfigFile getInstance () {
		if (_instance == null) {
			_instance = new ReaderConfigFile();
		}
		return _instance;
	}

	/**
	 * Loads the configuration from the system
	 */
	public void load () {
		LOG.debug("Loading config file "+(EnvironmentInfo.getProperty("CONFIG_FILE_LOCATION")));
		try {
			Reader fileReader = new FileReader (EnvironmentInfo.getProperty("CONFIG_FILE_LOCATION"));
		  	BufferedReader bir = new BufferedReader(fileReader);
		  	String configLine = bir.readLine();
		  	bir.close();
		  	String [] fields = configLine.split(",");
		  	fileIdUnderProcess = Integer.parseInt(fields[0]);
		  	ObjectsReadFromFile = Long.parseLong(fields[0]);
		  	
		} catch (Exception e) {
			LOG.error("Could not load data from the config file", e);
		}
	}
	
	/**
	 * Store the configuration from the system
	 */
	public void store () {
		LOG.debug("Storing config file");
		try {
			Writer file = new FileWriter (EnvironmentInfo.getProperty("CONFIG_FILE_LOCATION"));
		  	BufferedWriter bwr = new BufferedWriter(file);
		  	String config = fileIdUnderProcess+","+ObjectsReadFromFile;
		  	bwr.write(config);
		  	bwr.close();
		} catch (Exception e) {
			LOG.error("Could not write data to the config file", e);
		}
	}
	
	@Override
	public String toString() {
		return "Loaded config : File prefix - "+fileIdUnderProcess+" Bytes used - "+ObjectsReadFromFile;
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


	public long getObjectsReadFromFile() {
		return ObjectsReadFromFile;
	}

	public void setObjectsReadFromFile(long objectsInFile) {
		this.ObjectsReadFromFile = objectsInFile;
	}
	
	public void incrementObjectsReadFromFile () {
		ObjectsReadFromFile++;
		store();
	}
}