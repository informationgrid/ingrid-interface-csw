/*
 * Created on 18.10.2005
 *
 */
package de.ingrid.interfaces.csw.tools;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.Message;

import de.ingrid.interfaces.csw.tools.SOAPTools;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 */
public class SOAPToolsTest extends TestCase {

    public final void testCreateExceptionReport() throws Exception {
        
        SOAPMessage exceptionMess = null;
        
        exceptionMess = SOAPTools.createExceptionReport("exception text", "exception code", "locator", true);
        
        assertNotNull(exceptionMess);
        
   
        //System.out.println("exceptionMess.getSOAPPartAsString(): " + exceptionMess.getSOAPPartAsString());
        
    }

}
