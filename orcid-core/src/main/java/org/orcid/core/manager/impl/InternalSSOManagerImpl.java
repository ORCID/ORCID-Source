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
package org.orcid.core.manager.impl;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.InternalSSODao;
import org.springframework.beans.factory.annotation.Value;

public class InternalSSOManagerImpl implements InternalSSOManager {

    @Value("${org.orcid.core.soo.token.validity_minutes:10}")
    private int maxAgeMinutes;

    @Resource
    InternalSSODao internalSSODao;

    public InternalSSOManagerImpl() {

    }

    public InternalSSOManagerImpl(int maxAgeInMunutes) {
        this.maxAgeMinutes = maxAgeInMunutes;
    }

    @Override
    public void writeCookie(String orcid, HttpServletRequest request, HttpServletResponse response) {
        // Deletes previous cookie if exists
        deleteToken(orcid, request, response);
        // Generate a random token
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        byte[] encoded = Base64.encodeBase64(bytes);
        String token = new String(encoded);

        // Insert it into the DB
        internalSSODao.insert(orcid, token);

        HashMap<String, String> cookieValues = new HashMap<String, String>();
        cookieValues.put("orcid", orcid);
        cookieValues.put("token", token);
        
        String jsonCookie = JsonUtils.convertToJsonString(cookieValues);
        
        // Return it as a cookie in the response
        Cookie tokenCookie = new Cookie(COOKIE_NAME, jsonCookie);
        tokenCookie.setMaxAge(maxAgeMinutes * 60);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);
        response.addCookie(tokenCookie);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateCookie(String orcid, HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    HashMap<String, String> cookieValues = JsonUtils.readObjectFromJsonString(cookie.getValue(), HashMap.class);
                    if(cookieValues.containsKey("token")) {
                        if (internalSSODao.update(orcid, cookieValues.get("token"))) {
                            cookie.setMaxAge(maxAgeMinutes * 60);
                            cookie.setHttpOnly(true);
                            response.addCookie(cookie);
                        } else {
                            // TODO: throw error, couldn't update cookie
                        }
                    } else {
                            // TODO: throw an exception, the cookie dont have the key
                    }                        
                }
            }
        }
    }

    @Override
    public void deleteToken(String orcid, HttpServletRequest request, HttpServletResponse response) {
        // Delete the DB row
        internalSSODao.delete(orcid);
        // Delete the cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    cookie.setValue(StringUtils.EMPTY);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                }
            }
        }
    }

    @Override
    public boolean verifyToken(String orcid, String token) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -maxAgeMinutes);
        Date maxAge = c.getTime();
        return internalSSODao.verify(orcid, token, maxAge);
    }
}
