package com.i10n.tools.skins.filter.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.i10n.tools.skins.SkinConstants;
import com.i10n.tools.skins.beans.ILibrary;
import com.i10n.tools.skins.beans.Library;
import com.i10n.tools.skins.beans.Skin;
import com.i10n.tools.skins.beans.View;
import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.skins.exception.SkinParseException;
import com.i10n.tools.skins.factory.SkinFactory;
import com.i10n.tools.skins.files.CSSFile;
import com.i10n.tools.skins.files.IFile;
import com.i10n.tools.skins.files.IFileSet;
import com.i10n.tools.skins.files.JSFile;
import com.i10n.tools.skins.files.UIFile;
import com.i10n.tools.skins.files.IFile.FileType;
import com.i10n.tools.skins.filter.ISkinFilter;
import com.i10n.tools.skins.merge.FileMerger;

/**
 * Merges the {@link Skin} 's CSS files and JS Files and applies it to the Skin
 * 
 * @author aravind
 * 
 */
public class SkinMerger implements ISkinFilter {

    private SkinFactory m_skinFactory = new SkinFactory();
    private FileMerger m_fileMerger = new FileMerger();
    private boolean m_mergeEnabled = true;

    private static final String KEY_MERGEABLE = "merge";
    private static final String KEY_NONMERGEABLE = "unmerge";

    public SkinMerger() {
        m_mergeEnabled = "true".equals(SkinProperties
                .getProperty(SkinProperties.MERGE_ENABLED));
    }

    /**
     * Starts merging the file as given in the {@link Document} : input. Will
     * check if merge processing is enabled before merging
     */
    public Skin filter(Skin input) throws SkinParseException {
        Skin result = input;
        if (m_mergeEnabled) {
            result = m_skinFactory.manageSkin(result);
            result = merge(input);
            SkinDocumentApplier.applyMergedFilesOnSkin(result);
        }
        return result;
    }

    /**
     * merges the {@link IFile} in input
     * 
     * @param input
     * @return
     */
    private Skin merge(Skin input) {
        Skin result = input;
        List<View> views = result.getViews();
        for (View view : views) {
            view = mergeViewLibraries(result, view);
            view = mergeViewFiles(result, view);
        }
        return result;
    }

    /**
     * Merges all the files in {@link View} in the given {@link Skin}
     * 
     * @param skin
     * @param view
     * @return
     */
    private View mergeViewFiles(Skin skin, View view) {
        View result = view;
        List<IFile> mergedFiles = mergeFileSet(skin, view, view.getFileSet(), "view_"
                + view.getName() + "_merged");
        view.addAllMergedFiles(mergedFiles);
        return result;
    }

    /**
     * Merges all the {@link IFile} in {@link ILibrary} of the given
     * {@link View}
     * 
     * @param skin
     * @param view
     * @return
     */
    private View mergeViewLibraries(Skin skin, View view) {
        View result = view;
        List<ILibrary> libraries = result.getLibraries();
        for (ILibrary library : libraries) {
            List<IFile> mergedFiles = mergeFileSet(skin, view, library.getFileSet(),
                    "lib_" + library.getName() + "_merged");
            ((Library) library).addAllMergedFiles(mergedFiles);
        }
        return result;
    }

    /**
     * Merges the {@link CSSFile} and {@link JSFile} in the given [@link
     * {@link IFileSet}
     * 
     * @param skin
     * @param view
     * @param fileset
     * @param suffix
     * @return
     */
    private List<IFile> mergeFileSet(Skin skin, View view, IFileSet fileset, String suffix) {
        List<IFile> result = new ArrayList<IFile>();
        List<IFile> cssFiles = fileset.getFiles(FileType.CSS);
        if (cssFiles.size() > 0) {
            Map<String, List<IFile>> fileList = getMergeableFiles(cssFiles);
            List<IFile> mergeableFiles = fileList.get(KEY_MERGEABLE);
            if (mergeableFiles.size() > 0) {
            File mergedCSSFile = new File(getMergedFileOutDirectory(skin, view)
                    + SkinConstants.DELIMITER + suffix + ".css");
                m_fileMerger.merge(mergeableFiles, mergedCSSFile);
                IFile mergedCSSUIFile = createIFile(skin, view, suffix + ".css",
                        FileType.CSS);

            result.add(mergedCSSUIFile);
        }
            result.addAll(fileList.get(KEY_NONMERGEABLE));
        }
        List<IFile> jsFiles = fileset.getFiles(FileType.JS);
        if (jsFiles.size() > 0) {
            Map<String, List<IFile>> fileList = getMergeableFiles(jsFiles);
            List<IFile> mergeableFiles = fileList.get(KEY_MERGEABLE);
            if (mergeableFiles.size() > 0) {
            File mergedJSFile = new File(getMergedFileOutDirectory(skin, view)
                    + SkinConstants.DELIMITER + suffix + ".js");
                m_fileMerger.merge(mergeableFiles, mergedJSFile);
                IFile mergedJSUIFile = createIFile(skin, view, suffix + ".js",
                        FileType.JS);
            result.add(mergedJSUIFile);
        }
            result.addAll(fileList.get(KEY_NONMERGEABLE));
        }
        return result;
    }

    private Map<String, List<IFile>> getMergeableFiles(List<IFile> files) {
        Map<String, List<IFile>> result = new LinkedHashMap<String, List<IFile>>();
        result.put(KEY_NONMERGEABLE, new ArrayList<IFile>());
        result.put(KEY_MERGEABLE, new ArrayList<IFile>());
        for (IFile file : files) {
            if (file.isMergeable()) {
                result.get(KEY_MERGEABLE).add(file);
            }
            else {
                result.get(KEY_NONMERGEABLE).add(file);
            }
        }
        return result;
    }

    /**
     * Creates a IFile based on the FileType passed
     * 
     * @param skin
     * @param view
     * @param suffix
     * @param filetype
     * @return
     */
    private IFile createIFile(Skin skin, View view, String suffix, FileType filetype) {
        UIFile file = null;
        if (FileType.JS.equals(filetype)) {
            file = new JSFile();
        }
        else if (FileType.CSS.equals(filetype)) {
            file = new CSSFile();
        }
        if (null != file) {
            file.setFileName(SkinConstants.STATIC_DIR_REF + SkinConstants.DELIMITER
                    + getRelativeMergedSetPath(skin, view) + SkinConstants.DELIMITER
                    + suffix);
        }
        return file;
    }

    private String getMergedFileOutDirectory(Skin skin, View view) {
        String result = SkinProperties.getProperty(SkinProperties.STATIC_DIR_KEY);
        result = result + SkinConstants.DELIMITER + getRelativeMergedSetPath(skin, view);
        return result;
    }

    private String getRelativeMergedSetPath(Skin skin, View view) {
        return "skins" + SkinConstants.DELIMITER + skin.getName()
                + SkinConstants.DELIMITER + SkinConstants.VIEWS_PATH
                + SkinConstants.DELIMITER + view.getName() + SkinConstants.DELIMITER
                + "mergedset";
    }
}
