package org.orcid.core.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface InternalSSOManager {

    public static final String COOKIE_NAME = "orcid_token";
    public static final String COOKIE_KEY_TOKEN = "token";
    public static final String COOKIE_KEY_ORCID = "orcid";

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
     * Gets the cookie information from DB, update it and store it in the
     * response Use this method on switch user when switching back to the
     * original user
     * 
     * @param orcid
     * @param request
     * @param response
     * */
    void getAndUpdateCookie(String orcid, HttpServletRequest request, HttpServletResponse response);

    /**
     * Deletes an existing token
     * 
     * @param orcid
     * */
    void deleteToken(String orcid);

    /**
     * Deletes an existing token and removes the cookie from the response
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

    /**
     * Checks the domain it is working on and decide if we should set up the
     * cookies or not
     * 
     * @return true if the cookie functionality should be on
     * */
    boolean enableCookie();
}
