package com.i10n.fleet.web.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.ACLDriverDaoimpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.entity.ACLDriver;
import com.i10n.db.entity.Driver;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclDriverDetails;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;


/*
 * Class implementing methods for adding deleting editing and assigning vehicles  * 
 */

public class DriverAdminOperations extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(DriverAdminOperations.class);
	
	File savedFile;
	String finalimage;

	@SuppressWarnings("rawtypes")
	public boolean Uploading(HttpServletRequest request) throws FileUploadException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			LOG.debug("File Not Uploaded");
		} else {
			Iterator fileNames=null;
			fileNames =  ((MultipartHttpServletRequest) request).getFileNames();
			MultipartFile file=null;
			if (fileNames.hasNext()) {
				String fileName = (String) fileNames.next();
				file = ((MultipartHttpServletRequest) request).getFile(fileName);
				String itemName =null;
				try{ 
					itemName= file.getOriginalFilename();
				}
				catch(Exception e){
					LOG.error(e);
				}

				Random generator = new Random();
				int r = Math.abs(generator.nextInt());

				String reg = "[.*]";
				String replacingtext = "";

				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(itemName);
				StringBuffer buffer = new StringBuffer();

				while (matcher.find()) {
					matcher.appendReplacement(buffer, replacingtext);
				}
				int IndexOf = itemName.indexOf(".");
				String domainName = itemName.substring(IndexOf);

				finalimage = buffer.toString() + "_" + r+ domainName;
				savedFile = new File("/usr/local/tomcat6/webapps/driverimage/"+finalimage);
				savedFile.getAbsolutePath();
				try {
					file.transferTo(savedFile);
					/* *
					 * transferTo uses the tranfering a file location to destination
					 * 
					 */
				} catch (IllegalStateException e1) {
					LOG.error(e1);
				} catch (IOException e1) {
					LOG.error(e1);
				}
			} 			
		}
		return true;
	}

	public Driver addDriver(HttpServletRequest request){
		try {
			Uploading(request);
		} catch (FileUploadException e) {
			LOG.error(e);
		}
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String licenseno = request.getParameter("licenseno");
		String photo = finalimage;
		Driver driver = new Driver(firstname.trim(), lastname.trim(), licenseno.trim(), photo.trim());
		try {
			driver = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).insert(driver);
			updateDriverCache(driver);
		} catch (OperationNotSupportedException e) {
			LOG.error(e);
		}
		return driver;
	}

	public boolean editDriver(HttpServletRequest request) {
		boolean opSuccess = true;
		try {
			Uploading(request);
		} catch (FileUploadException e) {
			LOG.error(e);
		}
		String driverid = request.getParameter("key");
		driverid = StringUtils.stripCommas(driverid.trim());
		Long driverID = Long.parseLong(driverid);
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String photo=finalimage;
		String licenseno = request.getParameter("license");
		if(photo==null){
			photo = request.getParameter("photo");
		}
		if (firstname != null && lastname != null && licenseno != null) {
			Driver driver = LoadDriverDetails.getInstance().retrieve(driverID);
			driver.setFirstName(firstname.trim());
			driver.setLastName(lastname.trim());
			driver.setLicenseno(licenseno.trim());
			driver.setPhoto(photo.trim());
			try {
				driver = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).update(driver);
				if(driver == null){
					opSuccess= false;
					return opSuccess;
				}
				LoadDriverDetails.getInstance().cacheDriverRecords.put(driverID, driver);
			}
			catch (OperationNotSupportedException e) {
				LOG.error(e);
			}
		}
		return opSuccess;
	}

	public boolean assignDriver(HttpServletRequest request) {
		boolean opSuccess = true;
		String userid = request.getParameter("userID");
		userid = StringUtils.stripCommas(userid.trim());
		Long user = Long.parseLong(userid);
		String assigneddriverlist = request.getParameter("assignedDrivers");
		String vacantdriverlist = request.getParameter("vacantDrivers");

		// Split the params if more no of drivers selected
		String[] assineddrivers = assigneddriverlist.trim().split(",");
		String[] vacantdrivers = vacantdriverlist.trim().split(",");
		if (assineddrivers != null || vacantdrivers != null) {
			for (int i = 0; i < assineddrivers.length; i++) {
				Long adrivers = null;
				if (!assineddrivers[i].equals("") || assineddrivers[i].length() != 0) {
					adrivers = Long.parseLong(assineddrivers[i].trim());
				} else {
					continue;
				}
				ACLDriver acldriver = new ACLDriver(adrivers, user);
				try {
					acldriver = ((ACLDriverDaoimpl) DBManager.getInstance().getDao(DAOEnum.ACL_DRIVER_DAO)).insert(acldriver);
					if (acldriver == null) {
						opSuccess = false;
					}
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
			}
			for (int i = 0; i < vacantdrivers.length; i++) {
				Long vdrivers = null;
				if (!vacantdrivers[i].equals("") || vacantdrivers[i].length() != 0) {
					vdrivers = Long.parseLong(vacantdrivers[i].trim());
				} else {
					continue;
				}
				ACLDriver Acldriver = new ACLDriver(vdrivers, user);
				try {
					Acldriver = ((ACLDriverDaoimpl) DBManager.getInstance().getDao(DAOEnum.ACL_DRIVER_DAO)).delete(Acldriver);
					if (Acldriver == null) {
						opSuccess = false;
					}
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
			}
			LoadAclDriverDetails.getInstance().refresh();
		}
		return opSuccess;
	}

	public boolean[] deleteDriver(String[] driverIds) {
		Driver driver;
		boolean[] successStatus = new boolean[driverIds.length];
		java.util.Arrays.fill(successStatus, true);
		for (int i = 0; i < driverIds.length; i++) {
			Long driverId = Long.parseLong(StringUtils.stripCommas(driverIds[i].trim()));
			driver = LoadDriverDetails.getInstance().retrieve(driverId);
			try {
				driver = ((DriverDaoImp) DBManager.getInstance().getDao(
						DAOEnum.DRIVER_DAO)).delete(driver);
				if (driver == null) {
					successStatus[i] = false;
				} else {
					successStatus[i] = true;
					driver.setDeleted(true);
					updateDriverCache(driver);
				}
			} catch (OperationNotSupportedException e) {
				LOG.error(e);
			}
		}
		return successStatus;
	}
	
	/**
	 * Updating driver cache on any alteration on Drivers table
	 * @param driver
	 */
	private void updateDriverCache(Driver driver) {
		LoadDriverDetails.getInstance().cacheDriverRecords.put(driver.getId().getId(), driver);
	}
}
