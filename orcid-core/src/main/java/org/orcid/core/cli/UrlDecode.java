/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cli;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 
 * @author Will Simpson
 * 
 */
public class UrlDecode {
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLDecoder.decode(args[0], "UTF-8"));
    }
}
