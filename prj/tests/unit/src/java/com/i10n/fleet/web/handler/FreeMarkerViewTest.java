package com.i10n.fleet.web.handler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.web.servlet.view.freemarker.FleetFreeMarkerView;

import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;

/**
 * Tests freemarker view for handling compressed/singleline/normal output
 * 
 * @author sabarish
 * 
 */
public class FreeMarkerViewTest extends AbstractFleetTestCase {
    @Test
    public void testViewNormalHandling() {

        Configuration config = new Configuration();
        config.setSharedVariable("compression", new SimpleScalar("false"));
        config.setSharedVariable("single_line", new SimpleScalar("false"));
        config.setLocale(Locale.ENGLISH);
        doTestBasedOnConfig(config, "/freemarkerview/output.txt");
    }

    @Test
    public void testViewCompressionHandling() {

        Configuration config = new Configuration();
        config.setSharedVariable("compression", new SimpleScalar("true"));
        config.setSharedVariable("single_line", new SimpleScalar("false"));
        config.setLocale(Locale.ENGLISH);
        doTestBasedOnConfig(config, "/freemarkerview/output.compress.txt");
    }

    @Test
    public void testViewSIngleLineHandling() {

        Configuration config = new Configuration();
        config.setSharedVariable("compression", new SimpleScalar("true"));
        config.setSharedVariable("single_line", new SimpleScalar("true"));
        config.setLocale(Locale.ENGLISH);
        doTestBasedOnConfig(config, "/freemarkerview/output.singleline.txt");
    }

    @Test
    public void testContentTypeHandling() {
        Configuration config = new Configuration();
        config.setSharedVariable("compression", new SimpleScalar("true"));
        config.setSharedVariable("single_line", new SimpleScalar("false"));
        config.setLocale(Locale.ENGLISH);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "input");
        MockHttpServletResponse response = new MockHttpServletResponse();
        IDataset dataset = new Dataset();
        IDataset innerSet = new Dataset();
        innerSet.put("value", "World");
        dataset.put("entry", innerSet);
        FleetFreeMarkerView view = new FleetFreeMarkerView();
        File inputFile = new File(getClass().getResource(
                "/freemarkerview/input.encoded.ftm").getFile());
        try {
            config.setDirectoryForTemplateLoading(inputFile.getParentFile());
        }
        catch (IOException e) {
            fail("Caught IOException while setting config directory : \n"
                    + e.getMessage());
        }
        view.setConfiguration(config);
        view.setBeanName("input.encoded");
        view.setUrl("input.encoded.ftm");
        try {
            view.render(dataset, request, response);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while rendering view /freemarkerview/input.encoded.ftm : \n"
                    + e.getMessage());
        }
        try {
            assertEquals(response.getContentType(), "text/xml");
            assertEquals(response.getContentAsString(), IOUtils.toString(getClass()
                    .getResourceAsStream("/freemarkerview/output.encoded.txt")));
        }
        catch (UnsupportedEncodingException e) {
            fail("Caught UnsupportedEncodingException comparing response with expected reponse : \n"
                    + e.getMessage());
        }
        catch (IOException e) {
            fail("Caught IOException comparing response with expected reponse : \n"
                    + e.getMessage());
        }
    }

    public void doTestBasedOnConfig(Configuration config, String output) {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "input");
        MockHttpServletResponse response = new MockHttpServletResponse();
        IDataset dataset = new Dataset();
        IDataset innerSet = new Dataset();
        innerSet.put("value", "World");
        dataset.put("entry", innerSet);
        FleetFreeMarkerView view = new FleetFreeMarkerView();
        File inputFile = new File(getClass().getResource("/freemarkerview/input.ftm")
                .getFile());
        try {
            config.setDirectoryForTemplateLoading(inputFile.getParentFile());
        }
        catch (IOException e) {
            fail("Caught IOException while setting config directory : \n"
                    + e.getMessage());
        }
        view.setConfiguration(config);
        view.setBeanName("input");
        view.setUrl("input.ftm");
        try {
            view.render(dataset, request, response);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception while rendering view /freemarkerview/input.ftm : \n"
                    + e.getMessage());
        }
        try {
            assertEquals(response.getContentAsString(), IOUtils.toString(getClass()
                    .getResourceAsStream(output)));
        }
        catch (UnsupportedEncodingException e) {
            fail("Caught UnsupportedEncodingException comparing response with expected reponse : \n"
                    + e.getMessage());
        }
        catch (IOException e) {
            fail("Caught IOException comparing response with expected reponse : \n"
                    + e.getMessage());
        }
    }

}
