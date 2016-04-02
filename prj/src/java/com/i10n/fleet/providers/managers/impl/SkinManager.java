package com.i10n.fleet.providers.managers.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.managers.ISkinManager;
import com.i10n.fleet.skins.ISkinFactory;
import com.i10n.fleet.skins.SkinFactory;

/**
 * Generates a {@link Dataset} that represents the informations(ie their xml
 * docs) for each skin after parsing the skin xml's
 * 
 * @author sabarish
 * 
 */
public class SkinManager implements ISkinManager {

    private ISkinFactory m_skinFactory = new SkinFactory();
    private Dataset m_skinDataset = new Dataset();

    private static final String SKINCONFIG_RESOURCE = "/skins/skins.xml";

    private static final Logger LOG = Logger.getLogger(SkinManager.class);

    /**
     * Returns a generated {@link Dataset}
     */
    public Dataset getSkin() {
        synchronized (m_skinDataset) {
            if (m_skinDataset.isEmpty()) {
                m_skinDataset = loadSkins();
            }
        }
        return m_skinDataset;
    }

    public void reload() {
        synchronized (m_skinDataset) {
            m_skinDataset = loadSkins();
        }
    }

    private Dataset loadSkins() {
        Dataset dataset = new Dataset();
        InputStream stream = getClass().getResourceAsStream(SKINCONFIG_RESOURCE);
        if (null != stream) {
            try {
                DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = docBuilder.parse(stream);
                dataset.putAll((m_skinFactory.getSkins(doc)));
            }
            catch (ParserConfigurationException e) {
                LOG
                        .error(
                                "Caught Parser Configuration Exception while building Document Builder Factory",
                                e);
            }
            catch (SAXException e) {
                LOG.error("Caught SAXException while parsing SkinConfig file:"
                        + SKINCONFIG_RESOURCE, e);
            }
            catch (IOException e) {
                LOG.error("Caught IOException while parsing SkinConfig file:"
                        + SKINCONFIG_RESOURCE, e);
            }
            finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return dataset;
    }

    public void setSkinFactory(ISkinFactory factory) {
        m_skinFactory = factory;
    }

    public ISkinFactory getSkinFactory() {
        return m_skinFactory;
    }

}
