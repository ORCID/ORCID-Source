/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 *
 * <p>Extends CookieLocaleResolver to support subdomain cookies
 *
 * @author Robert Peters
 */
public class CookieLocaleResolver extends org.springframework.web.servlet.i18n.CookieLocaleResolver {
    protected final Log logger = LogFactory.getLog(getClass());

    private boolean allowSubDomains = false;

    public Locale resolveLocale(HttpServletRequest request) {
        if (allowSubDomains) {
            try {
                String domain = new URL(request.getRequestURL().toString()).getHost();
                if (domain != null)
                    domain = "." + domain;
                this.setCookieDomain(domain);
            } catch (MalformedURLException e) {
                logger.error(e);
            }
        }
        return super.resolveLocale(request);
    }

    public boolean getAllowSubDomains() {
        return allowSubDomains;
    }

    public void setAllowSubDomains(boolean allowSubDomains) {
        this.allowSubDomains = allowSubDomains;
    }

}
