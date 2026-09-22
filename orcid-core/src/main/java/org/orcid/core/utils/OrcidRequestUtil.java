package org.orcid.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Shobhit Tyagi
 */
public class OrcidRequestUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidRequestUtil.class);

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return (attributes != null) ? attributes.getRequest() : null;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress != null) {
            ipAddress = ipAddress.split("\\,")[0];
        } else {
            ipAddress = request.getRemoteAddr();
        }
        if(LOGGER.isTraceEnabled()) {
            if(request.getRequestURL() != null) {
                LOGGER.trace("Request URL: {}", request.getRequestURL().toString());
            }
            LOGGER.trace("Query String: {}", request.getQueryString());
            LOGGER.trace("IP Address: {}", ipAddress);
        }
        return ipAddress;
    }
}
