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
import java.net.URLEncoder;

/**
 * 
 * @author Will Simpson
 * 
 */
public class UrlEncode {
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode(args[0], "UTF-8"));
    }
}
