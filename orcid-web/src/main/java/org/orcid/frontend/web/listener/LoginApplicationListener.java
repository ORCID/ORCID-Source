package org.orcid.frontend.web.listener;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Declan Newman (declan) Date: 18/07/2012
 */
public class LoginApplicationListener implements ApplicationListener<ApplicationEvent> {

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
                    String orcid = userDetails.getOrcid();
                    String email = userDetails.getPrimaryEmail();
                    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                    LOGGER.info("User logged in with orcid={}, email={}, sessionid={}", new Object[] { orcid, email, sessionId });
                }
            }
        }
    }
}
