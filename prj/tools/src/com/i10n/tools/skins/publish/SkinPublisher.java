package com.i10n.tools.skins.publish;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.i10n.tools.skins.SkinConstants;
import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.utils.XMLUtils;

/**
 * Publishes the skin to the outputstream
 * 
 * @author sabarish
 * 
 */
public class SkinPublisher {

    private static final Logger LOG = Logger.getLogger(SkinPublisher.class);

    /**
     * Publishes the {@link Skin} : skin to the FileOutputStream
     * 
     * @param skin
     */
    public void publish(Skin skin) {
        Document doc = skin.getDocument();
        String xmlPath = getSkinPath(skin);
        OutputStream stream = null;
        try {
            LOG.debug("Publishing Skin : " + skin.getName() + " to " + xmlPath);
            stream = new FileOutputStream(xmlPath);
            IOUtils.write(XMLUtils.documentToString(doc), stream);
        }
        catch (FileNotFoundException e) {
            LOG.error("Caught FileNotFoundException while writing the skin : "
                    + skin.getName(), e);
        }
        catch (IOException e) {
            LOG.error("Caught IOException while writing the skin : " + skin.getName(), e);
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private String getSkinPath(Skin skin) {
        String result = SkinProperties.getProperty(SkinProperties.SKIN_DIR_KEY)
                + SkinConstants.DELIMITER + skin.getName() + SkinConstants.DELIMITER
                + "skin.out.xml";
        return result;
    }
}
