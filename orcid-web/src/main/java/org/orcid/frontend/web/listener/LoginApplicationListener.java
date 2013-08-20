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
package org.orcid.frontend.web.listener;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.web.context.request.RequestContextHolder;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;

/**
 * 2011-2012 - ORCID
 * 
 * @author Declan Newman (declan) Date: 18/07/2012
 */
public class LoginApplicationListener implements ApplicationListener<ApplicationEvent> {

    public static final Counter LOGIN_COUNTER = Metrics.newCounter(LoginApplicationListener.class, "orcid-frontend-logins");

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginApplicationListener.class);

    /**
     * Handle an application event.
     * 
     * @param event
     *            the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            Object source = event.getSource();
            if (source instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) source;
                Object principal = token.getPrincipal();
                if (principal instanceof OrcidProfileUserDetails) {
                    OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
                    String orcid = userDetails.getRealOrcid();
                    String email = userDetails.getPrimaryEmail();
                    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                    LOGGER.info("User logged in with orcid={}, email={}, sessionid={}", new Object[] { orcid, email, sessionId });
                }
            }
            LOGIN_COUNTER.inc();
        }
    }
}
