package com.i10n.fleet.web.controllers;

import java.io.IOException;
import java.net.BindException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.i10n.fleet.util.StringUtils;

/**
 * 
 * Entry point for all the functions invoked from sasadmin page in the admin login.
 * 
 * @author vikram
 * 
 */
public class SasAdminController extends SimpleFormController {

	public enum SasAdminOperation {

		ADD_ROUTE("add_route"), EDIT_ROUTE("edit_route"), DELETE_ROUTE("delete_route");

		private static Map<String, SasAdminOperation> LOOKUP = new HashMap<String, SasAdminOperation>();
		private String val;

		private SasAdminOperation(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static SasAdminOperation get(String val) {
			return LOOKUP.get(val);
		}

		static {
			for (SasAdminOperation s : EnumSet.allOf(SasAdminOperation.class)) {
				LOOKUP.put(s.getVal(), s);
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
		ModelAndView modelAndView = processRequest(request,response);
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
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
	throws Exception {

		ModelAndView modelAndView = processRequest(request,response);
		return modelAndView;

	}

	/**
	 * 
	 * Switch case logic
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	private ModelAndView processRequest(HttpServletRequest request,HttpServletResponse response) throws IOException {
		ModelAndView modelAndView = null;

		String action = null;
		String operation = request.getParameter("command_type");
		RouteAdminOperations routeAdminOperations = new RouteAdminOperations();

		switch (SasAdminOperation.get(operation)) {
		case EDIT_ROUTE: {
			String routename = request.getParameter("routename");
			if (routeAdminOperations.editRoute(request)) {
				modelAndView = new ModelAndView(new RedirectView(
						getSuccessView()));
			}
			action =" HAS EDITTED ROUTE WITH ROUTENAME NO " + routename;
			StringUtils.insertLogs(action);

		}	break;

		default: {
		}

		}
		return modelAndView;
	}

}
