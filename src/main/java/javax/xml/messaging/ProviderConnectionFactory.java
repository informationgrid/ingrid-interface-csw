/*
 * $Id: ProviderConnectionFactory.java,v 1.10.2.1 2002/04/09 22:44:07 mode Exp $
 * $Revision: 1.10.2.1 $
 * $Date: 2002/04/09 22:44:07 $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.xml.messaging;

import javax.xml.soap.*;

/**
 * A factory for creating connections to a particular messaging provider.
 * A <code>ProviderConnectionFactory</code> object can be obtained in two
 * different ways.
 * <ul>
 * <li>Call the <code>ProviderConnectionFactory.newInstance</code>
 * method to get an instance of the default <code>ProviderConnectionFactory</code>
 * object.<br>
 *  This instance can be used to create a <code>ProviderConnection</code>
 * object that connects to the default provider implementation.
 * <PRE>
 *      ProviderConnectionFactory pcf = ProviderConnectionFactory.newInstance();
 *      ProviderConnection con = pcf.createConnection();
 * </PRE>
 * <P>
 * <li>Retrieve a <code>ProviderConnectionFactory</code> object
 * that has been registered with a naming service based on Java Naming and 
 * Directory Interface<sup><font size=-2>TM</font></sup> (JNDI) technology.<br>
 * In this case, the <code>ProviderConnectionFactory</code> object is an 
 * administered object that was created by a container (a servlet or Enterprise
 * JavaBeans<sup><font size=-2>TM</font></sup> container). The
 * <code>ProviderConnectionFactory</code> object was configured in an implementation-
 * specific way, and the connections it creates will be to the specified
 * messaging provider. <br>
 * <P>
 * Registering a <code>ProviderConnectionFactory</code> object with a JNDI naming service
 * associates it with a logical name. When an application wants to establish a
 * connection with the provider associated with that
 * <code>ProviderConnectionFactory</code> object, it does a lookup, providing the
 * logical name.  The application can then use the 
 * <code>ProviderConnectionFactory</code>
 * object that is returned to create a connection to the messaging provider.
 * The first two lines of the  following code fragment use JNDI methods to 
 * retrieve a <code>ProviderConnectionFactory</code> object. The third line uses the
 * returned object to create a connection to the JAXM provider that was 
 * registered with "ProviderXYZ" as its logical name.
 * <PRE>
 *      Context ctx = new InitialContext();
 *      ProviderConnectionFactory pcf = (ProviderConnectionFactory)ctx.lookup(
 *                                                                 "ProviderXYZ");
 *      ProviderConnection con = pcf.createConnection();
 * </PRE>
 * </ul>
 */
public abstract class ProviderConnectionFactory {
    /**
     * Creates a <code>ProviderConnection</code> object to the messaging provider that
     * is associated with this <code>ProviderConnectionFactory</code>
     * object. 
     *
     * @return a <code>ProviderConnection</code> object that represents 
     *         a connection to the provider associated with this 
     *         <code>ProviderConnectionFactory</code> object
     * @exception JAXMException if there is an error in creating the
     *            connection
     */
    public abstract ProviderConnection createConnection() 
        throws JAXMException;

    static private final String PCF_PROPERTY
        = "javax.xml.messaging.ProviderConnectionFactory";

    static private final String DEFAULT_PCF 
        = "com.sun.xml.messaging.jaxm.client.remote.ProviderConnectionFactoryImpl";

    /**
     * Creates a default <code>ProviderConnectionFactory</code> object. 
     *
     * @return a new instance of a <code>ProviderConnectionFactory</code>
     *
     * @exception JAXMException if there was an error creating the
     *            default <code>ProviderConnectionFactory</code>
     */
    public static ProviderConnectionFactory newInstance() 
        throws JAXMException
    {
        try {
	    return (ProviderConnectionFactory)
                FactoryFinder.find(PCF_PROPERTY,
                                   DEFAULT_PCF);
        } catch (Exception ex) {
            throw new JAXMException("Unable to create "+
                                    "ProviderConnectionFactory: "
                                    +ex.getMessage());
        }
    }
}
