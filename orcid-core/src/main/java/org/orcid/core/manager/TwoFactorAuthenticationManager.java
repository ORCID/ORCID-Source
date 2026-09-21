package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AuthChallenge;

public interface TwoFactorAuthenticationManager {

    String getQRCode(String orcid);

    void disable2FA(String orcid);

    /**
     * The R3.5 transaction: turns 2FA off, deletes the stored recovery phone number and invalidates the unused backup
     * codes, recording a distinct profile event so support can tell a recovery phone disable from a self service one.
     * Used by the sign in and the authentication challenge recovery phone flows, where the user proved possession of
     * the recovery number instead of a 2FA code, and the number is consumed by that single use.
     *
     * @param orcid
     *            the ORCID iD of the record whose 2FA is being disabled
     */
    void disable2FAByRecoveryPhone(String orcid);

    void adminDisable2FA(String orcid, String adminOrcidId);

    boolean userUsing2FA(String orcid);

    List<String> enable2FA(String orcid);

    boolean verificationCodeIsValid(String code, String orcid);

    String getSecret(String orcid);

    boolean verificationCodeIsValid(String code, ProfileEntity profileEntity);

    boolean validateTwoFactorAuthForm(String orcid, AuthChallenge form);
}
