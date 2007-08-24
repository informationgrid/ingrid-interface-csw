/**
 * 
 */
package de.ingrid.interfaces.csw.transform.response;

import org.dom4j.Document;

/**
 * @author joachim
 *
 */
public interface CSWPostProcessor {

	public Document process(Document in);
	
}
