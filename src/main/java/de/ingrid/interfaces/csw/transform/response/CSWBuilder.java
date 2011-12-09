/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.transform.response;

import java.text.SimpleDateFormat;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public abstract class CSWBuilder implements ICSWBuilder {

    protected static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    protected static String INVALID_XML_ID_CHARS_REGEX_STR = "[^_\\.\\-A-Za-z0-9]";
    
}
