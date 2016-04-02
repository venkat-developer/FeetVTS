package com.i10n.tools.skins.files;

/**
 * Represents a Freemarker Template File
 * 
 * @see UIFile
 * @author sabarish
 * 
 */
public class FTLFile extends UIFile {

    /**
     * See {@link IFile#getFileType()}
     */
    public FileType getFileType() {
        return FileType.FTL;
    }

}
