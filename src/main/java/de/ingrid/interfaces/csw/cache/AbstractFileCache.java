/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.cache;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.tools.FileUtils;
import de.ingrid.interfaces.csw.tools.StringUtils;

public abstract class AbstractFileCache<T> implements DocumentCache<T> {

	private static final String DOCUMENT_FILE_EXTENSION = ".xml";

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
	 * Get the filename for a document
	 * 
	 * @param id
	 * @return String
	 */
	protected String getFilename(Serializable id) {
		return FileUtils.encodeId(id) + DOCUMENT_FILE_EXTENSION;
	}

	/**
	 * Get the relative path to a document starting from the cache root
	 * 
	 * @param id
	 * @return String
	 */
	protected abstract String getRelativePath(Serializable id);

	/**
	 * Get the relative path to a document starting from the cache root
	 * 
	 * @param id
	 * @param elementSetName
	 * @return String
	 */
	protected String getAbsolutePath(Serializable id) {
		StringBuilder buf = new StringBuilder();
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

		EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		CacheFileLister cfl = new CacheFileLister(documentIds);
		try {
			Files.walkFileTree(directory.toPath(), opts, Integer.MAX_VALUE, cfl);
		} catch (IOException e) {
			log.error("Error getting document IDs from cache.");
		}

		return documentIds;
	}

	/**
	 * FileVisitor that lists all ids inside a file cache.
	 * 
	 * @author jm
	 *
	 */
	class CacheFileLister extends SimpleFileVisitor<Path> {

		Set<Serializable> documentIds = new HashSet<Serializable>();

		CacheFileLister(Set<Serializable> documentIds) {
			super();
			this.documentIds = documentIds;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (!Files.isDirectory(file) && file.toString().endsWith(DOCUMENT_FILE_EXTENSION)) {
				documentIds.add(FileUtils.getIdFromFilename(file.getFileName().toString()));
			}
			return FileVisitResult.CONTINUE;
		}
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
		StringBuilder buf = new StringBuilder();
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
			String content = FileUtils.readFile(Paths.get(filePath), Charset.forName("UTF-8"));
			return this.unserializeDocument(id, content);
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
		String content = this.serializeDocument(id, document);

		FileUtils.writeFile(Paths.get(filePath), content, Charset.forName("UTF-8"));

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
			Path originalDirPath = this.getCachePath().toPath();
			File tmpDir = new File(this.getTempPath().getAbsolutePath() + "_tmp");
			// make sure all parent paths are created
			tmpDir.mkdirs();
			// then remove the last directory
			Files.delete(tmpDir.toPath());
			Path workDirPath = this.getWorkPath().toPath();
			if (log.isInfoEnabled()) {
				log.info("Rename old cache: " + originalDirPath + " to " + tmpDir.toPath());
			}
			FileUtils.waitAndMove(originalDirPath, tmpDir.toPath(), ApplicationProperties.getInteger( ConfigurationKeys.FILE_OPERATION_TIMEOUT, 10000));
			if (log.isInfoEnabled()) {
				log.info("Rename new cache: " + workDirPath + " to " + originalDirPath);
			}
			FileUtils.waitAndMove(workDirPath, originalDirPath, ApplicationProperties.getInteger( ConfigurationKeys.FILE_OPERATION_TIMEOUT, 10000));
			if (log.isInfoEnabled()) {
				log.info("Remove tmp path: " + tmpDir.toPath());
			}
			FileUtils.waitAndDelete(tmpDir.toPath(), ApplicationProperties.getInteger( ConfigurationKeys.FILE_OPERATION_TIMEOUT, 10000));

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
