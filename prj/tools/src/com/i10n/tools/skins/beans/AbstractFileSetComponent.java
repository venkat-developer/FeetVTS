package com.i10n.tools.skins.beans;

import java.util.List;

import com.i10n.tools.skins.files.CSSFile;
import com.i10n.tools.skins.files.FileSet;
import com.i10n.tools.skins.files.IFile;
import com.i10n.tools.skins.files.IFileSet;
import com.i10n.tools.skins.files.JSFile;

/**
 * Provides default implementations for all the {@link FileSet} based components
 * like {@link Widget} {@link Library} {@link View}
 * 
 * @author sabarish
 * 
 */
public class AbstractFileSetComponent {

    private IFileSet m_fileset = new FileSet();
    private IFileSet m_mergedFileset = new FileSet();

    /**
     * Adds a file to the library fileset and checks whether the file is
     * instance of {@link CSSFile} or {@link JSFile}
     * 
     * @see Library#addAllFiles(List)
     * @param file
     */
    public void addFile(IFile file) {
        if (file instanceof JSFile || file instanceof CSSFile) {
            m_fileset.addFile(file);
        }
    }

    /**
     * Adds a merged file to the library fileset and checks whether the file is
     * instance of {@link CSSFile} or {@link JSFile}
     * 
     * @param file
     */
    public void addMergedFile(IFile file) {
        m_mergedFileset.addFile(file);
    }

    /**
     * Adds all the merged files in the list to the library fileset and checks
     * whether the files are instance of {@link CSSFile} or {@link JSFile}
     * 
     * @see #addMergedFile(IFile)
     * @param file
     */
    public void addAllMergedFiles(List<IFile> files) {
        for (IFile file : files) {
            addMergedFile(file);
        }
    }

    /**
     * Adds all the files in the list to the library fileset and checks whether
     * the files are instance of {@link CSSFile} or {@link JSFile}
     * 
     * @see #addFile(IFile)
     * @param file
     */
    public void addAllFiles(List<IFile> files) {
        for (IFile file : files) {
            addFile(file);
        }
    }

    /**
     * See {@link #getFileSet()}
     */
    public IFileSet getFileSet() {
        return m_fileset;
    }

    /**
     * See {@link #getFileSet()}
     */
    public IFileSet getMergedFileSet() {
        return m_mergedFileset;
    }

    /**
     * Sets current component's {@link IFileSet}
     */
    public void setFileSet(IFileSet fileset) {
        m_fileset = fileset;
    }

    /**
     * Sets current component's merged {@link IFileSet}
     */
    public void setMergedFileSet(IFileSet fileset) {
        m_mergedFileset = fileset;
    }
}
