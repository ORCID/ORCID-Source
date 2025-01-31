package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ProfileEmailDomainManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.EmailManagerReadOnlyImpl;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerImpl extends EmailManagerReadOnlyImpl implements EmailManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailManagerImpl.class);
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource(name = "profileEmailDomainManager")
    private ProfileEmailDomainManager profileEmailDomainManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ProfileDao profileDao;
    
    @Resource(name = "encryptionManager")
    private EncryptionManager encryptionManager;
    
    @Override
    @Transactional
    public void removeEmail(String orcid, String email) {
        if (isPrimaryEmail(orcid, email)) {
            throw new IllegalArgumentException("Can't mark primary email as deleted");
        }
        
        if (isUsersOnlyEmail(orcid, email)) {
            throw new IllegalArgumentException("Can't mark user's only email as deleted");
        }
        emailDao.removeEmail(orcid, email);

    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean verifyEmail(String orcid, String email) {
        boolean result = emailDao.verifyEmail(email);
        if (result && Features.EMAIL_DOMAINS.isActive()) {
            profileEmailDomainManager.processDomain(orcid, email);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean verifyPrimaryEmail(String orcid) {
        try {
            String primaryEmail = emailDao.findPrimaryEmail(orcid).getEmail();

            boolean result = emailDao.verifyEmail(primaryEmail);
            if (result && Features.EMAIL_DOMAINS.isActive()) {
                profileEmailDomainManager.processDomain(orcid, primaryEmail);
            }
            return result;
        } catch (javax.persistence.NoResultException nre) {
            String alternativePrimaryEmail = emailDao.findNewestVerifiedOrNewestEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);
            String message = String.format("User with orcid %s have no primary email, so, we are setting the newest verified email, or, the newest email in case non is verified as the primary one", orcid);
            LOGGER.error(message);            
            throw nre;
        } catch (javax.persistence.NonUniqueResultException nure) {
            String alternativePrimaryEmail = emailDao.findNewestPrimaryEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);            
            String message = String.format("User with orcid %s have more than one primary email, so, we are setting the latest modified primary as the primary one", orcid);
            LOGGER.error(message);                         
            throw nure;
        }               
    }

    @Override
    @Transactional
    public boolean moveEmailToOtherAccount(String email, String origin, String destination) {
        boolean moved = emailDao.moveEmailToOtherAccountAsNonPrimary(email, origin, destination);
        if (moved) {
            profileDao.updateLastModifiedDateAndIndexingStatusWithoutResult(destination, new Date(), IndexingStatus.PENDING);
        }
        return moved;
    }

    @Override
    @Transactional
    public boolean verifySetCurrentAndPrimary(String orcid, String email) {
        if (PojoUtil.isEmpty(orcid) || PojoUtil.isEmpty(email)) {
            throw new IllegalArgumentException("orcid or email param is empty or null");
        }

        boolean result = emailDao.updateVerifySetCurrentAndPrimary(orcid, email);
        if (result && Features.EMAIL_DOMAINS.isActive()) {
            profileEmailDomainManager.processDomain(orcid, email);
        }
        return result;
    }

    /***
     * Indicates if the given email address could be auto deprecated given the
     * ORCID rules. See
     * https://trello.com/c/ouHyr0mp/3144-implement-new-auto-deprecate-workflow-
     * for-members-unclaimed-ids
     * 
     * @param email
     *            Email address
     * @return true if the email exists in a non claimed record and the client
     *         source of the record allows auto deprecating records
     */
    @Override
    public boolean isAutoDeprecateEnableForEmail(String email) {
        if (PojoUtil.isEmpty(email)) {
            return false;
        }
        Map<String, String> emailKeys = getEmailKeys(email);
        return emailDao.isAutoDeprecateEnableForEmailUsingHash(emailKeys.get(HASH));        
    }
    
    /**
     * TODO: Returns true when the primary email is verified, this because the
     * email sender is on the orcid-web project until we finish migrating the
     * jersey libs, so, the calling function should know that the primary email
     * have changed; for now it returns the new and old primary email
     */    
    @Override
    @Transactional
    public Map<String, String> addEmail(String orcid, Email email) {        
        Map<String, String> keys = new HashMap<String, String>();
        SourceEntity sourceEntity = sourceManager.retrieveActiveSourceEntity();
        String sourceId = sourceEntity.getSourceProfile() == null ? null : sourceEntity.getSourceProfile().getId();
        String clientSourceId = sourceEntity.getSourceClient() == null ? null : sourceEntity.getSourceClient().getId();        
        Map<String, String> emailKeys = getEmailKeys(email.getEmail());
        
        Email currentPrimaryEmail = null;
        try {
            currentPrimaryEmail = findPrimaryEmail(orcid);
        } catch(Exception e) {
            LOGGER.error(String.format("User with orcid %s doesnt have a primary email address", orcid));
        }
        
        // Create the new email
        emailDao.addEmail(orcid, emailKeys.get(FILTERED_EMAIL), emailKeys.get(HASH), email.getVisibility().name(), sourceId, clientSourceId);
        if (email.isPrimary()) {
            // if primary email changed send notification.
            if (!StringUtils.equals(currentPrimaryEmail.getEmail(), emailKeys.get(FILTERED_EMAIL))) {                
                keys.put("new", emailKeys.get(FILTERED_EMAIL));
                keys.put("old", currentPrimaryEmail.getEmail());                
            }
        }
        return keys;
    }
    
    @Override
    public boolean hideAllEmails(String orcid) {
        return emailDao.hideAllEmails(orcid);
    }

    @Override
    public boolean updateVisibility(String orcid, String email, Visibility visibility) {
        return emailDao.updateVisibility(orcid, email, visibility.name());
    }
    
    /**
     * TODO: Returns a map with the new primary email and the old primary email
     * when the primary email is updated, this because the email sender is on
     * the orcid-web project until we finish migrating the jersey libs, so, the
     * calling function should know that the primary email have changed
     */
    @Override
    public Map<String, String> setPrimary(String orcid, String email, HttpServletRequest request) {
        Map<String, String> keys = new HashMap<String, String>();
        Email currentPrimaryEmail = this.findPrimaryEmail(orcid);
        EmailEntity newPrimary = emailDao.findByEmail(email); 
        if(newPrimary != null && !currentPrimaryEmail.getEmail().equals(email)) {
            emailDao.updatePrimary(orcid, email);
            keys.put("new", email);
            keys.put("old", currentPrimaryEmail.getEmail());
            
            if (!newPrimary.getVerified()) {
                keys.put("sendVerification", "true");                
            }
        }
        return keys;
    }
    
    /**
     * TODO: Returns true when the primary email is verified, this because the
     * email sender is on the orcid-web project until we finish migrating the
     * jersey libs, so, the calling function should know that the primary email
     * have changed
     */    
    @Override
    public Map<String, String> editEmail(String orcid, String original, String edited, HttpServletRequest request) {
        Map<String, String> keys = new HashMap<String, String>();
        EmailEntity originalEntity = emailDao.findByEmail(original); 
        Map<String, String> emailKeys = getEmailKeys(edited);        
        EmailEntity updatedEntity = new EmailEntity();
        updatedEntity.setSourceId(orcid);
        updatedEntity.setEmail(emailKeys.get(FILTERED_EMAIL));
        updatedEntity.setId(emailKeys.get(HASH));
        updatedEntity.setVerified(Boolean.FALSE);
        updatedEntity.setCurrent(originalEntity.getCurrent());
        updatedEntity.setVisibility(originalEntity.getVisibility());
        updatedEntity.setPrimary(originalEntity.getPrimary());
        updatedEntity.setOrcid(originalEntity.getOrcid());

        if (originalEntity.getPrimary()) {
            // if primary email changed send notification.
            if (!StringUtils.equals(original, emailKeys.get(FILTERED_EMAIL))) {
                keys.put("new", emailKeys.get(FILTERED_EMAIL));
                keys.put("old", original);                
            }
        }
        
        emailDao.persist(updatedEntity);
        emailDao.remove(originalEntity.getId());
        keys.put("verifyAddress", emailKeys.get(FILTERED_EMAIL));
        return keys;
    }

    @Override
    @Transactional
    public void reactivatePrimaryEmail(String orcid, String email) {
        Map<String, String> emailKeys = getEmailKeys(email);       
        String hash = emailKeys.get(HASH);
        EmailEntity entity = emailDao.find(hash);
        if(!orcid.equals(entity.getOrcid())) {
            throw new IllegalArgumentException("Email with hash {}" + hash + " doesn't belong to " + orcid);
        }
        if(!PojoUtil.isEmpty(entity.getEmail()) && !email.equalsIgnoreCase(entity.getEmail())) {            
            throw new IllegalArgumentException("Email address with hash " + hash + " is already populated and doesn't match the given address " + email);            
        }
        // Update the email just in case the email is empty
        if(PojoUtil.isEmpty(entity.getEmail())) {
            entity.setEmail(emailKeys.get(FILTERED_EMAIL));
        }
        entity.setPrimary(true);
        entity.setVerified(true);
        if (entity.getDateVerified() == null) {
            entity.setDateVerified(new Date());
        }
        emailDao.merge(entity);
        emailDao.flush();
    }

    @Override
    public Integer clearEmailsAfterReactivation(String orcid) {
        if(PojoUtil.isEmpty(orcid)) {
           return 0; 
        }
        return emailDao.clearEmailsAfterReactivation(orcid);
    }

    @Override
    public boolean reactivateOrCreate(String orcid, String email, Visibility visibility) {
        Map<String, String> emailKeys = getEmailKeys(email);
        EmailEntity entity = emailDao.find(emailKeys.get(HASH));
        // If email doesn't exists, create it
        if(entity == null) {
            emailDao.addEmail(orcid, emailKeys.get(FILTERED_EMAIL), emailKeys.get(HASH), visibility.name(), orcid, null);
            return true;
        } else {
            if(orcid.equals(entity.getOrcid())) {
                entity.setEmail(emailKeys.get(FILTERED_EMAIL));
                entity.setPrimary(false);
                entity.setVerified(false);
                entity.setVisibility(visibility.name());
                emailDao.merge(entity);  
                emailDao.flush();
                if(!entity.getVerified()) {
                    return true;
                }
            } else {
                throw new IllegalArgumentException("Email " + emailKeys.get(FILTERED_EMAIL) + " belongs to other record than " + orcid);
            }
        }
        
        return false;
    }

    @Override
    public void removeUnclaimedEmail(String orcid, String emailAddress) {
        ProfileEntity entity = profileDao.find(orcid);
        if (entity.getClaimed() != null && entity.getClaimed()) {
            throw new IllegalArgumentException("Profile is claimed");
        }
        emailDao.removeEmail(orcid, emailAddress);
    }      
}
