/*
 * $Id: OnewayListener.java,v 1.4 2002/01/04 19:53:00 ofung Exp $
 * $Revision: 1.4 $
 * $Date: 2002/01/04 19:53:00 $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.messaging;

import javax.xml.soap.*;

/**
 * A marker interface for components (for example, servlets) that are 
 * intended to be consumers of one-way (asynchronous) JAXM messages.  
 * The receiver of a one-way message is sent the message in one operation,
 * and it sends the response in another separate operation. The time
 * interval between the receipt of a one-way message and the sending
 * of the response may be measured in fractions of seconds or days.
 * <P>
 * The implementation of the <code>onMessage</code> method defines
 * how the receiver responds to the <code>SOAPMessage</code> object
 * that was passed to the <code>onMessage</code> method.
 *
 * @see JAXMServlet
 * @see ReqRespListener
 */
public interface OnewayListener {

    /**
     * Passes the given <code>SOAPMessage</code> object to this
     * <code>OnewayListener</code> object.  
     * This method is invoked behind the scenes, typically by the
     * container (servlet or EJB container) after the messaging provider
     * delivers the message to the container. 
     *
     * It is expected that EJB Containers will deliver JAXM messages
     * to EJB components using message driven Beans that implement the
     * <code>javax.xml.messaging.OnewayListener</code> interface.
     *
     * @param message the <code>SOAPMessage</code> object to be passed to this
     *                <code>OnewayListener</code> object
     */
    public void onMessage(SOAPMessage message);
}
