package org.orcid.frontend.spring.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

public class OrcidCookieLocaleResolver extends CookieLocaleResolver {

    public static final List<Locale> availableLocales = Arrays.asList(new Locale("ar"), new Locale("cs"), Locale.ENGLISH, new Locale("es"), Locale.FRENCH, Locale.ITALIAN, Locale.JAPANESE, Locale.KOREAN,
            new Locale("pt"), new Locale("ru"), Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE);
    public static final List<Locale> devLocales = Arrays.asList(new Locale("lr"), new Locale("rl"), new Locale("xx"));

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        Locale locale = null;
        TimeZone timeZone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
            }

            if (!availableLocales.contains(locale)) {
                Locale justLang = new Locale(locale.getLanguage());
                if (availableLocales.contains(justLang) || devLocales.contains(justLang)) {
                    locale = justLang;
                } else {
                    locale = Locale.ENGLISH;
                }
            }

            addCookie(response, (locale != null ? toLocaleValue(locale) : "-") + (timeZone != null ? ' ' + timeZone.getID() : ""));
        } else {
            removeCookie(response);
        }
        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, (locale != null ? locale : determineDefaultLocale(request)));
        request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, (timeZone != null ? timeZone : determineDefaultTimeZone(request)));
    }
}
