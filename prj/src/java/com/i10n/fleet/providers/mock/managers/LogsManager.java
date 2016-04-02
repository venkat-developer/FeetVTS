package com.i10n.fleet.providers.mock.managers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
 * Mock : Mock Data Manager for logs. This class will be removed in future.
 * 
 * @author Sabarish
 * 
 */
public class LogsManager extends AbstractDataManager implements ILogsManager {

    private static final String FILE_DOCUMENT = "/mock/logs.xml";
    private static final Logger LOG = Logger.getLogger(LogsManager.class);
    private static final long TIME_HOUR_MULTIPLIER = 3600000;
    private static final long TIME_DAY_MULTIPLIER = 86400000;
    private static final int TIME_HOURS_INDAY = 24;
    private static final int TIME_DAYS_INMONTH = 30;
    private static final int TIME_DEFAULT_TODAY_OFFSET = 6;
    private static final int TIME_DEFAULT_WEEK_OFFSET = 3;
    private static final int TIME_DEFAULT_MONTH_OFFSET = 10;
    private Document m_document = null;

    /**
     * @see AbstractDataManager#getDocumentName()
     */
    protected String getDocumentName() {
        return FILE_DOCUMENT;
    }

    /**
     * @see IDataManager#getData(IDataset)
     */
    public IDataset getData(IDataset options) {
        if (null == m_document) {
            getXMLDocument();
        }
        IDataset result = getParsedData(options);
        
        return result;
    }

    /**
     * Returns data prsed from the document and filters based on options.
     * 
     * @param options
     * @return
     */
    private IDataset getParsedData(IDataset options) {
        IDataset result = new Dataset();
        String user = options.getValue("filter.user");
        String userXPath = getUserXpath(user);
        List<Node> userNodes = XPathUtils.getNodes(m_document, userXPath);
        for (Node userNode : userNodes) {
            String userID = XPathUtils.getAttribute(userNode, "id");
            IDataset userDataset = new Dataset();
            List<Node> childNodes = XPathUtils.getNodes(userNode, "*");
            for (Node childNode : childNodes) {
                String name = childNode.getNodeName();
                if ("ips".equals(name)) {
                    if (null != user) {
                        userDataset.put("ips", getIPSData(childNode, options));
                    }
                }
                else {
                    String value = childNode.getTextContent();
                    userDataset.put(name, value);
                }
            }
            result.put(userID, userDataset);
        }
        
        return result;
    }

    /**
     * Returns IP Data
     * 
     * @param ipsNode
     * @param ips
     * @return
     */
    private IDataset getIPSData(Node ipsNode, IDataset options) {
        IDataset ipsData = new Dataset();
        String ips = options.getValue("filter.ips");
        String startdate = options.getValue("filter.startdate");
        String enddate = options.getValue("filter.enddate");
        String logXPath = getLogXPath(ips);
        List<Node> ipNodes = XPathUtils.getNodes(ipsNode, logXPath);
        for (Node ipNode : ipNodes) {
            if (null != ips) {
                IDataset ipData = getXMLDataset(ipNode);
                if (null != startdate && null != enddate) {
                    ipData = filterDataByDate(ipData.copy(), startdate, enddate);
                }
                ipsData.putAll(ipData);
            }
            else {
                IDataset ipData = new Dataset();
                ipData.put("ipaddr", XPathUtils.getNode(ipNode, "ipaddr")
                        .getTextContent());
                ipsData.put(XPathUtils.getAttribute(ipNode, "id").replace(".", "_"),
                        ipData);
            }
        }
        return ipsData;
    }

    @SuppressWarnings( {
            "unchecked", "deprecation"
    })
    private IDataset filterDataByDate(IDataset data, String startdate, String enddate) {
        Date startDate = new Date(startdate);
        Date endDate = new Date(enddate);
        for (String key : data.keySet()) {
            List<IDataset> logsData = (List<IDataset>) data.get(key + ".logs");
            List<IDataset> filteredData = new ArrayList<IDataset>();
            for (IDataset logs : logsData) {
                long date = Long.parseLong(logs.getValue("date"));
                Date curDate = new Date(date);
                if (curDate.after(startDate) && curDate.before(endDate)) {
                    filteredData.add(logs);
                }
            }
            data.put(key + ".logs", filteredData);
        }
        return data;
    }

