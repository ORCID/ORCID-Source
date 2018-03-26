package org.orcid.core.manager.impl;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidProfileManagerReadOnly;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.security.visibility.aop.VisibilityControl;
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
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class OrcidProfileManagerReadOnlyImpl implements OrcidProfileManagerReadOnly {

    @Resource
    private ProfileEntityManager profileEntityManager;
    @Resource
    private ProfileDao profileDao;
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
    protected TransactionTemplate transactionTemplate;
    
    protected static final Logger LOG = LoggerFactory.getLogger(OrcidProfileManagerReadOnlyImpl.class);

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public void setOrcidProfileCacheManager(OrcidProfileCacheManager orcidProfileCacheManager) {
        this.orcidProfileCacheManager = orcidProfileCacheManager;
    }

    public void setClaimWaitPeriodDays(int claimWaitPeriodDays) {
        this.claimWaitPeriodDays = claimWaitPeriodDays;
    }

    @Required
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Retrieves the orcid fundings given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the funding list populated
     */
    @Override
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
    public OrcidProfile retrieveClaimedOrcidWorks(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToWorksOnly();
        }
        return profile;
    }

    @Override
    public OrcidProfile retrieveOrcidProfile(String orcid) {
        return retrieveOrcidProfile(orcid, LoadOptions.ALL);
    }

    @Override
    public OrcidProfile retrieveOrcidProfile(String orcid, LoadOptions loadOptions) {
        if (LoadOptions.ALL.equals(loadOptions))
            return orcidProfileCacheManager.retrieve(orcid);
        if (LoadOptions.BIO_AND_INTERNAL_ONLY.equals(loadOptions))
            return orcidProfileCacheManager.retrieveProfileBioAndInternal(orcid);
        return retrieveFreshOrcidProfile(orcid, loadOptions);
    }

    @Override
    public OrcidProfile retrieveFreshOrcidProfile(final String orcid, final LoadOptions loadOptions) {
        return transactionTemplate.execute(new TransactionCallback<OrcidProfile>() {
            public OrcidProfile doInTransaction(TransactionStatus status) {
                return doRetrieveFreshOrcidProfileInTransaction(orcid, loadOptions);
            }
        });
    }

    private OrcidProfile doRetrieveFreshOrcidProfileInTransaction(String orcid, LoadOptions loadOptions) {
        LOG.debug("About to obtain fresh profile: " + orcid);
        profileDao.flushWithoutTransactional();
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity != null) {
            OrcidProfile freshOrcidProfile = convertToOrcidProfile(profileEntity, loadOptions);
            return freshOrcidProfile;
        }
        return null;
    }
    
    @Override
    public OrcidProfile retrieveClaimedOrcidProfile(String orcid){
        return retrieveClaimedOrcidProfile(orcid, LoadOptions.ALL);
    }

    @Override
    public OrcidProfile retrieveClaimedOrcidProfile(String orcid, LoadOptions loadOptions) {
        OrcidProfile orcidProfile = retrieveOrcidProfile(orcid, loadOptions);
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

    /**
     * Retrieves the orcid external identifiers given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    @Override
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
    public OrcidProfile retrieveClaimedAffiliations(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToAffiliationsOnly();
        }
        return profile;
    }

    @Override
    @VisibilityControl(visibilities = Visibility.PUBLIC)
    public OrcidProfile retrievePublicOrcidProfile(String orcid) {
        return retrievePublicOrcidProfile(orcid, LoadOptions.ALL);
    }
    
    @Override
    @VisibilityControl(visibilities = Visibility.PUBLIC)
    public OrcidProfile retrievePublicOrcidProfile(String orcid, LoadOptions loadOptions) {
        return retrieveClaimedOrcidProfile(orcid, loadOptions);
    }

    @Override
    public Date retrieveLastModifiedDate(String orcid) {
        return profileEntityManager.getLastModifiedDate(orcid);
    }

    @Deprecated
    protected boolean isOldEnough(OrcidProfile orcidProfile) {
        return DateUtils.olderThan(orcidProfile.getOrcidHistory().getSubmissionDate().getValue().toGregorianCalendar().getTime(), claimWaitPeriodDays);
    }

    protected boolean isBeingAccessedByCreator(OrcidProfile orcidProfile) {
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        Source source = orcidProfile.getOrcidHistory().getSource();
        if (NullUtils.noneNull(amenderOrcid, source)) {
            return amenderOrcid.equals(source.retrieveSourcePath());
        }
        return false;
    }

    protected boolean haveSystemRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                return authorities.contains(new SimpleGrantedAuthority("ROLE_SYSTEM"));
            }
        }
        return false;
    }

    protected OrcidProfile createReservedForClaimOrcidProfile(String orcid, LastModifiedDate lastModifiedDate) {
        return createReservedForClaimOrcidProfile(orcid, null, lastModifiedDate);
    }

    protected OrcidProfile createReservedForClaimOrcidProfile(String orcid, OrcidDeprecated deprecatedInfo, LastModifiedDate lastModifiedDate) {
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
        gn.setVisibility(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility());
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

    protected String decrypt(String encrypted) {
        if (StringUtils.isNotBlank(encrypted)) {
            return encryptionManager.decryptForInternalUse(encrypted);
        } else {
            return null;
        }
    }

}