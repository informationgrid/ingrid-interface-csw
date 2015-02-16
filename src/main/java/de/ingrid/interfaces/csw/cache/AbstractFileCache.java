/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.interfaces.csw.tools.FileUtils;
import de.ingrid.interfaces.csw.tools.StringUtils;

public abstract class AbstractFileCache<T> implements DocumentCache<T> {

    private static final String DOT = ".";
    private static final String DOCUMENT_FILE_EXTENSION = DOT + "xml";

    final protected static Log log = LogFactory.getLog(AbstractFileCache.class);

    protected boolean isInitialized = false;
    protected boolean inTransaction = false;

    /**
     * The original path of the cache. If the cache is not in transaction mode,
     * all content is served from and stored in there. Committed content will be
     * copied to there.
     */
    protected File cachePath = null;

    /**
     * The temporary path of the cache, that is used in transaction mode.
     */
    protected File tmpPath = null;

    /**
     * The initial cache from which a transaction was started.
     */
    protected DocumentCache<T> initialCache = null;

    /**
     * File name filter for recognizing cached files
     */
    protected class CacheFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return !file.isDirectory() && file.getName().endsWith(DOCUMENT_FILE_EXTENSION);
        }
    };

    /**
     * Constructor
     */
    public AbstractFileCache() {
        this.initialCache = this;
    }

    /**
     * Check if the cache is already initialized
     * 
     * @return boolean
     */
    protected boolean isInitialized() {
        return this.isInitialized;
    }

    /**
     * Initialize the cache
     */
    protected void initialize() {
        if (!this.isInitialized) {
            // check for original path
            File cacheLocation = this.getCachePath();
            if (cacheLocation == null) {
                throw new RuntimeException("DefaultFileCache is not configured properly: cachePath not set.");
            }

            // check if the original path exists and create it if not
            if (!cacheLocation.exists()) {
                cacheLocation.mkdir();
            }
            this.isInitialized = true;
        }
    }

    /**
     * Get the work path of the cache. If the cache is in transaction mode, the
     * path will differ from cachePath.
     * 
     * @return File
     */
    protected File getWorkPath() {
        if (this.isInTransaction()) {
            return this.getTempPath();
        } else {
            return this.getCachePath();
        }
    }

    /**
     * Encode an id to be used in a filename.
     * 
     * @return String
     */
    protected String encodeId(Serializable id) {
        if (id != null) {
            return FileUtils.encodeFileName(id.toString());
        } else {
            throw new IllegalArgumentException("Null is not allowed as id value.");
        }
    }

    /**
     * Decode an id that was used in a filename.
     * 
     * @return String
     */
    protected String decodeId(Serializable id) {
        if (id != null) {
            return FileUtils.decodeFileName(id.toString());
        } else {
            throw new IllegalArgumentException("Null is not allowed as id value.");
        }
    }

    /**
     * Get the document id from a cache filename
     * 
     * @param filename
     *            The filename without the path
     * @return String
     */
    protected String getIdFromFilename(String filename) {
        File file = new File(filename);
        String basename = file.getName();
        String id = this.decodeId(basename.substring(0, basename.lastIndexOf(DOT)));
        return id;
    }

    /**
     * Get the filename for a document
     * 
     * @param id
     * @return String
     */
    protected String getFilename(Serializable id) {
        return this.encodeId(id) + DOCUMENT_FILE_EXTENSION;
    }

    /**
     * Get the relative path to a document starting from the cache root
     * 
     * @param id
     * @return String
     */
    protected String getRelativePath(Serializable id) {
        String encodedId = this.encodeId(id);
        if (encodedId.length() >= 2) {
            return this.encodeId(id).substring(0, 2);
        } else {
            return this.encodeId(id).substring(0, 1);
        }
    }

    /**
     * Get the relative path to a document starting from the cache root
     * 
     * @param id
     * @param elementSetName
     * @return String
     */
    protected String getAbsolutePath(Serializable id) {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getWorkPath()).append(File.separatorChar).append(this.getRelativePath(id));
        return new File(buf.toString()).getAbsolutePath();
    }

    /**
     * Get document ids from a directory and all sub directories
     * 
     * @param directory
     *            The start directory
     * @return Set
     */
    protected Set<Serializable> getDocumentIds(File directory) {
        Set<Serializable> documentIds = new HashSet<Serializable>();
        FileFilter cacheFileFilter = new CacheFileFilter();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (cacheFileFilter.accept(file)) {
                    documentIds.add(this.getIdFromFilename(file.getName()));
                }
                if (file.isDirectory()) {
                    documentIds.addAll(this.getDocumentIds(file));
                }
            }
        }
        return documentIds;
    }

    /**
     * Set the original cache path (that is used if not in transaction mode)
     * 
     * @param cachePath
     */
    public void setCachePath(File cachePath) {
        this.cachePath = cachePath;
    }

    /**
     * Get the root path of the cache if it is not in transaction mode.
     * 
     * @return String
     */
    public File getCachePath() {
        return this.cachePath;
    }

    /**
     * Get the root path of the cache if it is in transaction mode.
     * 
     * @return File
     */
    public File getTempPath() {
        if (this.tmpPath == null) {
            File originalPath = this.getCachePath();
            File newPath = new File(originalPath.getParent() + File.separatorChar + originalPath.getName() + "_"
                    + StringUtils.generateUuid());

            // check if the cache path exists and create it if not
            if (!newPath.exists()) {
                newPath.mkdir();
            }
            this.tmpPath = newPath;
        }
        return this.tmpPath;
    }

    /**
     * Get the absolute filename of a document.
     * 
     * @param id
     * @param cacheOperation
     * @return String
     */
    public String getAbsoluteFilename(Serializable id) {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getAbsolutePath(id)).append(File.separatorChar).append(this.getFilename(id));
        return new File(buf.toString()).getAbsolutePath();
    }

    /**
     * Cache interface implementation
     */

    @Override
    public Set<Serializable> getCachedIds() {
        if (!this.isInitialized()) {
            this.initialize();
        }
        return this.getDocumentIds(this.getWorkPath());
    }

    @Override
    public boolean isCached(Serializable id) {
        if (!this.isInitialized()) {
            this.initialize();
        }
        String filePath = this.getAbsoluteFilename(id);
        File file = new File(filePath);
        return file.exists();
    }

    @Override
    public T get(Serializable id) throws Exception {
        if (!this.isInitialized()) {
            this.initialize();
        }
        String filePath = this.getAbsoluteFilename(id);
        File file = new File(filePath);
        if (file.exists()) {

            StringBuilder content = new StringBuilder();
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    content.append(line);
                    content.append(System.getProperty("line.separator"));
                }
                input.close();
                input = null;

                return this.unserializeDocument(id, content.toString());
            } catch (Exception e) {
                throw new IOException(e);
            } finally {
                if (input != null)
                    input.close();
            }
        } else
            throw new IOException("No cache entry with id " + id + " found.");
    }

    @Override
    public Serializable put(T document) throws Exception {
        if (!this.isInitialized()) {
            this.initialize();
        }
        Serializable id = this.getCacheId(document);

        if (id == null) {
            throw new IllegalArgumentException("ID of the document is null!!");
        }
        
        // ensure that the directory exists
        String path = this.getAbsolutePath(id);
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            if (!pathFile.mkdirs()) {
                log.error("Error creating directory: " + path);
            }
        }

        String filePath = this.getAbsoluteFilename(id);
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF8"));
        try {
            String content = this.serializeDocument(id, document);
            output.write(content);
            output.close();
            output = null;
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return id;
    }

    @Override
    public void removeAll() {
        if (!this.isInitialized()) {
            this.initialize();
        }
        File workPath = this.getWorkPath();
        FileUtils.deleteRecursive(workPath);
        workPath.mkdirs();
    }

    @Override
    public void remove(Serializable id) {
        if (!this.isInitialized()) {
            this.initialize();
        }
        String filePath = this.getAbsoluteFilename(id);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public boolean isInTransaction() {
        return this.inTransaction;
    }

    @Override
    public DocumentCache<T> startTransaction(Boolean initialize) throws IOException {
        if (!this.isInitialized()) {
            this.initialize();
        }
        AbstractFileCache<T> cache = this.newInstance();

        // the original content of the new cache instance
        // is the content of this cache
        cache.cachePath = this.getWorkPath();
        cache.initialCache = this;
        cache.inTransaction = true;

        // copy content of this instance to the new cache
        if (initialize) {
            FileUtils.copyRecursive(this.getWorkPath(), cache.getWorkPath());
        }
        return cache;
    }

    @Override
    public void commitTransaction() throws IOException {
        if (!this.isInitialized()) {
            this.initialize();
        }
        if (this.isInTransaction()) {
            // move content of this instance to the original cache
            File originalDir = this.getCachePath();
            File tmpDir = new File(this.getTempPath().getAbsolutePath() + "_tmp");
            // make sure all parent paths are created
            tmpDir.mkdirs();
            // then remove the last directory
            tmpDir.delete();
            File workDir = this.getWorkPath();
            if (!originalDir.renameTo(tmpDir)) {
                throw new IOException("Could not rename '"+originalDir+"' to '"+tmpDir+"'.");
            }
            if (!workDir.renameTo(originalDir)) {
                throw new IOException("Could not rename '"+workDir+"' to '"+originalDir+"'.");
            }
            FileUtils.deleteRecursive(tmpDir);

            this.inTransaction = false;
        } else {
            throw new RuntimeException("The cache is not in transaction mode.");
        }
    }

    @Override
    public void rollbackTransaction() {
        if (!this.isInitialized()) {
            this.initialize();
        }
        if (this.isInTransaction()) {
            // remove content of this instance
            File tmpDir = this.getWorkPath();
            FileUtils.deleteRecursive(tmpDir);

            this.inTransaction = false;
        } else {
            throw new RuntimeException("The cache is not in transaction mode.");
        }
    }

    @Override
    public DocumentCache<T> getInitialCache() {
        return this.initialCache;
    }

    @Override
    public Date getLastCommitDate() {
        // return the last modified date of the cache directory
        File cacheDir = this.getCachePath();
        return new Date(cacheDir.lastModified());
    }

    @Override
    public String toString() {
        return this.getWorkPath() + ", " + super.toString();
    }

    /**
     * Serialize a document into a string.
     * 
     * @param id
     * @param document
     * @return String
     * @throws Exception
     */
    protected abstract String serializeDocument(Serializable id, T document) throws Exception;

    /**
     * Unserialize a string into a document.
     * 
     * @param id
     * @param str
     * @return T
     * @throws Exception
     */
    protected abstract T unserializeDocument(Serializable id, String str) throws Exception;

    /**
     * Create a new cache instance.
     * 
     * @return AbstractFileCache<T>
     */
    protected abstract AbstractFileCache<T> newInstance();
}
