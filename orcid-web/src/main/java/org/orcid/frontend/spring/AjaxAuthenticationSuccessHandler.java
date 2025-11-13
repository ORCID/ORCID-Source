package org.orcid.frontend.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.common.manager.EventManager;
import org.orcid.core.togglz.Features;
import org.orcid.persistence.jpa.entities.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

/*
 * Trying to make spring login for
 * http://stackoverflow.com/questions/10811623/spring-security-programatically-logging-in
 * 
 * @author Robert Peters (rcpeters)
 */
public class AjaxAuthenticationSuccessHandler extends AjaxAuthenticationSuccessHandlerBase {

    @Autowired
    EventManager eventManager;
    
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    	String targetUrl = getTargetUrl(request, response, authentication);
        if (Features.EVENTS.isActive()) {
            eventManager.createEvent(EventType.SIGN_IN, request);
        }
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");        
    }
}
