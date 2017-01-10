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
package de.ingrid.interfaces.csw.index;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import de.ingrid.interfaces.csw.harvest.impl.RecordCache;

/**
 * Indexer defines the interface for document indexing implementations.
 * 
 * @author ingo@wemove.com
 * @author joachim@wemove.com
 */
public interface Indexer {

    /**
     * Execute the indexing job.
     * 
     * @throws Exception
     */
    void run(List<RecordCache> recordCacheList) throws Exception;

    /**
     * Remove documents from index.
     * 
     * @throws Exception
     */
    void removeDocs(Set<Serializable> docIds) throws Exception;

    /**
     * Remove documents from index by query. Returns a List of the "id" field
     * content of the removed index documents.
     * 
     * @throws Exception
     */
    List<String> removeDocsByQuery(String queryString) throws Exception;

    /**
     * Get the path to the Lucene index
     * 
     * @return File
     */
    public File getIndexConfigPath();
}
