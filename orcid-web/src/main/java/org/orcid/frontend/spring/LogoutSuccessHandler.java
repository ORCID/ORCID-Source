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
package org.orcid.frontend.spring;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/**
 * @author Angel Montenegro
 * */
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Resource
    private InternalSSOManager internalSSOManager;

    @SuppressWarnings("unchecked")
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String orcidId = authentication.getName();
        Cookie[] cookies = request.getCookies();
        // Delete cookie and token associated with that cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (InternalSSOManager.COOKIE_NAME.equals(cookie.getName())) {
                    try {
                        // If it is a valid cookie, extract the orcid value and
                        // remove the token and the cookie
                        HashMap<String, String> cookieValues = JsonUtils.readObjectFromJsonString(cookie.getValue(), HashMap.class);
                        if (cookieValues.containsKey(InternalSSOManager.COOKIE_KEY_ORCID) && !PojoUtil.isEmpty(cookieValues.get(InternalSSOManager.COOKIE_KEY_ORCID))) {
                            internalSSOManager.deleteToken(cookieValues.get(InternalSSOManager.COOKIE_KEY_ORCID), request, response);
                        } else {
                            // If it is not valid, just remove the cookie
                            cookie.setValue(StringUtils.EMPTY);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    } catch (RuntimeException re) {
                        // If any exception happens, but, the cookie exists,
                        // remove the cookie
                        cookie.setValue(StringUtils.EMPTY);
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                    break;
                }                
            }
        }

        // Delete token if exists
        if (!PojoUtil.isEmpty(orcidId)) {
            internalSSOManager.deleteToken(orcidId);
        }

        super.handle(request, response, authentication);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return "/signin";
    }
}
