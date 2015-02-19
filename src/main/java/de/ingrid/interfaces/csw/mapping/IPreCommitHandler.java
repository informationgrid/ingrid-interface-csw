/**
 * 
 */
package de.ingrid.interfaces.csw.mapping;

import de.ingrid.interfaces.csw.cache.DocumentCache;

/**
 * Handler that gets executed just before commiting the temp cache of a
 * transactional mapping process.
 * 
 * @author joachim
 * 
 */
public interface IPreCommitHandler {

    /**
     * Executes before commiting the temp cache of a transactional mapping
     * process.
     * 
     */
    public void beforeCommit(DocumentCache<?> cache) throws Exception;

}
