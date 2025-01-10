/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Cache interface.
 * 
 * Transaction support. A transaction allows to do all cache operations in a
 * temporary store. The cache guarantees that changes do not affect the content
 * of the cache from which the transaction started until the transaction is
 * committed. A rollback ends the transaction without changing the original
 * content.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface DocumentCache<T> {

	/**
	 * Get the ids all the documents, that are cached.
	 * 
	 * @return List
	 */
	public Set<Serializable> getCachedIds();

	/**
	 * Get the id of a document.
	 * 
	 * @param document
	 * @return Serializable
	 */
	public Serializable getCacheId(T document) throws Exception;

	/**
	 * Check if a document is cached.
	 * 
	 * @param id
	 * @return boolean
	 */
	public boolean isCached(Serializable id);

	/**
	 * Get a document.
	 * 
	 * @param id
	 * @return document
	 */
	public T get(Serializable id) throws Exception;

	/**
	 * Store a document. Overrides the old document with the same id.
	 * Returns the id of the document in the cache.
	 * 
	 * @param document
	 * @return Serializable
	 */
	public Serializable put(T document) throws Exception;

	/**
	 * Remove a document.
	 * 
	 * @param id
	 */
	public void remove(Serializable id);

	/**
	 * Remove all documents.
	 */
	public void removeAll();

	/**
	 * Check wether the cache is in transaction mode.
	 * 
	 * @param boolean
	 */
	public boolean isInTransaction();

	/**
	 * Start the transaction. If initialize is true the content of the returned cache is the same as
	 * the content of this cache initially.
	 * 
	 * @param initialize
	 * @return
	 * @throws IOException
	 */
	public DocumentCache<T> startTransaction(Boolean initialize) throws IOException;

	/**
	 * Commit the transaction. Transfer all changes, that are done since the
	 * transaction was opened, to the original content.
	 */
	public void commitTransaction() throws IOException;

	/**
	 * Rollback the transaction. Discard all changes, that are done since the
	 * transaction was opened.
	 */
	public void rollbackTransaction();

	/**
	 * Get the cache from that a transaction was started. If the cache is not in
	 * transaction, the result is the same instance on which the method is
	 * called.
	 * 
	 * @param Returns
	 *            the initial cache instance.
	 */
	public DocumentCache<T> getInitialCache();

	/**
	 * Get the date of the last commit, returns null, if it could not be
	 * determined
	 */
	public Date getLastCommitDate();
}
