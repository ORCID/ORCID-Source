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
