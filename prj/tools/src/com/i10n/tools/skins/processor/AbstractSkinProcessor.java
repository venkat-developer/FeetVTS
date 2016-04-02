package com.i10n.tools.skins.processor;

import java.io.InputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.i10n.tools.utils.XMLUtils;

/**
 * Abstract Class for Skin Processors. This will contain behaviors common across
 * {@link ISkinProcessor}
 * 
 * @author sabarish
 * 
 */
public abstract class AbstractSkinProcessor implements ISkinProcessor {

    private static final Logger LOG = Logger.getLogger(AbstractSkinProcessor.class);

    /**
     * Transforms Document given in input with the xslt file path given
     * 
     * @param input
     * @param xslt
     * @return
     */
    protected Document transformDocument(Document input, String xslt) {
        Document result = input;
        InputStream xsltStream = null;
        try {
            xsltStream = getClass().getResourceAsStream("/xslt/" + xslt);
            result = XMLUtils.transformToDocument(new StreamSource(xsltStream),
                    new DOMSource(result));
        }
        catch (TransformerException e) {
            e.printStackTrace();
            LOG.error(
                    "Caught TransformerException while transforing document with xslt - "
                            + xslt, e);
        }
        finally {
            IOUtils.closeQuietly(xsltStream);
        }
        return result;
    }
}
