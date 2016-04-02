package com.i10n.tools.quality;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.i10n.tools.exception.FleetToolsException;
import com.i10n.tools.utils.XPathUtils;

public abstract class AbstractQualityChecker {

    protected static final int LESSER_COMPARE_TYPE = 0;
    protected static final int GREATER_COMPARE_TYPE = 1;

    /**
     * Start processing the errors
     * 
     * @param errorFile
     * @param thresholdFile
     * @throws FleetToolsException
     */
    public void process(String errorFile, String thresholdFile)
            throws FleetToolsException {
        loadThreshold(thresholdFile);
        handleErrors(processErrorFile(errorFile));
    }

    /**
     * All the inheriting checkers except those processing xml file should
     * override this function. This function processes the error file to give
     * the list of errors for each file.
     * 
     * @param xmlFile
     * @return
     * @throws FleetToolsException
     */
    protected Map<String, Integer> processErrorFile(String xmlFile)
            throws FleetToolsException {
        InputStream xmlStream = null;
        Map<String, Integer> errorFiles = new HashMap<String, Integer>();
        try {
            xmlStream = new FileInputStream(xmlFile);
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document xmlDoc = docBuilder.parse(xmlStream);
            errorFiles.putAll(parseXML(xmlDoc));

        }
        catch (FileNotFoundException ex) {
            throw new FleetToolsException("Error File : " + xmlFile + " is missing", ex);
        }
        catch (SAXException ex) {
            throw new FleetToolsException("Caught SAXException while parsing xml file : "
                    + xmlFile, ex);
        }
        catch (IOException ex) {
            throw new FleetToolsException("Caught IOException while parsing xml file : "
                    + xmlFile, ex);
        }
        catch (ParserConfigurationException ex) {
            throw new FleetToolsException(
                    "Caught ParserConfigurationException while parsing  xml file : "
                            + xmlFile, ex);
        }
        finally {
            IOUtils.closeQuietly(xmlStream);
        }
        return errorFiles;
    }

    /**
     * All the inheriting checkers having XMl file as their error file shoudl
     * override this function.
     * 
     * @param xmlDoc
     * @return
     */
    protected Map<String, Integer> parseXML(Document xmlDoc) {
        String fileXpath = "/" + getNamespace() + "/file";
        List<Node> fileNodes = XPathUtils.getNodes(xmlDoc, fileXpath);
        Map<String, Integer> errorFiles = new HashMap<String, Integer>();
        for (Node fileNode : fileNodes) {
            List<Node> errorNodes = XPathUtils.getNodes(fileNode, "error");
            String fileName = XPathUtils.getAttribute(fileNode, "name");
            int currentErrorCount = errorNodes.size();
            Integer count = errorFiles.get(fileName);
            if (null == count) {
                errorFiles.put(fileName, currentErrorCount);
            }
            else {
                errorFiles.put(fileName, count.intValue() + currentErrorCount);
            }
        }
        return errorFiles;

    }

    /**
     * handling errors based on the errorfile-errorcount map and based on the
     * threshold property loaded.
     * 
     * @param errorFiles
     * @throws FleetToolsException
     */
    protected void handleErrors(Map<String, Integer> errorFiles)
            throws FleetToolsException {
        boolean isErrorState = false;
        String msg = "Errors reached above the Threshold Limit for the following files";
        for (Entry<String, Integer> entry : errorFiles.entrySet()) {
            String completefileName = entry.getKey();
            String fileName = completefileName.replaceFirst("\\Q"
                    + System.getProperty("user.dir") + "\\E", "");
            fileName = fileName.replaceAll("\\Q\\\\E", "/");
            Integer count = entry.getValue();
            String countStr = getProperties().getProperty(fileName);
            int prevCount = null == countStr ? 0 : Integer.parseInt(countStr);
            m_props.put(fileName, count.toString());
            boolean errorState = false;
            if (getCompareType() == GREATER_COMPARE_TYPE) {
                errorState = count.intValue() > prevCount;
            }
            else if (getCompareType() == LESSER_COMPARE_TYPE) {
                errorState = count.intValue() < prevCount;
            }
            if (errorState) {
                msg += "\n" + fileName + " : " + count + "(" + prevCount + ")";
                m_props.put(fileName, count.toString());
                isErrorState = true;
            }
        }
        if (isErrorState) {
            throw new FleetToolsException(msg);
        }
    }

    /**
     * Tells whether the required value should be greater or smaller than the
     * threshold value
     * 
     * @return
     */
    protected int getCompareType() {
        return GREATER_COMPARE_TYPE;
    }

    /**
     * Write the current properties to the file
     * 
     * @param fileName
     */
    public void writeProperties(String fileName) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            m_props.store(out, "Threshold File");
        }
        catch (FileNotFoundException e) {
            /* TODO : Handle Exception */
        }
        catch (IOException e) {
            /* TODO : Handle Exception */
        }
        finally {
            IOUtils.closeQuietly(out);
        }

    }

    /**
     * load threshold property file
     * 
     * @param thresholdFile
     */
    protected void loadThreshold(String thresholdFile) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(thresholdFile);
            getProperties().load(stream);
        }
        catch (FileNotFoundException ex) {

        }
        catch (IOException ex) {

        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    protected String getNamespace() {
        return m_namespace;
    }

    protected void setNamespace(String namespace) {
        m_namespace = namespace;
    }

    protected Properties getProperties() {
        return m_props;
    }

    protected void setProperties(Properties props) {
        m_props = props;
    }

    protected Properties m_props = new Properties();
    protected String m_namespace = "";

}
