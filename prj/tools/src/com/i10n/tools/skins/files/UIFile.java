package com.i10n.tools.skins.files;

import java.io.File;

import com.i10n.tools.skins.SkinConstants;
import com.i10n.tools.skins.config.SkinProperties;

/**
 * Abstract implementation of IFile for {@link CSSFile} , {@link JSFile} and
 * {@link FTLFile}
 * 
 * @author sabarish
 * 
 */
public abstract class UIFile implements IFile {

    private String m_fileName;
    private String m_namespace;
    private File m_file;
    private boolean m_mergeable = true;
    private boolean m_compressible = true;

    /**
     * See {@link IFile#exists()}
     */
    public boolean exists() {
        return getFile().exists();
    }

    /**
     * See {@link IFile#getFileName()}
     */
    public String getFileName() {
        return m_fileName;
    }

    /**
     * See {@link IFile#getCompleteFileName()}
     */
    public String getCompleteFileName() {
        return m_namespace + SkinConstants.DELIMITER + m_fileName;
    }

    /**
     * See {@link IFile#getNamespace()}
     */
    public String getNamespace() {
        return m_namespace;
    }

    /**
     * Sets the filename.
     * 
     * @param fileName
     */
    public void setFileName(String fileName) {
        m_fileName = fileName;
        if (fileName.contains(SkinConstants.STATIC_DIR_REF)) {
            fileName = fileName.substring("@STATIC_DIR@/".length());
        }
        String path = SkinProperties.getProperty(SkinProperties.STATIC_DIR_KEY);
        m_file = new File(path + "/" + fileName);
    }

    /**
     * Returns a {@link File} representing this IFile
     * 
     * @return
     */
    public File getFile() {
        return m_file;
    }

    /**
     * Sets the current namespace
     * 
     * @param namespace
     */
    public void setNameSpace(String namespace) {
        m_namespace = namespace;
    }

    public boolean isRemoteFile() {
        return (m_fileName.startsWith("http://") || m_fileName.startsWith("https://"));
    }

    public boolean isMergeable() {
        return m_mergeable && !isRemoteFile();
    }

    public boolean isCompressible() {
        return m_compressible && !isRemoteFile();
    }

    public void setMergeable(boolean mergeable) {
        m_mergeable = mergeable;
    }

    public void setCompressible(boolean compressible) {
        m_compressible = compressible;
    }

    /**
     * Returns a new instance of : <li>{@link CSSFile}</li> <li>{@link JSFile}</li>
     * <li>{@link FTLFile}</li> based on the {@link FileType} specified.
     * 
     * @param type
     * @return
     */
    public static UIFile getNewInstanceByType(FileType type) {
        UIFile file = null;
        if (FileType.CSS.equals(type)) {
            file = new CSSFile();
        }
        else if (FileType.JS.equals(type)) {
            file = new JSFile();
        }
        else if (FileType.FTL.equals(type)) {
            file = new FTLFile();
        }
        return file;
    }

}
