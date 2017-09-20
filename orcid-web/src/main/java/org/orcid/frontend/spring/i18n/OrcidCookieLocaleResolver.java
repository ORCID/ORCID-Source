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
package org.orcid.frontend.spring.i18n;

import java.util.Locale;
import java.util.TimeZone;

import org.hsqldb.lib.StringUtil;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OrcidCookieLocaleResolver extends CookieLocaleResolver {
    
    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
            Locale locale = null;
            TimeZone timeZone = null;
            if (localeContext != null) {
                    locale = localeContext.getLocale();
                    if (localeContext instanceof TimeZoneAwareLocaleContext) {
                            timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
                    }                                        
                    
                    String lang = locale.getLanguage();
                    String country = locale.getCountry();
                    
                    //TODO: Should we allow the language even if the country is wrong? as we do it currently, or should we default it to English?
                    if(!StringUtil.isEmpty(country)) {
                        OrcidWebLocale oLocale = OrcidWebLocale.find(lang, country);
                        if(oLocale == null) {
                            
                        }
                    }
                    //END TODO
                    
                    addCookie(response,
                                    (locale != null ? toLocaleValue(locale) : "-") + (timeZone != null ? ' ' + timeZone.getID() : ""));
            }
            else {
                    removeCookie(response);
            }
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
                            (locale != null ? locale : determineDefaultLocale(request)));
            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
                            (timeZone != null ? timeZone : determineDefaultTimeZone(request)));
    }
}
