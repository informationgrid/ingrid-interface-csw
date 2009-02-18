/*----------------------------------------------------------------------------*
 *          @@@@@      @@@       @@@@@                                        *
 *      @@@@@@@@@@@    @@@@    @@@@@@@@        @                              *
 *     @@@@@@@@@@@@    @@@@   @@@@@@@@@     @@@@                              *
 *    @@@@@            @@@@  @@@@           @@@@                              *
 *   @@@@@             @@@@  @@@@@        @@@@@@@@@   @@@@@@@@      @@@@@@@   *
 *   @@@@    @@@@@@@   @@@@   @@@@@@@     @@@@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *   @@@@   @@@@@@@@   @@@@    @@@@@@@@     @@@@    @@@@    @@@   @@@@        *
 *   @@@@    @@@@@@@   @@@@      @@@@@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *   @@@@@      @@@@   @@@@         @@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *    @@@@@     @@@@   @@@@   @     @@@@    @@@@    @@@@      @   @@@@        *
 *     @@@@@@@@@@@@@   @@@@   @@@@@@@@@@    @@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *       @@@@@@@@@@@   @@@@   @@@@@@@@       @@@@@@   @@@@@@@@@     @@@@@@@   *
 *                           Neue Wege mit GIS                                *
 *                                                                            *
 * Rundeturmstr. 12                                                           *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright © 2004 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schaefer                                           *
 * Erstellungsdatum : 19.11.2004		                                      *
 * Version          : 1.0                                                     *
 * Beschreibung     : Tools zum Lesen und Schreiben von Dateien				  *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * aenderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |         |                                           *
 *            |         |         |                                           *
 *            |         |         |                                           *
 *----------------------------------------------------------------------------*
*/

package de.ingrid.interfaces.csw.tools;

import java.io.*;

/**
 * Tools for reading and writing of files	
 * @author rschaefer
 */
public class FileTools {

    
    
    
    /**
     * reads in a file 
     * output is a byte array
     * @param path String path of the file
     * @return byteArray byte[] the output
     */
    public static byte[] readFile(String path) {

        File inputFile = new File(path);

        int fileLength = (int) inputFile.length();

        //System.out.println("FileTools readFile fileLength: " + fileLength);

        byte[] byteArray = new byte[fileLength];

        FileInputStream fis = null;

        try {
        	
            fis = new FileInputStream(inputFile);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int iByte = 0;
        int i = 0;

        try {

            iByte = fis.read();

            while (iByte != -1) {

                byteArray[i] = (byte) iByte;

                //System.out.println("FileTools readFile blobByteArray[" + i + "]: " + blobByteArray[i]);

                iByte = fis.read();
                i++;
            }
          
			fis.close();
			
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return byteArray;

    }

    
    /**
     * writes a file with the given string
     * only to use with character data!
     * @param path String path of the file
     * @param strToWrite String to write into the file 
     */
    public static void writeFile(String path, String strToWrite) {

        PrintStream outFile = null;

        try {

            outFile = new PrintStream(new FileOutputStream(path));
            
			outFile.print(strToWrite);
			
			outFile.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       

    }



    /**
     * writes a file with the given byte array
     * @param path String path of the file
     * @param bytes byte[] byte array to write
     */
    public static void writeFile(String path, byte[] bytes) {

        FileOutputStream fos = null;

        try {

            fos = new FileOutputStream(path);

            try {
                fos.write(bytes);
                fos.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
