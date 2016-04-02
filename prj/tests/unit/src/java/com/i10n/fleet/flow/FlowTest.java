package com.i10n.fleet.flow;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import com.i10n.fleet.test.AbstractFleetWebSpringTests;
import com.i10n.fleet.web.controllers.CascadedFormController;
import com.i10n.fleet.web.controllers.ViewController;

/**
 * Tests the application flow
 * 
 * @author sabarish
 * 
 */
public class FlowTest extends AbstractFleetWebSpringTests {

    /**
     * Tests the reachability of dashboard page
     */
    @Test
    public void testDashBoardReachability() {
        SimpleUrlHandlerMapping mapping = (SimpleUrlHandlerMapping) getApplicationContext()
                .getBean("urlMapping");
        MockHttpServletRequest request = getSessionRequest("GET", "/view/dashboard/");

        try {
            ViewController controller = (ViewController) (mapping.getHandler(request)
                    .getHandler());
            assertEquals(controller.getViewName(), "dashboard");
        }
        catch (Exception ex) {
            fail("Failed retrieveing the controller from the urlMapping");
        }

    }

    /**
     * Tests the reachability of livetrack page
     */
    @Test
    public void testLiveTrackReachability() {
        SimpleUrlHandlerMapping mapping = (SimpleUrlHandlerMapping) getApplicationContext()
                .getBean("urlMapping");
        MockHttpServletRequest request = getSessionRequest("GET", "/view/livetrack/Hello");
        try {
            ViewController controller = (ViewController) (mapping.getHandler(request)
                    .getHandler());
            assertEquals(controller.getViewName(), "livetrack");
        }
        catch (Exception ex) {
            fail("Failed retrieveing the controller from the urlMapping");
        }
    }

    /**
     * Tests the reachability of login page
     */
    @Test
    public void testLoginFormReachability() {
        SimpleUrlHandlerMapping mapping = (SimpleUrlHandlerMapping) getApplicationContext()
                .getBean("urlMapping");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/form/login/");
        request.setMethod("POST");
        request.setParameter("user", "testuser");
        request.setParameter("password", "testuser");
        try {
            Object controller = mapping.getHandler(request).getHandler();
            assertNotNull(controller);
            assertTrue(controller instanceof CascadedFormController);
            ModelAndView mview = ((CascadedFormController) controller).handleRequest(
                    request, new MockHttpServletResponse());
            RedirectView view = (RedirectView) mview.getView();
            assertTrue(view.getUrl().equals("/fleet/view/dashboard/"));
        }
        catch (Exception ex) {
            fail("Failed retrieveing the controller from the urlMapping");
        }
    }

    public MockHttpServletRequest getSessionRequest(String method, String url) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, url);
        WebUtils.setSessionAttribute(request, "currentuser", "testuser");
        return request;
    }
}
