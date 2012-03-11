/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
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
     * all content is served from and stored in there. Commited content will be
     * copied to there.
     */
    protected String cachePath = null;

    /**
     * The temporary path of the cache, that is used in transaction mode.
     */
    protected String tmpPath = null;

    /**
     * The initial cache from which a transaction was started.
     */
    protected DocumentCache<T> initialCache = null;

    /**
     * File name filter for recognizing cached files
     */
    protected class CacheFileFilter implements FileFilter {
	public boolean accept(File file) {
	    return !file.isDirectory()
		    && file.getName().endsWith(DOCUMENT_FILE_EXTENSION);
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
	    String originalPath = this.getCachePath();
	    if (originalPath == null)
		throw new RuntimeException(
			"DefaultFileCache is not configured properly: cachePath not set.");

	    // check if the original path exists and create it if not
	    File cacheLocation = new File(originalPath);
	    if (!cacheLocation.exists())
		cacheLocation.mkdir();

	    this.isInitialized = true;
	}
    }

    /**
     * Get the work path of the cache. If the cache is in transaction mode, the
     * path will differ from cachePath.
     * 
     * @return String
     */
    protected String getWorkPath() {
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
	    throw new IllegalArgumentException(
		    "Null is not allowed as id value.");
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
	    throw new IllegalArgumentException(
		    "Null is not allowed as id value.");
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
	String id = this.decodeId(basename.substring(0,
		basename.lastIndexOf(DOT)));
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
	return this.encodeId(id).substring(0, 1);
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
	buf.append(this.getWorkPath()).append(File.separatorChar)
		.append(this.getRelativePath(id));
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
	    for (int i = 0; i < files.length; i++) {
		if (cacheFileFilter.accept(files[i]))
		    documentIds.add(this.getIdFromFilename(files[i].getName()));
		if (files[i].isDirectory())
		    documentIds.addAll(getDocumentIds(files[i]));
	    }
	}
	return documentIds;
    }

    /**
     * Set the original cache path (that is used if not in transaction mode)
     * 
     * @param cachePath
     */
    public void setCachePath(String cachePath) {
	this.cachePath = cachePath;
    }

    /**
     * Get the root path of the cache if it is not in transaction mode.
     * 
     * @return String
     */
    public String getCachePath() {
	return this.cachePath;
    }

    /**
     * Get the root path of the cache if it is in transaction mode.
     * 
     * @return String
     */
    public String getTempPath() {
	if (this.tmpPath == null) {
	    File originalPath = new File(this.getCachePath());
	    File newPath = new File(originalPath.getParent()
		    + File.separatorChar + originalPath.getName() + "_"
		    + StringUtils.generateUuid());

	    // check if the cache path exists and create it if not
	    if (!newPath.exists())
		newPath.mkdir();
	    this.tmpPath = newPath.getName();
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
	buf.append(this.getAbsolutePath(id)).append(File.separatorChar)
		.append(this.getFilename(id));
	return new File(buf.toString()).getAbsolutePath();
    }

    /**
     * Cache interface implementation
     */

    @Override
    public Set<Serializable> getCachedIds() {
	if (!this.isInitialized()) {
	    initialize();
	}
	return getDocumentIds(new File(this.getWorkPath()));
    }

    @Override
    public boolean isCached(Serializable id) throws IOException {
	if (!this.isInitialized()) {
	    initialize();
	}
	String filePath = this.getAbsoluteFilename(id);
	File file = new File(filePath);
	return file.exists();
    }

    @Override
    public T get(Serializable id) throws IOException {
	if (!this.isInitialized()) {
	    initialize();
	}
	String filePath = this.getAbsoluteFilename(id);
	File file = new File(filePath);
	if (file.exists()) {

	    StringBuilder content = new StringBuilder();
	    BufferedReader input = new BufferedReader(new InputStreamReader(
		    new FileInputStream(file), "UTF-8"));

	    try {
		String line = null;
		while ((line = input.readLine()) != null) {
		    content.append(line);
		    content.append(System.getProperty("line.separator"));
		}
		input.close();
		input = null;

		return this.unserializeDocument(content.toString());
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
    public void put(T document) throws IOException {
	if (!this.isInitialized()) {
	    initialize();
	}
	Serializable documentId = this.getDocumentId(document);

	// ensure that the directory exists
	String path = this.getAbsolutePath(documentId);
	new File(path).mkdirs();

	String filePath = this.getAbsoluteFilename(documentId);
	BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(filePath), "UTF8"));
	try {
	    String content = this.serializeDocument(document);
	    output.write(content);
	    output.close();
	    output = null;
	} finally {
	    if (output != null) {
		output.close();
	    }
	}
    }

    @Override
    public void removeAll() {
	if (!this.isInitialized()) {
	    initialize();
	}
	File workPath = new File(this.getWorkPath());
	FileUtils.deleteRecursive(workPath);
	workPath.mkdirs();
    }

    @Override
    public void remove(Serializable id) {
	if (!this.isInitialized()) {
	    initialize();
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
    public DocumentCache<T> startTransaction() throws IOException {
	if (!this.isInitialized()) {
	    initialize();
	}
	AbstractFileCache<T> cache = this.newInstance();

	// the original content of the new cache instance
	// is the content of this cache
	cache.cachePath = this.getWorkPath();
	cache.initialCache = this;
	cache.inTransaction = true;

	// copy content of this instance to the new cache
	FileUtils.copyRecursive(new File(this.getWorkPath()),
		new File(cache.getWorkPath()));

	return cache;
    }

    @Override
    public void commitTransaction() throws IOException {
	if (!this.isInitialized()) {
	    initialize();
	}
	if (this.isInTransaction()) {
	    // move content of this instance to the original cache
	    File originalDir = new File(this.getCachePath());
	    File tmpDir = new File(this.getWorkPath());
	    FileUtils.deleteRecursive(originalDir);
	    tmpDir.renameTo(originalDir);

	    this.inTransaction = false;
	} else {
	    throw new RuntimeException("The cache is not in transaction mode.");
	}
    }

    @Override
    public void rollbackTransaction() {
	if (!this.isInitialized()) {
	    initialize();
	}
	if (this.isInTransaction()) {
	    // remove content of this instance
	    File tmpDir = new File(this.getWorkPath());
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
	File cacheDir = new File(this.getCachePath());
	return new Date(cacheDir.lastModified());
    }

    @Override
    public String toString() {
	return this.getWorkPath() + ", " + super.toString();
    }

    /**
     * Get the id of a document.
     * 
     * @param document
     * @return Serializable
     */
    protected abstract Serializable getDocumentId(T document);

    /**
     * Serialize a document into a string.
     * 
     * @param document
     * @return String
     */
    protected abstract String serializeDocument(T document);

    /**
     * Unserialize a string into a document.
     * 
     * @param str
     * @return T
     */
    protected abstract T unserializeDocument(String str);

    /**
     * Create a new cache instance.
     * 
     * @return AbstractFileCache<T>
     */
    protected abstract AbstractFileCache<T> newInstance();
}
