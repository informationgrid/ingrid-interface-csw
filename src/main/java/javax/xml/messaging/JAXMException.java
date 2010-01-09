/*
 * $Id: JAXMException.java,v 1.14 2002/01/04 19:53:00 ofung Exp $
 * $Revision: 1.14 $
 * $Date: 2002/01/04 19:53:00 $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.xml.messaging;

import javax.xml.soap.*;

/**
 * An exception that signals that a JAXM exception has occurred. A
 * <code>JAXMException</code> object may contain a <code>String</code>
 * that gives the reason for the exception, an embedded
 * <code>Throwable</code> object, or both. This class provides methods
 * for retrieving reason messages and for retrieving the embedded
 * <code>Throwable</code> object.
 *
 * <P> Typical reasons for throwing a <code>JAXMException</code>
 * object are problems such as not being able to send a message and
 * not being able to get a connection with the provider.  Reasons for
 * embedding a <code>Throwable</code> object include problems such as
 * an input/output errors or a parsing problem, such as an error
 * parsing a header.
 */
public class JAXMException extends SOAPException {

    private Throwable cause;

    /**
     * Constructs a <code>JAXMException</code> object with no
     * reason or embedded <code>Throwable</code> object.
     */
    public JAXMException() {
        super();
    }

    /**
     * Constructs a <code>JAXMException</code> object with the given
     * <code>String</code> as the reason for the exception being thrown.
     *
     * @param reason a <code>String</code> giving a description of what 
     *        caused this exception
     */
    public JAXMException(String reason) {
        super(reason);
    }

    /**
     * Constructs a <code>JAXMException</code> object with the given
     * <code>String</code> as the reason for the exception being thrown
     * and the given <code>Throwable</code> object as an embedded
     * exception.
     *
     * @param reason a <code>String</code> giving a description of what 
     *        caused this exception
     * @param cause a <code>Throwable</code> object that is to
     *        be embedded in this <code>JAXMException</code> object
     */
    public JAXMException(String reason, Throwable cause) {
       super (reason, cause);
    }

    /**
     * Constructs a <code>JAXMException</code> object initialized
     * with the given <code>Throwable</code> object.
     *
     * @param cause a <code>Throwable</code> object that is to
     *        be embedded in this <code>JAXMException</code> object
     */
    public JAXMException(Throwable cause) {
	super(cause);
    }
}
