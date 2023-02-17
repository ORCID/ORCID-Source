package org.orcid.core.manager.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.read_only.impl.ProfileEntityManagerReadOnlyImpl;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Declan Newman (declan) Date: 10/02/2012
 */
public class ProfileEntityManagerImpl extends ProfileEntityManagerReadOnlyImpl implements ProfileEntityManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEntityManagerImpl.class);

    @Resource
    private EncryptionManager encryptionManager;

    @Resource(name = "emailManagerV3")
    private EmailManager emailManagerV3;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Resource
    private RecordNameManager recordNameManager;
    
    @Resource
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

    @Override
    public boolean orcidExists(String orcid) {
        return profileDao.orcidExists(orcid);
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        return profileDao.hasBeenGivenPermissionTo(giverOrcid, receiverOrcid);
    }

    @Override
    public boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId) {
        return profileDao.existsAndNotClaimedAndBelongsTo(messageOrcid, clientId);
    }

    @Override
    public String findByCreditName(String creditName) {
        Name name = recordNameManager.findByCreditName(creditName);
        if (name == null) {
            return null;
        }
        return name.getPath();
    }

    /**
     * Enable developer tools on the given record
     * 
     * @param orcid
     *            record id
     * @return true if the developer tools where enabled on the given record
     */
    @Override
    public boolean enableDeveloperTools(String orcid) {
        return profileDao.updateDeveloperTools(orcid, true);
    }

    /**
     * Disable developer tools in the given record
     * 
     * @param orcid
     *            record id
     * @return true if the developer tools where disabled on the given record
     */
    @Override
    public boolean disableDeveloperTools(String orcid) {
        return profileDao.updateDeveloperTools(orcid, false);
    }

    @Override
    public boolean isProfileClaimed(String orcid) {
        return profileDao.getClaimedStatus(orcid);
    }

    /**
     * Get the group type of a profile
     * 
     * @param orcid
     *            The profile to look for
     * @return the group type, null if it is not a client
     */
    @Override
    public MemberType getGroupType(String orcid) {
        String groupType = profileDao.getGroupType(orcid);
        return MemberType.valueOf(groupType);
    }

    /**
     * Updates the DB and the cached value in the request scope.
     * 
     */
    @Override
    public void updateLastModifed(String orcid) {
        profileLastModifiedAspect.updateLastModifiedDateAndIndexingStatus(orcid);
    }

    @Override
    public boolean isDeactivated(String orcid) {
        return profileDao.isDeactivated(orcid);
    }

    @Override
    public boolean reviewProfile(String orcid) {
        return profileDao.reviewProfile(orcid);
    }

    @Override
    public boolean unreviewProfile(String orcid) {
        return profileDao.unreviewProfile(orcid);
    }

    @Override
    public void disableApplication(Long tokenId, String userOrcid) {
        orcidOauth2TokenService.disableAccessToken(tokenId, userOrcid);
    }

    @Override
    public String getOrcidHash(String orcid) throws NoSuchAlgorithmException {
        if (PojoUtil.isEmpty(orcid)) {
            return null;
        }
        return encryptionManager.sha256Hash(orcid);
    }

    @Override
    public String retrivePublicDisplayName(String orcid) {
        String publicName = recordNameManagerReadOnly.fetchDisplayablePublicName(orcid);
        return PojoUtil.isEmpty(publicName) ? StringUtils.EMPTY : publicName;
    }

    @Override
    public void updateLocale(String orcid, Locale locale) {
        profileDao.updateLocale(orcid, locale.name());
    }

    @Override
    public boolean isProfileClaimedByEmail(String email) {
        Map<String, String> emailKeys = emailManagerV3.getEmailKeys(email);
        return profileDao.getClaimedStatusByEmailHash(emailKeys.get(EmailManager.HASH));
    }

    @Override
    public void updatePassword(String orcid, String password) {
        String encryptedPassword = encryptionManager.hashForInternalUse(password);
        profileDao.changeEncryptedPassword(orcid, encryptedPassword);
    }

    @Override
    public boolean isProfileDeprecated(String orcid) {
        return profileDao.isProfileDeprecated(orcid);
    }

    @Override
    public void updateLastLoginDetails(String orcid, String ipAddress) {
        profileDao.updateLastLoginDetails(orcid, ipAddress);
    }

    @Override
    public Locale retrieveLocale(String orcid) {
        String locale = profileDao.retrieveLocale(orcid);
        return Locale.valueOf(locale);
    }

    @Override
    public Date getLastLogin(String orcid) {
        return profileDao.getLastLogin(orcid);
    }    

    @Override
    public void update2FASecret(String orcid, String secret) {
        profileDao.update2FASecret(orcid, secret);
    }        
}