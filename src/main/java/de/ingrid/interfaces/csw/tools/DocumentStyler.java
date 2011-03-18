/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

/**
 * @author joachim
 * 
 */
public class DocumentStyler {
    private Transformer transformer;

    public DocumentStyler(Source aStyleSheet) throws Exception {
        // create transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        transformer = factory.newTransformer(aStyleSheet);
    }

    public Document transform(Document aDocument) throws Exception {

        // perform transformation
        DocumentSource source = new DocumentSource(aDocument);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        // return resulting document
        return result.getDocument();
    }
}
