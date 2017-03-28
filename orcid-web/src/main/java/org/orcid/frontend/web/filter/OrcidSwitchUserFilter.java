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
package org.orcid.frontend.web.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.frontend.web.exception.SwitchUserAuthenticationException;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidSwitchUserFilter extends SwitchUserFilter {

    @Resource
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private InternalSSOManager internalSSOManager;        
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {                
        if(internalSSOManager.enableCookie()) {            
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            if (requiresSwitchUser(request)) {
                // Add the cookie for the delegate user
                String targetUserOrcid = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
                if (!PojoUtil.isEmpty(targetUserOrcid)) {
                    //If it is switching back to the original user
                    if(isSwitchingBack(request)) {
                        internalSSOManager.getAndUpdateCookie(targetUserOrcid, request, response);
                    } else {
                        //If it is switching user
                        internalSSOManager.writeCookie(targetUserOrcid, request, response);
                    }            
                }
            }
        }
        
        super.doFilter(req, res, chain);
    }

    @Override
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        String targetUserOrcid = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
        ProfileEntity profileEntity = sourceManager.retrieveSourceProfileEntity();
        if (OrcidType.ADMIN.equals(profileEntity.getOrcidType())) {
            return super.attemptSwitchUser(request);
        }
        // If we are switching back to me it is OK
        if (isSwitchingBack(request)) {
            return super.attemptSwitchUser(request);
        }
        for (GivenPermissionByEntity gpbe : profileEntity.getGivenPermissionBy()) {
            if (gpbe.getGiver().getId().equals(targetUserOrcid)) {
                return super.attemptSwitchUser(request);
            }
        }
        Object params[] = {};
        throw new SwitchUserAuthenticationException(localeManager.resolveMessage("web.orcid.switchuser.exception", params));
    }

    private boolean isSwitchingBack(HttpServletRequest request) {
        String targetUserOrcid = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
        String realUser = sourceManager.retrieveRealUserOrcid();
        return targetUserOrcid.equals(realUser);
    }
    
}
