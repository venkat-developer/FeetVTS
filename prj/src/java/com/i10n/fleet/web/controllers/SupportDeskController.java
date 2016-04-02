package com.i10n.fleet.web.controllers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.i10n.db.entity.User;
import com.i10n.fleet.util.MailSender;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * @author Madhu Shree M
 *
 */
public class SupportDeskController  extends SimpleFormController {
	//private static Logger LOG = Logger.getLogger(MailSender.class);
	public enum SupportDesk {

		SUPPORT_DESK("support_desk");

		private static Map<String, SupportDesk> lookup = new HashMap<String, SupportDesk>();
		private String val;

		private SupportDesk(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static SupportDesk get(String val) {
			return lookup.get(val);
		}

		static {
			for (SupportDesk s : EnumSet.allOf(SupportDesk.class)) {
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

	private ModelAndView processRequest(HttpServletRequest request) {
		ModelAndView modelAndView = null;
		String operation = request.getParameter("command_type");

		switch (SupportDesk.get(operation)) {
		case SUPPORT_DESK:{
			String contents = request.getParameter("contents");
			String issue = request.getParameter("issue");
			User user=SessionUtils.getCurrentlyLoggedInUser();
			ArrayList<String> recipientsList = new ArrayList<String>();
			String message ="";
			String subject="Support Desk ";
			if(issue.equalsIgnoreCase("Support")){
				recipientsList.add("harikrishna.rao@harman.com");
				recipientsList.add("jagadishchandra.gutta@harman.com");
				subject=subject+" "+issue+" issue";
				message="<h5><font color=\"#87CEEB\"> ";
			}else{
				recipientsList.add("server.maintenance@i10n.com");
				if(issue.equalsIgnoreCase("Bug")){
					subject=subject+" "+issue;
					message="<h5><font color=\"#FF0000\"> ";
				}else if(issue.equalsIgnoreCase("Performance")){
					subject=subject+" "+issue+" issue";
					message="<h5><font color=\"#FF0000\"> ";
				}else  if(issue.equalsIgnoreCase("Suggestion")){
					subject=subject+" ";
					message="<h5><font color=\"#2E8B57\"> ";
				}else if(issue.equalsIgnoreCase("Anonymous")){
					subject=subject+" "+issue;
					message="<h5><font color=\"#2E8B57\"> ";
				}	
			} 
			String[] recipients = new String[recipientsList.size()];
			recipients = recipientsList.toArray(recipients);


			message=message+contents+" </font></h5> <br> User Name : "+user.getLogin();
			if (contents != null) {
				if (recipients.length != 0) {
					try {
						MailSender.postMail(recipients,subject, message, "text/html");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
		}
		return modelAndView;
	}
}