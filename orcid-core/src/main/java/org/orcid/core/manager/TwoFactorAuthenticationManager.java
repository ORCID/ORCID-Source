package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface TwoFactorAuthenticationManager {

    String getQRCode(String orcid);

    void disable2FA(String orcid);

    boolean userUsing2FA(String orcid);

    void enable2FA(String orcid);
    
    boolean verificationCodeIsValid(String code, String orcid);

    List<String> getBackupCodes(String orcid);

    String getSecret(String orcid);

    boolean verificationCodeIsValid(String code, ProfileEntity profileEntity);

}
