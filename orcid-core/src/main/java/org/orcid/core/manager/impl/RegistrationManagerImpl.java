package org.orcid.core.manager.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.VerifyRegistrationToken;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class RegistrationManagerImpl implements RegistrationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationManagerImpl.class);

    private EncryptionManager encryptionManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;

    @Resource
    private EmailDao emailDao;
    
    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private AdminManager adminManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private OrcidGenerationManager orcidGenerationManager;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManager;

    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;
    
    @Required
    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }   
    
    @Override
    public Long getCount() {
        return profileDao.getConfirmedProfileCount();
    }

    @Override
    public void createAffiliation(Registration registration, String orcid) {
        AffiliationForm affiliationForm = registration.getAffiliationForm();
        OrgDisambiguated orgDisambiguated = orgDisambiguatedManager.findInDB(Long.valueOf(affiliationForm.getOrgDisambiguatedId().getValue()));
        affiliationForm.setDisambiguatedAffiliationSourceId(Text.valueOf(orgDisambiguated.getSourceId()));
        affiliationForm.setDisambiguationSource(Text.valueOf(orgDisambiguated.getSourceType()));
        affiliationForm.setCity(Text.valueOf(orgDisambiguated.getCity()));
        affiliationForm.setCountry(Text.valueOf(orgDisambiguated.getCountry()));
        Affiliation affiliation = registration.getAffiliationForm().toAffiliation();
        affiliationsManager.createEmploymentAffiliation(orcid, (Employment) affiliation, false);
    }

    @Override
    public VerifyRegistrationToken parseEncyrptedParamsForVerification(String encryptedParams) {
        String decryptedParams = encryptionManager.decryptForExternalUse(encryptedParams);
        LOGGER.debug("Got verification params: {}", decryptedParams);
        return new VerifyRegistrationToken(decryptedParams);
    }

    @Override
    public String createMinimalRegistration(Registration registration, boolean usedCaptcha, Locale locale, String ip) {
        String emailAddress = registration.getEmail().getValue();
        
        try {
            String orcidId = transactionTemplate.execute(new TransactionCallback<String>() {
                public String doInTransaction(TransactionStatus status) {
                    boolean duplicatePrimaryEmail = false;
                    boolean duplicateAdditionalEmail = false;
                    int duplicateCount = 0;
                    String duplicateAdditionalAddress = null;
                    
                    if (emailManager.emailExists(emailAddress)) {
                        duplicatePrimaryEmail = true;
                        duplicateCount++;
                    }

                    for(Text emailAdditional : registration.getEmailsAdditional()) {
                        if(!PojoUtil.isEmpty(emailAdditional)){
                            String emailAddressAdditional = emailAdditional.getValue();
                            if (emailManager.emailExists(emailAddressAdditional)) {
                                duplicateAdditionalEmail = true;
                                duplicateCount++;
                                if(PojoUtil.isEmpty(duplicateAdditionalAddress)){
                                    duplicateAdditionalAddress = emailAddressAdditional;
                                } else {
                                    throw new InvalidRequestException("More than 2 duplicate emails");
                                }
                            } 
                        }
                    }
                        
                    if (duplicatePrimaryEmail && !duplicateAdditionalEmail) {
                        checkAutoDeprecateIsEnabledForEmail(emailAddress);
                        String unclaimedOrcid = getOrcidIdFromEmail(emailAddress);       
                        emailManager.removeUnclaimedEmail(unclaimedOrcid, emailAddress);
                        String newUserOrcid = createMinimalProfile(registration, usedCaptcha, locale, ip);
                        ProfileDeprecationRequest result = new ProfileDeprecationRequest();
                        adminManager.autoDeprecateProfile(result, unclaimedOrcid, newUserOrcid);
                        notificationManager.sendAutoDeprecateNotification(newUserOrcid, unclaimedOrcid);
                        profileEntityCacheManager.remove(unclaimedOrcid);
                        return newUserOrcid;
                       
                    } else if (!duplicatePrimaryEmail && duplicateAdditionalEmail && duplicateCount < 2) {
                        checkAutoDeprecateIsEnabledForEmail(duplicateAdditionalAddress);
                        String unclaimedOrcid = getOrcidIdFromEmail(duplicateAdditionalAddress);
                        emailManager.removeUnclaimedEmail(unclaimedOrcid, duplicateAdditionalAddress);
                        String newUserOrcid = createMinimalProfile(registration, usedCaptcha, locale, ip);
                        ProfileDeprecationRequest result = new ProfileDeprecationRequest();
                        adminManager.autoDeprecateProfile(result, unclaimedOrcid, newUserOrcid);
                        notificationManager.sendAutoDeprecateNotification(newUserOrcid, unclaimedOrcid);
                        profileEntityCacheManager.remove(unclaimedOrcid);
                        return newUserOrcid;

                    } else if (duplicatePrimaryEmail && duplicateAdditionalEmail && duplicateCount < 2) {
                        checkAutoDeprecateIsEnabledForEmail(emailAddress);
                        String unclaimedOrcid01 = getOrcidIdFromEmail(emailAddress);
                        emailManager.removeUnclaimedEmail(unclaimedOrcid01, emailAddress);
                        checkAutoDeprecateIsEnabledForEmail(duplicateAdditionalAddress);
                        String unclaimedOrcid02 = getOrcidIdFromEmail(duplicateAdditionalAddress);
                        emailManager.removeUnclaimedEmail(unclaimedOrcid02, emailAddress);
                        String newUserOrcid = createMinimalProfile(registration, usedCaptcha, locale, ip);
                        ProfileDeprecationRequest result01 = new ProfileDeprecationRequest();
                        adminManager.autoDeprecateProfile(result01, unclaimedOrcid01, newUserOrcid);
                        notificationManager.sendAutoDeprecateNotification(newUserOrcid, unclaimedOrcid01);
                        profileEntityCacheManager.remove(unclaimedOrcid01);
                        ProfileDeprecationRequest result02 = new ProfileDeprecationRequest();
                        adminManager.autoDeprecateProfile(result02, unclaimedOrcid02, newUserOrcid);
                        notificationManager.sendAutoDeprecateNotification(newUserOrcid, unclaimedOrcid02);
                        profileEntityCacheManager.remove(unclaimedOrcid02);
                        return newUserOrcid;

                    } else {
                        return createMinimalProfile(registration, usedCaptcha, locale, ip);
                    }
                    
                }
            });
            return orcidId;
        } catch (Exception e) {
            throw new InvalidRequestException("Unable to register user due: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * Creates a minimal record
     * 
     * @param orcidProfile
     *            The record to create
     * @return the new record
     * @throws NoSuchAlgorithmException 
     */
    @Transactional
    private String createMinimalProfile(Registration registration, boolean usedCaptcha, Locale locale, String ip) {
        Date now = new Date();
        String orcid = orcidGenerationManager.createNewOrcid();
        ProfileEntity newRecord = new ProfileEntity();
        newRecord.setId(orcid);
        
        try {
            newRecord.setHashedOrcid(encryptionManager.sha256Hash(orcid));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        
        newRecord.setOrcidType(OrcidType.USER.name());
        newRecord.setSubmissionDate(now);
        newRecord.setClaimed(true);
        newRecord.setEnableDeveloperTools(false);
        newRecord.setRecordLocked(false);
        newRecord.setReviewed(false);
        newRecord.setUsedRecaptchaOnRegistration(usedCaptcha);
        newRecord.setUserLastIp(ip);
        newRecord.setLastLogin(now);
        newRecord.setCreationMethod(PojoUtil.isEmpty(registration.getCreationType()) ? CreationMethod.DIRECT.value() : registration.getCreationType().getValue());
        newRecord.setLocale(locale == null ? AvailableLocales.EN.name() : AvailableLocales.fromValue(locale.toString()).name());
        // Visibility defaults
        newRecord.setActivitiesVisibilityDefault(registration.getActivitiesVisibilityDefault().getVisibility().name());

        // Encrypt the password
        newRecord.setEncryptedPassword(encryptionManager.hashForInternalUse(registration.getPassword().getValue()));        
       
        // Set authority
        OrcidGrantedAuthority authority = new OrcidGrantedAuthority();
        authority.setOrcid(orcid);
        authority.setAuthority(OrcidWebRole.ROLE_USER.getAuthority());
        Set<OrcidGrantedAuthority> authorities = new HashSet<OrcidGrantedAuthority>(1);
        authorities.add(authority);
        newRecord.setAuthorities(authorities);

        profileDao.persist(newRecord);
        profileDao.flush();
        
        // Set primary email
        EmailEntity primaryEmailEntity = new EmailEntity();
        
        Map<String, String> emailKeys = emailManager.getEmailKeys(registration.getEmail().getValue());
        String email = emailKeys.get(EmailManager.FILTERED_EMAIL);
        String hash = emailKeys.get(EmailManager.HASH);
        
        primaryEmailEntity.setEmail(email);
        primaryEmailEntity.setId(hash);
        primaryEmailEntity.setOrcid(orcid);
        primaryEmailEntity.setPrimary(true);
        primaryEmailEntity.setCurrent(true);
        primaryEmailEntity.setVerified(false);
        // Email is private by default
        primaryEmailEntity.setVisibility(Visibility.PRIVATE.name());
        primaryEmailEntity.setSourceId(orcid);        
        emailDao.persist(primaryEmailEntity);
        
        // Set additional emails
        for(Text emailAdditional : registration.getEmailsAdditional()) {
            if(!PojoUtil.isEmpty(emailAdditional)){
                Map<String, String> aEmailKeys = emailManager.getEmailKeys(emailAdditional.getValue());
                String aEmail = aEmailKeys.get(EmailManager.FILTERED_EMAIL);
                String aHash = aEmailKeys.get(EmailManager.HASH);
                
                EmailEntity emailAdditionalEntity = new EmailEntity();
                emailAdditionalEntity.setEmail(aEmail);
                emailAdditionalEntity.setId(aHash);
                emailAdditionalEntity.setOrcid(orcid);
                emailAdditionalEntity.setPrimary(false);
                emailAdditionalEntity.setCurrent(true);
                emailAdditionalEntity.setVerified(false);
                // Email is private by default
                emailAdditionalEntity.setVisibility(Visibility.PRIVATE.name());
                emailAdditionalEntity.setSourceId(orcid);
                emailDao.persist(emailAdditionalEntity);
            }
        }
        
        // Save the record name
        Name name = new Name();
        name.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC);
        if (!PojoUtil.isEmpty(registration.getFamilyNames())) {            
            name.setFamilyName(new FamilyName(registration.getFamilyNames().getValue()));
        }
        if (!PojoUtil.isEmpty(registration.getGivenNames())) {
            name.setGivenNames(new GivenNames(registration.getGivenNames().getValue()));
        }
        recordNameManager.createRecordName(orcid, name);

        // Create email frequency entity
        boolean sendQuarterlyTips = (registration.getSendOrcidNews() == null) ? false : registration.getSendOrcidNews().getValue();
        emailFrequencyManager.createOnRegister(orcid, SendEmailFrequency.WEEKLY, SendEmailFrequency.WEEKLY, SendEmailFrequency.WEEKLY, sendQuarterlyTips);
                        
        return newRecord.getId();
    }

    /**
     * Validates if the given email address could be auto deprecated
     * 
     * @param emailAddress
     *            The email we want to check
     */
    private void checkAutoDeprecateIsEnabledForEmail(String emailAddress) throws InvalidRequestException {
        // If the email doesn't exists, just return
        if (!emailManager.emailExists(emailAddress)) {
            return;
        }

        // Check the record is not claimed
        if (profileEntityManager.isProfileClaimedByEmail(emailAddress)) {
            throw new InvalidRequestException("Email " + emailAddress + " already exists and is claimed, so, it can't be used again");
        }

        // Check the auto deprecate is enabled for this email address
        if (!emailManager.isAutoDeprecateEnableForEmail(emailAddress)) {
            throw new InvalidRequestException("Autodeprecate is not enabled for " + emailAddress);
        }
    }

    /**
     * Returns the orcid id associated with an email address
     * 
     * @param emailAddress
     * @return the orcid id associated with the given email address
     */
    private String getOrcidIdFromEmail(String emailAddress) {
        Map<String, String> emailMap = emailManager.findOricdIdsByCommaSeparatedEmails(emailAddress);
        String unclaimedOrcid = emailMap == null ? null : emailMap.get(emailAddress);
        if (PojoUtil.isEmpty(unclaimedOrcid)) {
            throw new InvalidRequestException("Unable to find orcid id for " + emailAddress);
        }
        return unclaimedOrcid;
    }

}
