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
import org.orcid.persistence.jpa.entities.InternalSSOEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

public class InternalSSOManagerImpl implements InternalSSOManager {

    @Value("${org.orcid.core.soo.token.validity_minutes:10}")
    private int maxAgeMinutes;

    @Value("${org.orcid.security.cookie.allowed_domain:.orcid.org}")
    private String allowedDomain;
    
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
        //Generate the token
        String token = generateAndStoreToken(orcid);
        //Save the cookie in the response
        populateCookie(orcid, token, request, response);
    }

    private void populateCookie(String orcid, String token, HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, String> cookieValues = new HashMap<String, String>();
        cookieValues.put(COOKIE_KEY_ORCID, orcid);
        cookieValues.put(COOKIE_KEY_TOKEN, token);
        
        String jsonCookie = JsonUtils.convertToJsonString(cookieValues);
        
        // Return it as a cookie in the response
        Cookie tokenCookie = new Cookie(COOKIE_NAME, jsonCookie);
        tokenCookie.setMaxAge(maxAgeMinutes * 60);
        tokenCookie.setPath("/");
        tokenCookie.setSecure(true);
        tokenCookie.setHttpOnly(true);   
        tokenCookie.setDomain(allowedDomain.trim());
        response.addCookie(tokenCookie);        
    } 
    
    private String generateAndStoreToken(String orcid) {
        // Generate a random token
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        byte[] encoded = Base64.encodeBase64(bytes);
        String token = new String(encoded);

        // Insert it into the DB
        internalSSODao.insert(orcid, token);
        return token;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void updateCookie(String orcid, HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    HashMap<String, String> cookieValues = JsonUtils.readObjectFromJsonString(cookie.getValue(), HashMap.class);
                    if(cookieValues.containsKey(COOKIE_KEY_TOKEN)) {
                        if (internalSSODao.update(orcid, cookieValues.get(COOKIE_KEY_TOKEN))) {
                            //Create new cookie
                            Cookie tokenCookie = new Cookie(COOKIE_NAME, cookie.getValue());
                            tokenCookie.setMaxAge(maxAgeMinutes * 60);
                            tokenCookie.setPath("/");
                            tokenCookie.setSecure(true);
                            tokenCookie.setHttpOnly(true);    
                            tokenCookie.setDomain(allowedDomain.trim());
                            //Add new cookie to response
                            response.addCookie(tokenCookie);
                        } 
                    } 
                    break;
                }
            }
        }
    }

    @Override
    public void getAndUpdateCookie(String orcid, HttpServletRequest request, HttpServletResponse response) {
        InternalSSOEntity existingCookie = internalSSODao.find(orcid);
        if(existingCookie != null) {
            internalSSODao.update(existingCookie.getId(), existingCookie.getToken());
            HashMap<String, String> cookieValues = new HashMap<String, String>();
            cookieValues.put(COOKIE_KEY_ORCID, orcid);
            cookieValues.put(COOKIE_KEY_TOKEN, existingCookie.getToken());
            
            String jsonCookie = JsonUtils.convertToJsonString(cookieValues);
            Cookie tokenCookie = new Cookie(COOKIE_NAME, jsonCookie);
            tokenCookie.setMaxAge(maxAgeMinutes * 60);
            tokenCookie.setPath("/");
            tokenCookie.setSecure(true);
            tokenCookie.setHttpOnly(true);    
            tokenCookie.setDomain(allowedDomain.trim());
            //Add new cookie to response
            response.addCookie(tokenCookie);
        }
    }    
    
    @Override
    public void deleteToken(String orcid) {
        if(!PojoUtil.isEmpty(orcid)) {
            // Delete the token from DB
            internalSSODao.delete(orcid);
        }        
    }
    
    @Override
    public void deleteToken(String orcid, HttpServletRequest request, HttpServletResponse response) {
        this.deleteToken(orcid);
        // Delete the cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    cookie.setValue(StringUtils.EMPTY);
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);
                    cookie.setDomain(allowedDomain.trim());
                    response.addCookie(cookie);
                }
            }
        }               
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean verifyToken(String orcid, String cookie) {        
        HashMap<String, String> cookieValues = JsonUtils.readObjectFromJsonString(cookie, HashMap.class);
        if(!cookieValues.containsKey(COOKIE_KEY_TOKEN)) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -maxAgeMinutes);
        Date maxAge = c.getTime();
        return internalSSODao.verify(orcid, cookieValues.get(COOKIE_KEY_TOKEN), maxAge);
    }
    
    @Override
    public boolean enableCookie() {
        return !allowedDomain.equals("localhost");
    }
}
