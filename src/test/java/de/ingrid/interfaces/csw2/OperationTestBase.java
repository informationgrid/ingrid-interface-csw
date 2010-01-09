/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import junit.framework.TestCase;

public abstract class OperationTestBase extends TestCase {

	/**
	 * InputStream class used with servlet response
	 */
	protected class TestServletInputStream extends ServletInputStream {
		Reader buf = null;
		
		public TestServletInputStream(String input) {
			this.buf = new StringReader(input);
		}

		@Override
		public int read() throws IOException {
			return this.buf.read();
		}
	}

	/**
	 * OutputStream class used with servlet response
	 */
	protected class TestServletOutputStream extends ServletOutputStream {
		StringBuffer buf = null;
		
		public TestServletOutputStream(StringBuffer buf) {
			this.buf = buf;
		}
		
		@Override  
		public void write(int c) throws IOException {  
			this.buf.append((char)c);
		}  
	}
}