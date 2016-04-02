package com.i10n.tools.skins.beans;

import com.i10n.tools.skins.files.CSSFile;
import com.i10n.tools.skins.files.IFileSet;
import com.i10n.tools.skins.files.JSFile;

/**
 * Represents a library for the skin.The Library supports only {@link CSSFile}
 * and {@link JSFile} to added.
 * 
 * @author sabarish
 * 
 */
public interface ILibrary {
    /**
     * Returns the name of the library
     * 
     * @return
     */
    public String getName();

    /**
     * Returns the {@link IFileSet} representing all the files in the library.
     * 
     * @return
     */
    public IFileSet getFileSet();

    /**
     * Returns the {@link IFileSet} representing all the merged files in the
     * library.
     * 
     * @return
     */
    public IFileSet getMergedFileSet();
}
