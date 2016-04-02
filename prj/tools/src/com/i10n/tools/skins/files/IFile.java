package com.i10n.tools.skins.files;

import java.io.File;

import com.i10n.tools.skins.beans.ILibrary;
import com.i10n.tools.skins.beans.IWidget;

/**
 * Represents a file that is needed for {@link IWidget} or a {@link ILibrary}
 * 
 * @author sabarish
 * 
 */
public interface IFile {
    /**
     * Returns a complete filename of the file i.e. with namespace and relative
     * filename
     * 
     * @see IFile#getFileName()
     * @see IFile#getNamespace()
     * @return
     */
    String getCompleteFileName();

    /**
     * Returns the namespace of the file. normally namespace is dependent on the
     * skin
     * 
     * @return
     */
    String getNamespace();

    /**
     * Returns the relative filename of the file. ie. without the namespace.
     * 
     * @return
     */
    String getFileName();

    /**
     * Checks whether such a file exists
     * 
     * @return
     */
    boolean exists();

    /**
     * Returns the type : {@link FileType} of the the file
     * 
     * @return
     */
    FileType getFileType();

    /**
     * Returns {@link File} representing this {@link IFile}
     * 
     * @return
     */
    File getFile();

    /**
     * Checks whether the file is a remote file ot not
     * 
     * @return
     */
    boolean isRemoteFile();

    /**
     * Returns if the file is mergeable or not.
     * 
     * @return
     */
    boolean isMergeable();

    /**
     * REturn if the file is compressible ot not,
     * 
     * @return
     */
    boolean isCompressible();

    /**
     * Enum representation of the Type of {@link IFile} Values : <li>CSS File :
     * CSS</li> <li>Javascript File : JS</li> <li>Freemarker Template File : FTL
     * </li>
     * 
     * @author sabarish
     * 
     */
    enum FileType {
        /**
         * CSS File Type
         * 
         * @see CSSFile
         */
        CSS,
        /**
         * Javascript File Type
         * 
         * @see JSFile
         */
        JS,
        /**
         * Freemarker Template File
         * 
         * @see FTLFile
         */
        FTL
    }
}
