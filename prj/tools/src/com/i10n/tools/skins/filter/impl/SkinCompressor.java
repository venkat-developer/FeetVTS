package com.i10n.tools.skins.filter.impl;

import java.util.List;

import com.i10n.tools.skins.beans.ILibrary;
import com.i10n.tools.skins.beans.Library;
import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.beans.View;
import com.i10n.tools.skins.compress.StaticFileCompressor;
import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.skins.files.FileSet;
import com.i10n.tools.skins.files.IFile;
import com.i10n.tools.skins.files.IFileSet;
import com.i10n.tools.skins.files.IFile.FileType;
import com.i10n.tools.skins.filter.ISkinFilter;

/**
 * Compresses the merged files in the {@link Skin}
 * 
 * @author aravind
 * 
 */
public class SkinCompressor implements ISkinFilter {

    private StaticFileCompressor m_compressor = new StaticFileCompressor();
    private boolean m_compressEnabled = true;

    public SkinCompressor() {
        m_compressEnabled = "true".equals(SkinProperties
                .getProperty(SkinProperties.COMPRESS_ENABLED));
    }

    /**
     * Starts compressing the file as given in the {@link Skin} : input
     */
    public Skin filter(Skin input) throws SkinParseException {
        Skin result = input;
        if (m_compressEnabled) {
            List<View> views = result.getViews();
            for (View view : views) {
                view.setMergedFileSet(compress(view.getMergedFileSet()));
                List<ILibrary> libraries = view.getLibraries();
                for (ILibrary library : libraries) {
                    ((Library) library).setMergedFileSet(compress(library
                            .getMergedFileSet()));
                }
            }
            SkinDocumentApplier.applyMergedFilesOnSkin(result);
        }
        return result;
    }

    private IFileSet compress(IFileSet fileset) {
        FileSet compressedFileSet = new FileSet();
        List<IFile> jsFiles = fileset.getFiles(FileType.JS);
        List<IFile> compressedJSFiles = m_compressor.compress(jsFiles);
        compressedFileSet.addAllFiles(compressedJSFiles);

        List<IFile> cssFiles = fileset.getFiles(FileType.CSS);
        List<IFile> compressedCSSSFiles = m_compressor.compress(cssFiles);
        compressedFileSet.addAllFiles(compressedCSSSFiles);

        return compressedFileSet;
    }
}
