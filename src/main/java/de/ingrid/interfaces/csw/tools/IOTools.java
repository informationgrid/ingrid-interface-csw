/*
 * Created on 20.10.2005
 *
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

/**
 * some tools for input/output operations 
 * @author rschaefer
 *
 */
public final class IOTools {
    
    
    /**
     * constructor
     */
    private IOTools () {
    }
    
    
    /**
     * writes the contents of an input stream to an output stream
     * @param inputStream InputStream
     * @param outputstream OutputStream
     * @throws IOException e
     */
    public static void writeInputToOutputStream(final InputStream inputStream, final OutputStream outputstream) 
    	throws IOException {

        int c = 0;
        
         Reader reader = null;
      
         try {
          
         
            reader = new InputStreamReader(inputStream);
            
            
            
            while ((c = reader.read()) != -1) {
  	       
               outputstream.write(c);     
            } 
         
            
         } finally {
           
             if (reader != null) {
  	     
                 reader.close();    
           }	    
        }	     
  	    
  	       
     }	
    

}
