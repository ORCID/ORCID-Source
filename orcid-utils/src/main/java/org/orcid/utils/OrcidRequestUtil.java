/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Shobhit Tyagi
 */
public class OrcidRequestUtil {

	 public static String getIpAddress(HttpServletRequest request) {
    	String ipAddress = request.getHeader("X-FORWARDED-FOR");  
    	if (ipAddress != null) {
    		ipAddress = ipAddress.split("\\,")[0];
        } else {
        	ipAddress = request.getRemoteAddr();  
        }
        return ipAddress;
    }
}
