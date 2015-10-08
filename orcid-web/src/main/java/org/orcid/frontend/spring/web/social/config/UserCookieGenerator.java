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
package org.orcid.frontend.spring.web.social.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.CookieGenerator;

/**
 * @author Shobhit Tyagi
 */
public final class UserCookieGenerator {

	private final CookieGenerator cookieGenerator = new CookieGenerator();

	public UserCookieGenerator() {
		cookieGenerator.setCookieName("orcid");
	}

	public void addCookie(String userId, HttpServletResponse response) {
		cookieGenerator.addCookie(response, userId);
	}

	public void removeCookie(HttpServletResponse response) {
		cookieGenerator.addCookie(response, "");
	}

	public String readCookieValue(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieGenerator.getCookieName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

}