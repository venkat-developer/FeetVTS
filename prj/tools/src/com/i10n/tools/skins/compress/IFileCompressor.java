package com.i10n.tools.skins.compress;

import com.i10n.tools.skins.files.IFile;

/**
 * Interface for Compressor Classes that compresses implementations of
 * {@link IFile}
 * 
 * @see CSSFileCompressor
 * @see JSFileCompressor
 * @author sabarish
 * 
 * @param <T>
 */
public interface IFileCompressor<T extends IFile> {
    /**
     * Compresses {@link IFile} - src and writes it to {@link IFile} - dest
     * 
     * @param src
     * @param dest
     * @return
     */
    T compress(T src, T dest);
}
