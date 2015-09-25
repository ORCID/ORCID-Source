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
package org.orcid.core.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface InternalSSOManager {

    public static final String COOKIE_NAME = "orcid_token";
    public static final String COOKIE_PARAM_ORCID = "orcid";
    public static final String COOKIE_PARAM_TOKEN = "token";
    
    /**
     * Creates a new token and populate it in a cookie
     * 
     * @param orcid
     * @param request
     * @param response
     * */
    void writeCookie(String orcid, HttpServletRequest request, HttpServletResponse response);
    
    /**
     * Updates an existing cookie
     *      
     * @param orcid
     * @param request
     * @param response
     * */
    void updateCookie(String orcid, HttpServletRequest request, HttpServletResponse response);
    
    /**
     * Deletes an existing token
     * 
     * @param orcid
     * @param request
     * @param response
     * */
    void deleteToken(String orcid, HttpServletRequest request, HttpServletResponse response);
    
    /**
     * Returns true if the given token is still valid
     * 
     * @param orcid
     * @param token
     * */
    boolean verifyToken(String orcid, String token);       
}
