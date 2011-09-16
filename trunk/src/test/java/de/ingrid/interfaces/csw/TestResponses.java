/*
 * Created on 18.10.2005
 *
 */
package de.ingrid.interfaces.csw;

/**
 * @author rschaefer
 * 
 */
public class TestResponses {
    
   
    public static final String EXC1 =    
      "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"" +
        " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
      "<soapenv:Body>" +
      "<soapenv:ExceptionReport version=\"2.0.0\" language=\"en\">" +
      "<soapenv:Exception exceptionCode=\"OperationNotSupported\" locator=\"GetCap\">" +
      "<soapenv:ExceptionText>de.ingrid.interfaces.csw.CSWServlet: Operation GetCap is not supported.</soapenv:ExceptionText>" +
      "</soapenv:Exception>" +
      "</soapenv:ExceptionReport>" +
      "</soapenv:Body>" +
      "</soapenv:Envelope>";
    

    public static final String EXC2 =        
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"" +
        " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
      " <soapenv:Body>" +
      "  <soapenv:ExceptionReport version=\"2.0.0\" language=\"en\">" +
      "    <soapenv:Exception exceptionCode=\"OperationNotSupported\" locator=\"GetCap\">" +
      "     <soapenv:ExceptionText>de.ingrid.interfaces.csw.CSWServlet: Operation GetCap is not supported.</soapenv:ExceptionText>" +
      "    </soapenv:Exception>" +
      "  </soapenv:ExceptionReport>" +
      " </soapenv:Body>" +
      "</soapenv:Envelope>";
    
    
}
