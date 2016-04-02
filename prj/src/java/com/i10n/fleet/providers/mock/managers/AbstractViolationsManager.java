package com.i10n.fleet.providers.mock.managers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.XPathUtils;

/**
 * Mock : Abstract Class for Managers of Violations Data.This class will be
 * removed in future.
 * 
 * @author Sabarish
 * 
 */
public abstract class AbstractViolationsManager extends AbstractDataManager {

    private Document m_document;
    private static final Logger LOG = Logger.getLogger(AbstractViolationsManager.class);
    private static final long DATE_MINUTE_MULTIPLIER = 60000;
    private static final long DATE_HOUR_MULTIPLIER = 3600000;
    private static final long DATE_DAY_MULTIPLIER = 86400000;
    private static final int DATE_HOURS_INDAY = 24;
    private static final int DATE_DAYS_INMONTH = 30;
    private static final int DATE_DEFAULT_TODAY_OFFSET = 6;
    private static final int DATE_DEFAULT_WEEK_OFFSET = 3;
    private static final int DATE_DEFAULT_MONTH_OFFSET = 10;

    /**
     * creates the document for the manager by detemplatizing the date
     * templates.Probable victim of Double Checked Locking. Should not be used
     * in prod.
     */
    private void createDocument() {
        InputStream stream = IdlePointsManager.class
                .getResourceAsStream(getDocumentName());
        InputStream xmlStringStream = null;
        Document doc = null;
        try {
            String xmlString = IOUtils.toString(stream);
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            xmlString = decorateXMLData(xmlString);
            xmlStringStream = new ByteArrayInputStream(xmlString.getBytes());
            doc = docBuilder.parse(xmlStringStream);
        }
        catch (ParserConfigurationException e) {
            LOG
                    .error(
                            "Caught Parser Configuration Exception while building Document Builder Factory",
                            e);
        }
        catch (SAXException e) {
            LOG.error("Caught SAXException while parsing vehicles document", e);
        }
        catch (IOException e) {
            LOG.error("Caught IOException while parsing vehicles document", e);
        }
        finally {
            IOUtils.closeQuietly(stream);
            IOUtils.closeQuietly(xmlStringStream);
        }
        synchronized (this) {
            if (null == m_document) {
                m_document = doc;
            }
        }
    }

    /**
     * decorates the xml data with appropriate start date and end date based in
     * the template in the string.
     * 
     * @param xmlString
     * @return
     */
    private String decorateXMLData(String xmlString) {
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        Date startOfToday = getStartOfToday();
        Date startOfWeek = new Date(startOfToday.getTime() - (week * DATE_DAY_MULTIPLIER));
        Date startOfMonth = new Date(startOfToday.getTime()
                - (DATE_DAYS_INMONTH * DATE_DAY_MULTIPLIER));
        Pattern timePattern = Pattern
                .compile("\\Qstartendtime=\"{\\E(\\w*)\\Q:\\E(\\d*)\\Q}\"\\E");
        Matcher matcher = timePattern.matcher(xmlString);
        while (matcher.find()) {
            Date startDate = null;
            String time = matcher.group(1);
            String diffStr = matcher.group(2);
            int diff = Integer.parseInt(diffStr);
            if ("today".equals(time)) {
                startDate = new Date(startOfToday.getTime()
                        + (DATE_HOUR_MULTIPLIER * new Random()
                                .nextInt(DATE_DEFAULT_TODAY_OFFSET)));
            }
            else if ("week".equals(time)) {
                startDate = new Date(
                        startOfWeek.getTime()
                                + (new Random().nextInt(DATE_HOURS_INDAY) * DATE_HOUR_MULTIPLIER)
                                + (new Random().nextInt(DATE_DEFAULT_WEEK_OFFSET) * DATE_DAY_MULTIPLIER));
            }
            else if ("month".equals(time)) {
                startDate = new Date(
                        startOfMonth.getTime()
                                + (new Random().nextInt(DATE_HOURS_INDAY) * DATE_HOUR_MULTIPLIER)
                                + (new Random().nextInt(DATE_DEFAULT_MONTH_OFFSET) * DATE_DAY_MULTIPLIER));
            }
            if (null != startDate) {
                Date endDate = new Date(startDate.getTime()
                        + (diff * DATE_MINUTE_MULTIPLIER));
                xmlString = matcher.replaceFirst("startdate=\"" + startDate.getTime()
                        + "\" enddate=\"" + endDate.getTime() + "\"");

            }
            else {
                xmlString = matcher.replaceFirst("");
            }
            matcher.reset(xmlString);
        }

        return xmlString;
    }

    /**
     * @see IDataManager#getData(IDataset)
     */
    public IDataset getData(IDataset options) {
        if (null == m_document) {
            createDocument();
        }
        return getParsedData(m_document, options);
    }

    /**
     * returns a dataset from the document . also applies the filter given in
     * the options
     * 
     * @param doc
     * @param options
     * @return
     */
    private IDataset getParsedData(Document doc, IDataset options) {
        IDataset result = new Dataset();
        String vehicleID = options.getValue("filter.vehicleid");
        String xPath = null;
        if (null == vehicleID) {
            xPath = "//vehicle";
        }
        else {
            xPath = "//vehicle[@id='" + vehicleID + "']";
        }

        if (null != xPath) {
            List<Node> nodes = XPathUtils.getNodes(doc, xPath);
            for (Node node : nodes) {
                result.putAll(getXMLDataset(node));
            }
        }
        return filterData(result, options);
    }

    /**
     * filters the data based on the startdate and enddate given in the options
     * 
     * @param data
     * @param options
     * @return
     */
    @SuppressWarnings( {
            "deprecation", "unchecked"
    })
    private IDataset filterData(IDataset data, IDataset options) {
        IDataset result = new Dataset();
        String startDateStr = options.getValue("filter.startdate");
        String endDateStr = options.getValue("filter.enddate");
        Date startDate = null;
        Date endDate = null;
        Date startOfToday = getStartOfToday();
        if (null == startDateStr) {
            startDate = startOfToday;
            endDate = new Date(startDate.getTime() + DATE_DAY_MULTIPLIER);
        }
        else if (null == endDateStr) {
            startDate = new Date(startDateStr);
            endDate = new Date(startOfToday.getTime() + DATE_DAY_MULTIPLIER);
        }
        else {
            startDate = new Date(startDateStr);
            endDate = new Date(endDateStr);
        }
        for (Entry<String, Object> entry : data.entrySet()) {
            String vehicleID = entry.getKey();
            List<IDataset> vehicleListData = (List<IDataset>) entry.getValue();
            List<IDataset> resultList = new ArrayList<IDataset>();
            for (IDataset vehicleData : vehicleListData) {
                Date entryStartDate = new Date(Long.parseLong(vehicleData
                        .getValue("startdate")));
                Date entryEndDate = new Date(Long.parseLong(vehicleData
                        .getValue("enddate")));
                if ((entryStartDate.after(startDate) && entryEndDate.before(endDate))) {
                    resultList.add(vehicleData);
                }
            }
            result.put(vehicleID, resultList);
        }
        return result;
    }

    /**
     * Returns and object of {@link Date} representing the satrt of today.
     * 
     * @return
     */
    private Date getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Date startOfToday = new Date(new Date().getTime() - (hour * DATE_HOUR_MULTIPLIER));
        return startOfToday;
    }
}
