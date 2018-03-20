package org.orcid.frontend.spring;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.utils.OrcidRequestUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

public class AjaxAuthenticationSuccessHandlerBase extends SimpleUrlAuthenticationSuccessHandler {

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    protected InternalSSOManager internalSSOManager;

    @Resource
    protected SourceManager sourceManager;

    @Resource
    protected LocaleContextResolver localeContextResolver;
    
    @Resource
    private ProfileEntityManager profileEntityManager;

    protected String getTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String targetUrl = orcidUrlManager.determineFullTargetUrlFromSavedRequest(request, response);
        if (authentication != null) {
            String orcidId = authentication.getName();
            checkLocale(request, response, orcidId);
            if (internalSSOManager.enableCookie()) {
                internalSSOManager.writeCookie(orcidId, request, response);
            }
            profileEntityManager.updateLastLoginDetails(orcidId, OrcidRequestUtil.getIpAddress(request));
        }
        if (targetUrl == null) {
            targetUrl = determineFullTargetUrl(request, response);
        }

        return targetUrl;
    }

    protected String getRealUserOrcid() {
        return sourceManager.retrieveRealUserOrcid();
    }

    // new method - persist which ever local they logged in with
    private void checkLocale(HttpServletRequest request, HttpServletResponse response, String orcidId) {
        Locale lastKnownLocale = profileEntityManager.retrieveLocale(orcidId);
        if (lastKnownLocale != null) {
            localeContextResolver.setLocale(request, response, LocaleUtils.toLocale(lastKnownLocale.value()));
        } else {
            // have to read the cookie directly since spring has
            // populated the request locale yet
            CookieLocaleResolver clr = new CookieLocaleResolver();
            // must match <property name="cookieName" value="locale_v3"
            // />
            clr.setCookieName("locale_v3");
            Locale cookieLocale = org.orcid.jaxb.model.common_v2.Locale.fromValue(clr.resolveLocale(request).toString());

            // update the users preferences, so that
            // send out emails in their last chosen language
            profileEntityManager.updateLocale(orcidId, cookieLocale);
        }
    }

    private String determineFullTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return orcidUrlManager.getServerStringWithContextPath(request) + determineTargetUrl(request, response);
    }
}
