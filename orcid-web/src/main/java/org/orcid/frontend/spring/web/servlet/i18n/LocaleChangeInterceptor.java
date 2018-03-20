package org.orcid.frontend.spring.web.servlet.i18n;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Interceptor that allows for changing the current locale on every request,
 * via a configurable request parameter.
 *
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see org.springframework.web.servlet.LocaleResolver
 */
public class LocaleChangeInterceptor extends HandlerInterceptorAdapter {

        /**
         * Default name of the locale specification parameter: "locale".
         */
        public static final String DEFAULT_PARAM_NAME = "locale";

        private String paramName = DEFAULT_PARAM_NAME;
        
        private Pattern langPattern = Pattern.compile("(&|\\?)" + paramName +"=([^&]*)");
        


        /**
         * Set the name of the parameter that contains a locale specification
         * in a locale change request. Default is "locale".
         */
        public void setParamName(String paramName) {
                this.paramName = paramName;
                langPattern = Pattern.compile("(&|\\?)" + paramName +"=([^&]*)");
        }

        /**
         * Return the name of the parameter that contains a locale specification
         * in a locale change request.
         */
        public String getParamName() {
                return this.paramName;
        }


        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                        throws ServletException {

                String newLocale = request.getParameter(this.paramName);
                if (newLocale == null) {
                    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                    if (savedRequest != null) {
                        String url = savedRequest.getRedirectUrl();
                        Matcher matcher = langPattern.matcher(url);
                        if (matcher.find()) {
                            newLocale = matcher.group(2);
                        }
                    }
                }
                if (newLocale != null) {
                        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                        if (localeResolver == null) {
                                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
                        }
                        try {
                           localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));
                        } catch (Exception e) {
                            /* 
                             * Ignore exceptions from invalid locales as it will cause a 500 error and
                             * continue with the last valid locale set.
                             */
                        }
                }
                // Proceed in any case.
                return true;
        }

}
