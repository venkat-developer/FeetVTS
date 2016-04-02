package com.i10n.tools.skins.compress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.i10n.tools.skins.files.CSSFile;
import com.i10n.tools.skins.files.IFile;
import com.i10n.tools.skins.files.JSFile;
import com.i10n.tools.skins.files.UIFile;
import com.i10n.tools.skins.files.IFile.FileType;

/**
 * A Class to perform all the compression related tasks on {@link IFile}. Uses
 * {@link CSSFileCompressor} and {@link JSFileCompressor} to compress
 * {@link CSSFile} and {@link JSFile}. Currently only supports compression of
 * {@link JSFile} and {@link JSFile}
 * 
 * @author sabarish
 * 
 */
public class StaticFileCompressor {

    private static final String MIN_FILE_SUFFIX = "-min";

    private CSSFileCompressor m_cssCompressor = new CSSFileCompressor();
    private JSFileCompressor m_jsCompressor = new JSFileCompressor();

    /**
     * Compresses the given {@link IFile} and returns the compressed
     * {@link IFile}
     * 
     * @param file
     * @return
     */
    public IFile compress(IFile file) {
        IFile result = file;
        if (file.isCompressible()) {
            result = getMinFile(file);
        if (null != result) {
            doCompress(file, result);
        }
        }
        return result;
    }

    /**
     * Compresses the list of {@link IFile} and returns the list of compress
     * {@link IFile}. This is equivalent to calling {@link #compress(IFile)} on
     * each {@link IFile}
     * 
     * @param files
     * @return
     */
    public List<IFile> compress(List<IFile> files) {
        List<IFile> result = new ArrayList<IFile>();

        for (IFile file : files) {
            result.add(compress(file));
        }
        return result;
    }

    /**
     * Compresses {@link IFile} - src to {@link IFile} - dest
     * 
     * @param src
     * @param dest
     */
    private void doCompress(IFile src, IFile dest) {
        if (!isCompressed(src, dest)) {
            if (!dest.exists()) {
                dest.getFile().getParentFile().mkdirs();
            }
            if (FileType.CSS.equals(src.getFileType())) {
                m_cssCompressor.compress((CSSFile) src, ((CSSFile) dest));
            }
            else if (FileType.JS.equals(src.getFileType())) {
                m_jsCompressor.compress((JSFile) src, ((JSFile) dest));
            }

        }
    }

    /**
     * Returns the compresses {@link IFile} representing the given {@link IFile}
     * . <br/>
     * Note: This doesn't mean the the file represented the returned
     * {@link IFile} exists. Its just represents the file
     * 
     * @param file
     * @return
     */
    private IFile getMinFile(IFile file) {
        UIFile result = null;
        String suffix = null;
        if (FileType.CSS.equals(file.getFileType())) {
            result = new CSSFile();
            suffix = ".css";
        }
        else if (FileType.JS.equals(file.getFileType())) {
            result = new JSFile();
            suffix = ".js";
        }
        if (null != result) {
            String fileName = file.getFileName();
            int index = fileName.lastIndexOf(suffix);
            fileName = fileName.substring(0, index) + MIN_FILE_SUFFIX + suffix;
            result.setFileName(fileName);
        }
        return result;
    }

    /**
     * Checks whether {@link IFile} - src is older than {@link IFile} dest and
     * if yes, will return true signifying this file can be skipped for
     * compressing
     * 
     * @param src
     * @param dest
     * @return
     */
    private boolean isCompressed(IFile src, IFile dest) {
        boolean result = false;
        if (dest.getFile().exists()) {
            Date srcDate = new Date(src.getFile().lastModified());
            Date destDate = new Date(dest.getFile().lastModified());
            if (srcDate.before(destDate)) {
                result = true;
            }
        }
        return result;
    }
}
