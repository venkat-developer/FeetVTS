package com.i10n.tools.skins.files;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.i10n.tools.skins.files.IFile.FileType;

/**
 * Implementation of {@link IFileSet}
 * 
 * @author sabarish
 * 
 */
public class FileSet implements IFileSet {
    private Map<FileType, Map<String, IFile>> m_files = new LinkedHashMap<FileType, Map<String, IFile>>();

    /**
     * See {@link IFileSet#addFile(IFile)}
     */
    public void addFile(IFile file) {
        Map<String, IFile> files = m_files.get(file.getFileType());
        if (null == files) {
            files = new LinkedHashMap<String, IFile>();
            m_files.put(file.getFileType(), files);
        }
        files.put(file.getCompleteFileName(), file);
    }

    /**
     * See {@link IFileSet#addAllFiles(IFile)}
     */
    public void addAllFiles(List<IFile> files) {
        for (IFile file : files) {
            addFile(file);
        }
    }

    /**
     * See {@link IFileSet#getFiles(FileType)}
     */
    public List<IFile> getFiles(FileType type) {
        List<IFile> result = new ArrayList<IFile>();
        Map<String, IFile> files = m_files.get(type);
        if (null != files) {
            result.addAll(files.values());
        }
        return result;
    }

    /**
     * See {@link IFileSet#getFile(FileType)}
     * 
     * @param type
     * @return
     */

    public IFile getFile(FileType type) {
        IFile result = null;
        Map<String, IFile> files = m_files.get(type);
        if (null != files && files.size() > 0) {
            result = files.get(files.keySet().toArray()[files.size() - 1]);
        }
        return result;
    }

}
