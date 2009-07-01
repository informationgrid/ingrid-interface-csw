/*
 * Created on 28.10.2005
 *
 */
package de.ingrid.interfaces.csw.tools;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;



/**
 * @author rschaefer
 * 
 */
public class XSLTools {
	 /**
     * the log object
     */
   private static Log log = LogFactory.getLog(XSLTools.class);
  
        /**
         * transforms the (original) DOM document with XSL file 
         * into the transformed DOM document.
         * @param originalDoc Document
         * @param xslFilename String the XSLT file
         * @return transformedDoc Document
         * @throws Exception e
         */
        public final Document transform(final Document originalDoc, final String xslFilename)
        throws Exception {
            Document transformedDoc = null;
            
           //Create transformer factory
            TransformerFactory tFactory = TransformerFactory.newInstance();
            
           //Use the factory to create a template containing the xsl file
            Templates template = tFactory.newTemplates(new StreamSource(xslFilename));

           // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            //create a Source as a DOM source
            Source source = new DOMSource(originalDoc);
            transformedDoc = XMLTools.create();
            Result result = new DOMResult(transformedDoc);

           //  Apply the xsl file to the DOM source and create the DOM tree
            xformer.transform(source, result);
            
            return transformedDoc;
        }
}
