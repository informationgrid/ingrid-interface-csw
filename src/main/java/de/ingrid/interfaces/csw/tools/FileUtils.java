/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class FileUtils {

	final protected static Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * Remove all files from a directory and all sub directories
	 * 
	 * @param src
	 *            The start directory
	 */
	public static void deleteRecursive(File src) {
		File[] files = src.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteRecursive(file);
				}
				if (!file.delete()) {
				    log.warn("Could not delete file: " + file);
				}
			}
		}
        if (!src.delete()) {
            log.warn("Could not delete file: " + src);
        }
	}

	/**
	 * This function will copy files or directories from one location to
	 * another. note that the source and the destination must be mutually
	 * exclusive. This function can not be used to copy a directory to a sub
	 * directory of itself. The function will also have problems if the
	 * destination files already exist.
	 * 
	 * @param src
	 *            -- A File object that represents the source for the copy
	 * @param dest
	 *            -- A File object that represnts the destination for the copy.
	 * @throws IOException
	 *             if unable to copy.
	 * 
	 *             Source: http://www.dreamincode.net/code/snippet1443.htm
	 */
	public static void copyRecursive(File src, File dest) throws IOException {
		// Check to ensure that the source is valid...
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: "
					+ src.getAbsolutePath() + ".");
		} else if (!src.canRead()) { // check to ensure we have rights to the
			// source...
			throw new IOException("copyFiles: No right to source: "
					+ src.getAbsolutePath() + ".");
		}
		// is this a directory copy?
		if (src.isDirectory()) {
			if (!dest.exists()) { // does the destination already exist?
				// if not we need to make it exist if possible (note this is
				// mkdirs not mkdir)
				if (!dest.mkdirs()) {
					throw new IOException(
							"copyFiles: Could not create direcotry: "
									+ dest.getAbsolutePath() + ".");
				}
			}
			// get a listing of files...
			String list[] = src.list();
			// copy all the files in the list.
			for (String element : list) {
				File dest1 = new File(dest, element);
				File src1 = new File(src, element);
				copyRecursive(src1, dest1);
			}
		} else {
			// This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096]; // Buffer 4K at a time (you can
			// change this).
			int bytesRead;
			try {
				// open the files for input and output
				fin = new FileInputStream(src);
				fout = new FileOutputStream(dest);
				// while bytesRead indicates a successful read, lets write...
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);
				}
				fin.close();
				fout.close();
				fin = null;
				fout = null;
			} catch (IOException e) { // Error copying file...
				IOException wrapper = new IOException(
						"copyFiles: Unable to copy file: "
								+ src.getAbsolutePath() + "to"
								+ dest.getAbsolutePath() + ".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally { // Ensure that the files are closed (if they were open).
				if (fin != null) {
					fin.close();
				}
				if (fout != null) {
					fin.close();
				}
			}
		}
	}

	/**
	 * Encode Filename to prevent invalid characters in file name. The encoding
	 * is reversible (url encoding (UTF-8) is used).
	 * 
	 * @param s
	 * @return
	 */
	public static String encodeFileName(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to encode using UTF-8.", e);
			return s;
		}
	}

	/**
	 * Decode Filename coded with <code>encodeFileName</code>.
	 * 
	 * @param s
	 * @return
	 */
	public static String decodeFileName(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to decode using UTF-8.", e);
			return s;
		}
	}
	
    /**
     * Get all resources specified by pattern. For pattern syntax see (@link PathMatchingResourcePatternResolver).
     * 
     * @param packageName
     * @return
     * @throws IOException
     */
    public static Resource[] getPackageContent(String pattern) throws IOException{
        PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();
        Resource[] resources = pmrpr.getResources(pattern);
        return resources;
    }
    
    /**
     * Convert stream to string.
     * 
     * @param is
     * @return
     */
    public static String convertStreamToString(java.io.InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

}
