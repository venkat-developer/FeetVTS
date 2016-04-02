package com.i10n.module.dataprocessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.module.dataprocessor.DataPool;


public class ReadWriteJsonToFIle {
	private static String mString;
	private static int counter=0;
	private static final Logger LOG = Logger.getLogger(ReadWriteJsonToFIle.class);


	/**
	 * Writing the data packet into the file when error code 7 occurs 
	 * @param mDataPacket
	 */
	public static void writeDataPacketToFile(GWTrackModuleDataBean mDataPacket) {
		BufferedWriter mBufferedWriter = null;
		File mFile = null;
		try {
			File mFile1=new File((EnvironmentInfo.getProperty("FOLDER_NAME")));
			if(!mFile1.exists())
				mFile1.mkdir();
			Gson mGson=new Gson();
			if(mFile1.listFiles().length==0){
				Date d=new Date();
				LOG.debug("directory is created "+mFile1.exists()+ " path is "+mFile1.getAbsolutePath());
				mFile=new File(EnvironmentInfo.getProperty("FOLDER_NAME")+d.getTime()+".txt");
				mFile.createNewFile();
				mString="["+mGson.toJson(mDataPacket);
			}else {
				for(File mFile2: mFile1.listFiles()){
					mFile=mFile2;
					LOG.debug("file created is  "+mFile.getAbsolutePath());
					mString=mGson.toJson(mDataPacket);
				}
			}
			FileWriter mFileWriter=new FileWriter(mFile,true);
			mBufferedWriter=new BufferedWriter(mFileWriter);
			mBufferedWriter.write(mString+",");
			mBufferedWriter.close();
		} catch (IOException e) {
			LOG.error("IO Exception while writing the packet into file "+e.getMessage());
		}
		LOG.debug("number of packets writen to the file till now :: "+(counter+1));
	}


	/**
	 * To read the data packet's writen to the file on by one add them to data pool
	 */
	public static void readDataPacketFromJson(){
		LOG.debug("Reading datapacket from JSON");
		JSONParser parser = new JSONParser();
		int counter=0;
		File mFolder=new File(EnvironmentInfo.getProperty("FOLDER_NAME"));
		if(!mFolder.exists()){
			LOG.debug("folder does not exists so returning with out reading");
			return;
		}
		for(File mFile: mFolder.listFiles()){
			try {
				LOG.debug("file found succesfully ::"+mFile.getName()+"   "+mFile.exists());
				FileWriter mFileWriter=new FileWriter(mFile,true);
				BufferedWriter mBufferedWriter=new BufferedWriter(mFileWriter);
				mBufferedWriter.write("]");
				mBufferedWriter.close();
				LOG.debug("file does exists and reading the file");
				FileReader mFileReader=new  FileReader(mFile);
				JSONArray mArray = (JSONArray) parser.parse(mFileReader);
				for (int i = 0; i < mArray.size(); i++) {
					counter=i+1;
					JSONObject mObject =  (JSONObject) mArray.get(i);
					Gson mGson=new Gson();
					GWTrackModuleDataBean moduleDataBean=mGson.fromJson(mObject.toJSONString(), GWTrackModuleDataBean.class);
					DataPool.addPacket(moduleDataBean);
					LOG.debug("data packet added to the server isss == "+moduleDataBean.toString());
				}   
				mFileReader.close();
			}catch (IOException e) {
				LOG.error("Error while reading file ",e);
			}catch (ParseException e) {
				LOG.error("Error while parsing file ",e);
			} finally{
				if(mFile.exists()){
					LOG.debug("whether the file is deleted or not :: "+mFile.delete()+" path "+mFile.getAbsolutePath());
					LOG.debug("Number of records read from the file are "+counter);
				}
			}
		}
	}

}
