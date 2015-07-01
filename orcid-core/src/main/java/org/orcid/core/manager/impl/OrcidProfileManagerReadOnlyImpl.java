package org.orcid.core.manager.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidProfileManagerReadOnly;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

public class OrcidProfileManagerReadOnlyImpl implements OrcidProfileManagerReadOnly {

    @Resource
    protected ProfileDao profileDao;
    @Resource
    protected SourceManager sourceManager;
    @Resource
    protected Jpa2JaxbAdapter jpaJaxbAdapter;
    protected int claimWaitPeriodDays = 10;
    @Resource
    protected LocaleManager localeManager;
    @Resource
    protected EncryptionManager encryptionManager;
    @Resource
    protected OrcidProfileCacheManager orcidProfileCacheManager;
    @Resource
    protected JpaJaxbEntityAdapter adapter;
    protected static final Logger LOG = LoggerFactory.getLogger(OrcidProfileManagerImpl.class);

    /**
     * Retrieves the orcid fundings given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the funding list populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedFundings(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToFundingsOnly();
        }
        return profile;
    }

    /**
     * Retrieves the orcid works given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the works populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedOrcidWorks(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToWorksOnly();
        }
        return profile;
    }

    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfile(String orcid) {
        return retrieveOrcidProfile(orcid, LoadOptions.ALL);
    }

    @Override
    @Transactional(readOnly = true)
    public OrcidProfile retrieveOrcidProfile(String orcid, LoadOptions loadOptions) {
        if (LoadOptions.ALL.equals(loadOptions))
            return orcidProfileCacheManager.retrieve(orcid);
        return retrieveFreshOrcidProfile(orcid, loadOptions);
    }

    @Transactional
    public OrcidProfile retrieveFreshOrcidProfile(String orcid, LoadOptions loadOptions) {
        LOG.debug("About to obtain fresh profile: " + orcid);
        profileDao.flush();
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity != null) {
            OrcidProfile freshOrcidProfile = convertToOrcidProfile(profileEntity, loadOptions);
            return freshOrcidProfile;
        }
        return null;
    }

    @Override
    @Transactional
    public OrcidProfile retrieveClaimedOrcidProfile(String orcid) {
        OrcidProfile orcidProfile = retrieveOrcidProfile(orcid);
        if (orcidProfile != null) {
            if (Boolean.TRUE.equals(orcidProfile.getOrcidHistory().getClaimed().isValue()) || orcidProfile.isDeactivated() || isBeingAccessedByCreator(orcidProfile)
                    || haveSystemRole() || isOldEnough(orcidProfile)) {
                return orcidProfile;
            } else {
                if (orcidProfile.getOrcidDeprecated() != null && orcidProfile.getOrcidDeprecated().getPrimaryRecord() != null)
                    return createReservedForClaimOrcidProfile(orcid, orcidProfile.getOrcidDeprecated(), orcidProfile.getOrcidHistory().getLastModifiedDate());
                else
                    return createReservedForClaimOrcidProfile(orcid, orcidProfile.getOrcidHistory().getLastModifiedDate());
            }
        }
        return null;
    }

    private boolean isOldEnough(OrcidProfile orcidProfile) {
        return DateUtils.olderThan(orcidProfile.getOrcidHistory().getSubmissionDate().getValue().toGregorianCalendar().getTime(), claimWaitPeriodDays);
    }

    private boolean isBeingAccessedByCreator(OrcidProfile orcidProfile) {
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        Source source = orcidProfile.getOrcidHistory().getSource();
        if (NullUtils.noneNull(amenderOrcid, source)) {
            return amenderOrcid.equals(source.retrieveSourcePath());
        }
        return false;
    }

    private boolean haveSystemRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                return authorities.contains(new SimpleGrantedAuthority("ROLE_SYSTEM"));
            }
        }
        return false;
    }

    private OrcidProfile createReservedForClaimOrcidProfile(String orcid, LastModifiedDate lastModifiedDate) {
        return createReservedForClaimOrcidProfile(orcid, null, lastModifiedDate);
    }

    private OrcidProfile createReservedForClaimOrcidProfile(String orcid, OrcidDeprecated deprecatedInfo, LastModifiedDate lastModifiedDate) {
        OrcidProfile op = new OrcidProfile();
        if (jpaJaxbAdapter != null) {
            op.setOrcidIdentifier(new OrcidIdentifier(jpaJaxbAdapter.getOrcidIdBase(orcid)));
        } else {
            op.setOrcidIdentifier(orcid);
        }
        if (deprecatedInfo != null)
            op.setOrcidDeprecated(deprecatedInfo);

        OrcidHistory oh = new OrcidHistory();
        oh.setClaimed(new Claimed(false));
        oh.setLastModifiedDate(lastModifiedDate);
        op.setOrcidHistory(oh);
        GivenNames gn = new GivenNames();
        PersonalDetails pd = new PersonalDetails();
        gn.setContent(localeManager.resolveMessage("orcid.reserved_for_claim"));
        pd.setGivenNames(gn);
        OrcidBio ob = new OrcidBio();
        ob.setPersonalDetails(pd);
        op.setOrcidBio(ob);
        return op;
    }

    protected OrcidProfile convertToOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions) {
        LOG.debug("About to convert profile entity to orcid profile: " + profileEntity.getId());
        profileDao.refresh(profileEntity);
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, loadOptions);
        String verificationCode = profileEntity.getEncryptedVerificationCode();
        String securityAnswer = profileEntity.getEncryptedSecurityAnswer();
        orcidProfile.setVerificationCode(decrypt(verificationCode));
        orcidProfile.setSecurityQuestionAnswer(decrypt(securityAnswer));
        return orcidProfile;
    }

    public void setClaimWaitPeriodDays(int claimWaitPeriodDays) {
        this.claimWaitPeriodDays = claimWaitPeriodDays;
    }

    protected String decrypt(String encrypted) {
        if (StringUtils.isNotBlank(encrypted)) {
            return encryptionManager.decryptForInternalUse(encrypted);
        } else {
            return null;
        }
    }

    /**
     * Retrieves the orcid external identifiers given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedExternalIdentifiers(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToExternalIdentifiersOnly();
        }
        return profile;
    }

    /**
     * Retrieves the orcid bio given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedOrcidBio(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToBioOnly();
        }
        return profile;
    }

    /**
     * Retrieves the orcid affiliations given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the affiliations populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedAffiliations(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToAffiliationsOnly();
        }
        return profile;
    }

}