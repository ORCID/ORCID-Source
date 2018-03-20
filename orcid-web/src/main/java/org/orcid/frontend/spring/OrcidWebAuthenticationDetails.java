package org.orcid.frontend.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class OrcidWebAuthenticationDetails extends WebAuthenticationDetails {

    private static final long serialVersionUID = 1L;
    
    public static final String VERIFICATION_CODE_PARAMETER = "verificationCode";
    
    public static final String RECOVERY_CODE_PARAMETER = "recoveryCode";
 
    private String verificationCode;
    
    private String recoveryCode;
    
    public OrcidWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = getParameterOrAttribute(request, VERIFICATION_CODE_PARAMETER);
        recoveryCode = getParameterOrAttribute(request, RECOVERY_CODE_PARAMETER);
    }
 
    private String getParameterOrAttribute(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null) {
            value = (String) request.getAttribute(name);
        }
        return value;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
    
    public String getRecoveryCode() {
        return recoveryCode;
    }
    
}
