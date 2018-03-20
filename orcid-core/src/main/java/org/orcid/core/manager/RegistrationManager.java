package org.orcid.core.manager;

import java.util.Locale;

import org.orcid.core.utils.VerifyRegistrationToken;
import org.orcid.pojo.ajaxForm.Registration;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface RegistrationManager {

    VerifyRegistrationToken parseEncyrptedParamsForVerification(String encryptedParams);

    void resetUserPassword(String toEmail, String userOrcid, Boolean isClaimed);

    String createMinimalRegistration(Registration registration, boolean usedCaptcha, Locale locale, String ip);
    
    Long getCount();
}
