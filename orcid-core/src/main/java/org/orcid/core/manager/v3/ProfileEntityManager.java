package org.orcid.core.manager.v3;

import java.util.Date;
import java.util.List;

import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Reactivation;

/**
 * User: Declan Newman (declan) Date: 10/02/2012 </p>
 */
public interface ProfileEntityManager extends ProfileEntityManagerReadOnly {

    String findByCreditName(String creditName);
    
    boolean orcidExists(String orcid);

    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);    

    boolean deprecateProfile(String deprecated, String primary, String deprecatedMethod, String adminUser);
    
    boolean isProfileDeprecated(String orcid);

    boolean enableDeveloperTools(String orcid);

    boolean disableDeveloperTools(String orcid);

    boolean isProfileClaimed(String orcid);
    
    boolean isProfileClaimedByEmail(String email);

    MemberType getGroupType(String orcid);    

    boolean isDeactivated(String deactivated);

    boolean unreviewProfile(String orcid);

    boolean reviewProfile(String orcid);
    
    List<ApplicationSummary> getApplications(String orcid);
    
    void disableApplication(Long tokenId, String userOrcid);
    
    String getHash(String orcid);
    
    String retrivePublicDisplayName(String orcid);
    
    boolean claimProfileAndUpdatePreferences(String orcid, String email, AvailableLocales locale, Claim claim);
    
    boolean deactivateRecord(String orcid);
    
    void updateLastModifed(String orcid);

    void updateLocale(String orcid, AvailableLocales locale);

    void reactivate(String orcid, String primaryEmail, Reactivation reactivation);

    public void updatePassword(String orcid, String password);
    
    public void updateSecurityQuestion(String orcid, Integer questionId, String answer);
    
    public void updateLastLoginDetails(String orcid, String ipAddress);
    
    public AvailableLocales retrieveLocale(String orcid);      
    
    boolean lockProfile(String orcid, String lockReason, String description);

    boolean unlockProfile(String orcid);

    Date getLastLogin(String orcid);
    
    void disable2FA(String orcid);
    
    void enable2FA(String orcid);

    void update2FASecret(String orcid, String secret);
}