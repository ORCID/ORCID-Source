package org.orcid.frontend.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class OrcidWebAuthenticationDetails extends WebAuthenticationDetails {

    private static final long serialVersionUID = 1L;
 
    private String verificationCode;
    
    public OrcidWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = request.getParameter("verificationCode");
    }
 
    public String getVerificationCode() {
        return verificationCode;
    }
    
}
