package com.i10n.tools.skins.merge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.i10n.tools.skins.files.IFile;

/**
 * Merges the {@link List} of {@link IFile} to the output {@link File}. If the
 * outputFile is newer than all the files to be merged merge is skipped
 * 
 * @author sabarish
 * 
 */
public class FileMerger {

    private static final Logger LOG = Logger.getLogger(FileMerger.class);

    /**
     * Merges the {@link List} of {@link IFile} to the output {@link File}. If
     * the outputFile is newer than all the files to be merged merge is skipped
     * 
     * @param input
     * @param outputFile
     */
    public void merge(List<IFile> input, File outputFile) {
        if (!isAlreadyMerged(input, outputFile)) {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
            }
            OutputStream outStream = null;
            try {
                outStream = new FileOutputStream(outputFile);
                for (IFile file : input) {
                    if (null != file.getFile() && file.getFile().exists()) {
                        InputStream stream = new FileInputStream(file.getFile());
                        try {
                            IOUtils.write(IOUtils.toByteArray(stream), outStream);
                            IOUtils.write(("" + (char) 10).getBytes(), outStream);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                            LOG.error("Caught IOException while merging "
                                    + file.getFileName() + " to " + outputFile.getName(),
                                    e);
                        }
                        finally {
                            IOUtils.closeQuietly(stream);
                        }
                    }
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error("Caught FileNotFoundException while merging to "
                        + outputFile.getName(), e);
            }
            finally {
                IOUtils.closeQuietly(outStream);
            }
        }
    }

    /**
     * Checks if the output merged file is older than any of the given files
     * given
     * 
     * @param input
     * @param outputFile
     * @return
     */
    private boolean isAlreadyMerged(List<IFile> input, File outputFile) {
        boolean result = false;
        if (outputFile.exists()) {
            result = true;
            Date outputDate = new Date(outputFile.lastModified());
            for (IFile file : input) {
                Date inputDate = new Date(file.getFile().lastModified());
                if (outputDate.before(inputDate)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}
