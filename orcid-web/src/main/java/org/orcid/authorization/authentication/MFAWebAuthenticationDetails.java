package org.orcid.authorization.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.thrift.TSerializable;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.Serializable;
import java.util.Objects;

public class MFAWebAuthenticationDetails implements Serializable {

    public static final String VERIFICATION_CODE_PARAMETER = "verificationCode";
    
    public static final String RECOVERY_CODE_PARAMETER = "recoveryCode";
 
    private final String verificationCode;
    
    private final String recoveryCode;

    private final String remoteAddress;

    private final String sessionId;
    
    public MFAWebAuthenticationDetails(HttpServletRequest request) {
        verificationCode = getParameterOrAttribute(request, VERIFICATION_CODE_PARAMETER);
        recoveryCode = getParameterOrAttribute(request, RECOVERY_CODE_PARAMETER);
        remoteAddress = request.getRemoteAddr();
        HttpSession session = request.getSession(false);
        sessionId = session != null ? session.getId() : null;
    }

    public MFAWebAuthenticationDetails(String remoteAddress, String sessionId, String verificationCode, String recoveryCode) {
        this.verificationCode = verificationCode;
        this.recoveryCode = recoveryCode;
        this.remoteAddress = remoteAddress;
        this.sessionId = sessionId;
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

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MFAWebAuthenticationDetails that = (MFAWebAuthenticationDetails) o;
        return Objects.equals(verificationCode, that.verificationCode) && Objects.equals(recoveryCode, that.recoveryCode) && Objects.equals(remoteAddress, that.remoteAddress) && Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verificationCode, recoveryCode, remoteAddress, sessionId);
    }
}
