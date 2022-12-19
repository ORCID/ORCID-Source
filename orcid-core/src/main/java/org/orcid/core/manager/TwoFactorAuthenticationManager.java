package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface TwoFactorAuthenticationManager {

    String getQRCode(String orcid);

    void disable2FA(String orcid);
    
    void adminDisable2FA(String orcid, String adminOrcidId);

    boolean userUsing2FA(String orcid);

    List<String> enable2FA(String orcid);
    
    boolean verificationCodeIsValid(String code, String orcid);

    String getSecret(String orcid);

    boolean verificationCodeIsValid(String code, ProfileEntity profileEntity);

}
