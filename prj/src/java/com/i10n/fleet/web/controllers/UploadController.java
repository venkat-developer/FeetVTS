package com.i10n.fleet.web.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.i10n.fleet.util.KMLAddressFetch;

/*
 * @author dharmaraju.v
 *
 */

@SuppressWarnings("unused")
public class UploadController extends SimpleFormController {

	File savedFile;
	String finalimage;
	private static Logger LOG = Logger.getLogger(UploadController.class);

	public enum Uploading {
		UPLOAD_KML("uploadkml");

		private static Map<String, Uploading> lookup = new HashMap<String, Uploading>();
		private String val;

		private Uploading(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static Uploading get(String val) {
			return lookup.get(val);
		}

		static {
			for (Uploading s : EnumSet.allOf(Uploading.class)) {
				lookup.put(s.getVal(), s);
			}
		}
	}

	/**
	 * Handles login/logout requests and checks redirects to appproaprate view.
	 * If its a logout request, it removes the session attribute and redirects
	 * to login page. If a login request handling is passed to
	 * {@link #onSubmit(Object)} which on success returns a {@link RedirectView}
	 * to {@link #getSuccessView()} page and then finally adds the currents user
	 * as a session attribute. This will be reimplemented once the user database
	 * is available.
	 *
	 * @see SimpleFormController#handleRequest(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;

	}

	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 *
	 */

	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 *
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;
	}

	@SuppressWarnings("rawtypes")
	private ModelAndView processRequest(HttpServletRequest request) {
		LOG.info("UPLOAIDNG : upaoadling file ");
		ModelAndView modelAndView = null;
		String action;

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("UPLOAD : isMultipart ?? "+isMultipart);
		if (!isMultipart) {
			LOG.error("File Not Uploaded");
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
					e.printStackTrace();
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
				if(domainName.equalsIgnoreCase(".png") || domainName.equalsIgnoreCase(".jpg")){
					finalimage = buffer.toString() + domainName;
					String catalinaHome = System.getenv("CATALINA_HOME").replace(";", "");
					savedFile = new File(catalinaHome+"/webapps/kmlimages/"+finalimage);
					//savedFile = new File("D:/Projects/FleetCheckV2/OfflineLogicChange/prj/src/static/kml/images/"+finalimage);
					LOG.info("IMAGE file  : absolute path is "+savedFile.getAbsolutePath());	
				}else{
					Date dateExtention = new Date ();
					//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh.mm.ss");
					finalimage = buffer.toString()+dateExtention.getTime()+domainName;
					//finalimage = buffer.toString() + domainName;
					String catalinaHome = System.getenv("CATALINA_HOME").replace(";", "");
					savedFile = new File(catalinaHome+"/webapps/kml/"+finalimage);
					//savedFile = new File("D:/Projects/FleetCheckV2/OfflineLogicChange/prj/src/static/kml/"+finalimage);
					LOG.info("KML file  : absolute path is "+savedFile.getAbsolutePath());	
				}

				try {
					file.transferTo(savedFile);
					LOG.info("UPLOAD : file transfered to "+savedFile.getAbsolutePath());
					KMLAddressFetch.processForKMLAddressInsertion(savedFile);
					/* *
					 * transferTo uses the tranfering a file location to destination
					 * 
					 */
				} catch (IllegalStateException e) {
					LOG.error("Error while transfering file",e);
				} catch (IOException e1) {
					LOG.error("Error while generating file",e1);
				}
			} 			
		}
		modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
		return modelAndView;
	}
}