    /**
     * XPath to get users from document
     * 
     * @param user
     * @return
     */
    private String getUserXpath(String user) {
        String userEval = "*";
        if (null != user && !user.equals("*")) {
            userEval = "@id='" + user + "'";
        }
        String xpath = "/logs/user[" + userEval + "]";
        return xpath;
    }

    /**
     * Returns XPath for getting logs from ips nodes
     * 
     * @param ips
     * @return
     */
    private String getLogXPath(String ips) {
        String xpath = "";

        if (null == ips || ips.equals("*")) {
            xpath = "ip[*]";
        }
        else {
            String ipArray[] = ips.split("\\Q|\\E");
            StringBuilder ipBuilder = new StringBuilder();
            for (int i = 0; i < ipArray.length; i++) {
                String ip = ipArray[i];
                ipBuilder.append("ip[@id='" + ip + "']");
                if (i != (ipArray.length - 1)) {
                    ipBuilder.append("|");
                }
            }
            xpath = ipBuilder.toString();
        }
        return xpath;
    }

    /**
     * decorates the xml data with appropriate start date based in the template
     * in the string
     * 
     * @param xmlString
     * @return
     */
    private String decorateXML(String xmlString) {
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Date startOfToday = new Date(new Date().getTime() - (hour * TIME_HOUR_MULTIPLIER));
        Date startOfWeek = new Date(startOfToday.getTime() - (week * TIME_DAY_MULTIPLIER));
        Date startOfMonth = new Date(startOfToday.getTime()
                - (TIME_DAYS_INMONTH * TIME_DAY_MULTIPLIER));
        Pattern timePattern = Pattern.compile("\\Q{\\E(\\w*)\\Q}\\E");
        Matcher matcher = timePattern.matcher(xmlString);
        while (matcher.find()) {
            Date startDate = null;
            String time = matcher.group(1);
            if ("week".equals(time)) {
                startDate = new Date(
                        startOfWeek.getTime()
                                + (new Random().nextInt(TIME_HOURS_INDAY) * TIME_HOUR_MULTIPLIER)
                                + (new Random().nextInt(TIME_DEFAULT_WEEK_OFFSET) * TIME_DAY_MULTIPLIER));
            }
            else if ("today".equals(time)) {
                startDate = new Date(startOfToday.getTime()
                        + (TIME_HOUR_MULTIPLIER * new Random()
                                .nextInt(TIME_DEFAULT_TODAY_OFFSET)));
            }
            else if ("month".equals(time)) {
                startDate = new Date(
                        startOfMonth.getTime()
                                + (new Random().nextInt(TIME_HOURS_INDAY) * TIME_HOUR_MULTIPLIER)
                                + (new Random().nextInt(TIME_DEFAULT_MONTH_OFFSET) * TIME_DAY_MULTIPLIER));
            }
            if (null != startDate) {
                xmlString = matcher.replaceFirst("" + startDate.getTime() + "");

            }
            else {
                xmlString = matcher.replaceFirst("");
            }
            matcher.reset(xmlString);
        }

        return xmlString;
    }

    /**
     * Detemplatizes xml document for dates and then creates a document out of
     * the result xml
     * 
     * @return
     */
    private Document getXMLDocument() {
        InputStream stream = LogsManager.class.getResourceAsStream(FILE_DOCUMENT);
        InputStream xmlStringStream = null;
        Document doc = null;
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            String xmlString = IOUtils.toString(stream);
            xmlStringStream = new ByteArrayInputStream(decorateXML(xmlString).getBytes());
            doc = docBuilder.parse(xmlStringStream);
        }
        catch (ParserConfigurationException e) {
            LOG
                    .error(
                            "Caught Parser Conf Exception while building Document Builder Factory",
                            e);
        }
        catch (SAXException e) {
            LOG.error("Caught SAX Exception while parsing vehicles document", e);
        }
        catch (IOException e) {
            LOG.error("Caught IO Exception while parsing vehicles document", e);
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
        return doc;
    }

}