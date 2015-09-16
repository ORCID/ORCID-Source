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

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.orcid.core.manager.InternalSSOManager;
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

        // Return it as a cookie in the response
        Cookie tokenCookie = new Cookie(COOKIE_NAME, token);
        tokenCookie.setMaxAge(maxAgeMinutes * 60);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
    }

    @Override
    public void updateCookie(String orcid, HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    if (internalSSODao.update(orcid, cookie.getValue())) {
                        cookie.setMaxAge(maxAgeMinutes * 60);
                        response.addCookie(cookie);                        
                    } else {
                        // TODO: throw error, couldn't update cookie
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
                    response.addCookie(cookie);
                }
            }
        }
    }

    @Override
    public boolean verifyToken(String orcid, String encryptedToken) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -maxAgeMinutes);
        Date maxAge = c.getTime();
        return internalSSODao.verify(orcid, encryptedToken, maxAge);
    }
}
