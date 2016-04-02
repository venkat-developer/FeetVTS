package com.i10n.tools.skins.compress;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.i10n.tools.skins.files.CSSFile;
import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * A Compressor class that compresses {@link CSSFile} using YUI's
 * {@link CssCompressor}
 * 
 * @see IFileCompressor
 * @author aravind
 * 
 */
public class CSSFileCompressor implements IFileCompressor<CSSFile> {

    private static final Logger LOG = Logger.getLogger(CSSFileCompressor.class);

    /**
     * Compresses {@link CSSFile} - src and writes the compressed output to
     * {@link CSSFile} - dest
     */
    @Override
    public CSSFile compress(CSSFile src, CSSFile dest) {
        if (src.exists()) {
            if (!dest.exists()) {
                dest.getFile().getParentFile().mkdirs();
            }
            OutputStream out = null;
            InputStream in = null;
            Reader reader = null;
            Writer writer = null;
            try {
                out = new FileOutputStream(dest.getFile());
                writer = new OutputStreamWriter(out);
                in = new FileInputStream(src.getFile());
                reader = new InputStreamReader(in);
                CssCompressor yuiCompressor = new CssCompressor(reader);
                yuiCompressor.compress(writer, 2);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error("Caught FileNotFoundException while compressing file : "
                        + src.getFileName(), e);
            }
            catch (IOException e) {
                e.printStackTrace();
                LOG.error("Caught IOException while compressing file : "
                        + src.getFileName(), e);
            }
            finally {
                IOUtils.closeQuietly(writer);
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(in);
            }
        }
        return dest;
    }

}
