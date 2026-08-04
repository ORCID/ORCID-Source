package org.orcid.frontend.spring.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

public class OrcidCookieLocaleResolver extends CookieLocaleResolver {

    public static final List<Locale> availableLocales = Arrays.asList(new Locale("ar"), new Locale("cs"), Locale.ENGLISH, new Locale("es"), Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN, Locale.JAPANESE, Locale.KOREAN,
            new Locale("pl"), new Locale("pt"), new Locale("ru"), new Locale("tr"),Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE);
    public static final List<Locale> devLocales = Arrays.asList(new Locale("lr"), new Locale("rl"), new Locale("xx"));

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        // If context is null, pass null to super to handle cookie removal automatically
        if (localeContext == null) {
            super.setLocaleContext(request, response, null);
            return;
        }

        Locale locale = localeContext.getLocale();
        TimeZone timeZone = null;
        
        if (localeContext instanceof TimeZoneAwareLocaleContext) {
            timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
        }

        // Run custom Orcid validation
        if (locale != null && !availableLocales.contains(locale)) {
            Locale justLang = new Locale(locale.getLanguage());
            if (availableLocales.contains(justLang) || devLocales.contains(justLang)) {
                locale = justLang;
            } else {
                locale = Locale.ENGLISH;
            }
        }

        setCookieSecure(true);
        
        // Wrap the validated locale/timezone and pass it to the parent class.
        // Spring 6 will automatically build the cookie, add it to the response, 
        // and set the required LOCALE and TIME_ZONE request attributes for the rest of the request processing.
        LocaleContext validatedContext = new SimpleTimeZoneAwareLocaleContext(locale, timeZone);
        super.setLocaleContext(request, response, validatedContext);
    }
    
    @Override
    public void setCookieSecure(boolean cookieSecure) {
        super.setCookieSecure(cookieSecure);
    }
}