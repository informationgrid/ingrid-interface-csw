/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.tools;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;

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
				} else {
					if (!file.delete()) {
						log.error("Could not delete file: " + file);
					}
				}
			}
		}
		if (!src.delete()) {
			log.error("Could not delete file: " + src);
		}
	}

	/**
	 * Delete a file or directory specified by a {@link Path}. This method uses
	 * the new {@link Files} API.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static void deleteRecursive(Path path) throws IOException {
		deleteRecursive(path, ".*");
	}

	/**
	 * Delete a file or directory specified by a {@link Path}. This method uses
	 * the new {@link Files} API and allows to specify a regular expression to
	 * remove only files that match that expression.
	 * 
	 * @param path
	 * @param pattern
	 * @throws IOException
	 */
	public static void deleteRecursive(Path path, final String pattern) throws IOException {

		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:" + pattern);

		if (!Files.exists(path))
			return;

		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (pattern != null && matcher.matches(file.getFileName())) {
					Files.delete(file);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// try to delete the file anyway, even if its attributes
				// could not be read, since delete-only access is
				// theoretically possible
				if (pattern != null && matcher.matches(file.getFileName())) {
					Files.delete(file);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc == null) {
					if (matcher.matches(dir.getFileName())) {
						if (dir.toFile().list().length > 0) {
							// remove even if not empty
							FileUtils.deleteRecursive(dir);
						} else {
							Files.delete(dir);
						}
					}
					return FileVisitResult.CONTINUE;
				} else {
					// directory iteration failed; propagate exception
					throw exc;
				}
			}

		});
	}

	/**
	 * Moves a path, retries for timeout ms if moving fails.
	 * 
	 * @param src
	 * @param dest
	 * @param timeout
	 * @throws IOException
	 */
	public static void waitAndMove(Path src, Path dest, long timeout) throws IOException {
		long time = 0;
		boolean isMoved = false;
		long pause = ApplicationProperties.getInteger( ConfigurationKeys.FILE_OPERATION_RETRY_TIMEOUT, 1000);
		while (time < timeout && !isMoved) {
			try {
				Files.move(src, dest, ATOMIC_MOVE);
				isMoved = true;
			} catch (IOException e) {
				log.warn("Move " + src + " to " + dest + " failed.", e);
				log.warn("Sleep " + pause + "ms and retry.");
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e1) {
					log.error("Waiting for moving " + src + " to " + dest + " failed. Got iterrupted", e1);
					throw new IOException(e1);
				}
				time += pause;
				if (time >= timeout) {
					throw new IOException("Move " + src + " to " + dest + " failed after " + timeout + "ms.");
				}
			}
		}
	}

	/**
	 * Deleted a Path recursivly, retries for timeout if it fails.
	 * 
	 * @param path
	 * @param timeout
	 * @throws IOException
	 */
	public static void waitAndDelete(Path path, long timeout) throws IOException {
		long time = 0;
		boolean isDeleted = false;
		long pause = ApplicationProperties.getInteger( ConfigurationKeys.FILE_OPERATION_RETRY_TIMEOUT, 1000);
		while (time < timeout && !isDeleted) {
			try {
				deleteRecursive(path);
				isDeleted = true;
			} catch (IOException e) {
				log.warn("Deleting " + path + " failed.", e);
				log.warn("Sleep " + pause + "ms and retry.");
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e1) {
					log.error("Waiting for deleting " + path + " failed. Got iterrupted", e1);
					throw new IOException(e1);
				}
				time += pause;
				if (time >= timeout) {
					throw new IOException("Deleting " + path + " failed after " + timeout + "ms.");
				}
			}
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
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath() + ".");
		} else if (!src.canRead()) { // check to ensure we have rights to the
			// source...
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath() + ".");
		}
		// is this a directory copy?
		if (src.isDirectory()) {
			if (!dest.exists()) { // does the destination already exist?
				// if not we need to make it exist if possible (note this is
				// mkdirs not mkdir)
				if (!dest.mkdirs()) {
					throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
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
				IOException wrapper = new IOException("copyFiles: Unable to copy file: " + src.getAbsolutePath() + "to"
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
	 * Get the document id from a cache filename
	 * 
	 * @param filename
	 *            The filename without the path
	 * @return String
	 */
	public static String getIdFromFilename(String filename) {
		String id = decodeId(filename.substring(0, filename.lastIndexOf(".")));
		return id;
	}

	/**
	 * Encode an id to be used in a filename.
	 * 
	 * @return String
	 */
	public static String encodeId(Serializable id) {
		if (id != null) {
			return encodeFileName(id.toString());
		} else {
			throw new IllegalArgumentException("Null is not allowed as id value.");
		}
	}

	/**
	 * Decode an id that was used in a filename.
	 * 
	 * @return String
	 */
	public static String decodeId(Serializable id) {
		if (id != null) {
			return decodeFileName(id.toString());
		} else {
			throw new IllegalArgumentException("Null is not allowed as id value.");
		}
	}

	/**
	 * Get all resources specified by pattern. For pattern syntax see (@link
	 * PathMatchingResourcePatternResolver).
	 * 
	 * @param packageName
	 * @return
	 * @throws IOException
	 */
	public static Resource[] getPackageContent(String pattern) throws IOException {
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
		Scanner scanner = null;
		try {
			scanner = new Scanner(is);
			scanner.useDelimiter("\\A");
			String content = scanner.next();
			scanner.close();
			return content;
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
		finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/**
	 * Read a file into a string.
	 * 
	 * @param path
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String readFile(Path path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(path);
		return new String(encoded, encoding);
	}


	/**
	 * Write a string to a file.
	 * 
	 * @param path
	 * @param content
	 * @param encoding
	 * @throws IOException
	 */
	public static void writeFile(Path path, String content, Charset encoding) throws IOException {
		Files.write(path, content.getBytes(encoding));
	}


}
