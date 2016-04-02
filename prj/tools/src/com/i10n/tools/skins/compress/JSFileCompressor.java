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
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.i10n.tools.skins.config.SkinProperties;
import com.i10n.tools.skins.files.JSFile;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * A Compressor class to compress {@link JSFile} using YUI's
 * {@link JavaScriptCompressor}
 * 
 * @see IFileCompressor
 * @author aravind
 * 
 */
public class JSFileCompressor implements IFileCompressor<JSFile> {

    private static final Logger LOG = Logger.getLogger(JSFileCompressor.class);

    private boolean m_munge = true;
    private boolean m_verbose = false;
    private boolean m_preservesemicolons = true;
    private boolean m_optimize = true;

    public JSFileCompressor() {
        m_munge = "true"
                .equals(SkinProperties.getProperty(SkinProperties.COMPRESS_MUNGE));
        m_verbose = "true".equals(SkinProperties
                .getProperty(SkinProperties.COMPRESS_VERBOSE));
        m_preservesemicolons = "true".equals(SkinProperties
                .getProperty(SkinProperties.COMPRESS_PRESERVESEMICOLONS));
        m_optimize = "true".equals(SkinProperties
                .getProperty(SkinProperties.COMPRESS_OPTIMIZE));
    }

    private ErrorReporter m_errorReporter = new ErrorReporter() {

        public void warning(String message, String sourceName, int line,
                String lineSource, int lineOffset) {
            if (line < 0) {
                System.err.println("\n[WARNING] " + message);
            }
            else {
                System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':'
                        + message);
            }
        }

        public void error(String message, String sourceName, int line, String lineSource,
                int lineOffset) {
            if (line < 0) {
                System.err.println("\n[ERROR] " + message);
            }
            else {
                System.err
                        .println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
            }
        }

        public EvaluatorException runtimeError(String message, String sourceName,
                int line, String lineSource, int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    };

    /**
     * Compresses {@link JSFile} - src and writes the compressed output to
     * {@link JSFile} - dest
     */
    @Override
    public JSFile compress(JSFile src, JSFile dest) {
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
                JavaScriptCompressor compressor = new JavaScriptCompressor(reader,
                        m_errorReporter);
                compressor.compress(writer, -1, m_munge, m_verbose, m_preservesemicolons,
                        !m_optimize);
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
