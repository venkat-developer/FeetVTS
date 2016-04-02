package com.i10n.tools.skins.files;

import java.util.List;

import com.i10n.tools.skins.files.IFile.FileType;

/**
 * Represents a list of files : {@link IFile} grouped on {@link FileType}
 * 
 * @author sabarish
 * 
 */
public interface IFileSet {
    /**
     * Adds an {@link IFile} to the current set and arranges them based on
     * {@link FileType} for future retrieval
     * 
     * @param file
     */
    void addFile(IFile file);

    /**
     * Adds all the files in the given to the current set
     * 
     * @see #addFile(IFile)
     * @param file
     */
    void addAllFiles(List<IFile> files);

    /**
     * Returns all the files added of the type : {@link FileType} specified.
     * 
     * @param type
     * @return
     */
    List<IFile> getFiles(FileType type);

    /**
     * Returns only the latest added file for a given {@link FileType} . Useful
     * for {@link FTLFile}
     * 
     * @param type
     * @return
     */
    IFile getFile(FileType type);
}
