package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.EmailManagerReadOnlyImpl;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
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

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ProfileDao profileDao;
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @Resource(name = "encryptionManager")
    private EncryptionManager encryptionManager;   

    @Resource
    private SlackManager slackManager;
    
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
    public boolean verifyEmail(String email, String orcid) {
        boolean verified = emailDao.verifyEmail(email);

        if (verified) {
            profileLastModifiedAspect.updateLastModifiedDateAndIndexingStatus(orcid);
        }

        return verified;
    }

    @Override
    @Transactional
    public boolean verifyPrimaryEmail(String orcid) {
        try {
            String primaryEmail = emailDao.findPrimaryEmail(orcid).getEmail();
            return emailDao.verifyEmail(primaryEmail);
        } catch (javax.persistence.NoResultException nre) {
            String alternativePrimaryEmail = emailDao.findNewestVerifiedOrNewestEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);
            
            String message = String.format("User with orcid %s have no primary email, so, we are setting the newest verified email, or, the newest email in case non is verified as the primary one", orcid);
            LOGGER.error(message);
            
            slackManager.sendSystemAlert(message);
            throw nre;
        } catch (javax.persistence.NonUniqueResultException nure) {
            String alternativePrimaryEmail = emailDao.findNewestPrimaryEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);
            
            String message = String.format("User with orcid %s have more than one primary email, so, we are setting the latest modified primary as the primary one", orcid);
            LOGGER.error(message);
            
            slackManager.sendSystemAlert(message);    
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
    public boolean verifySetCurrentAndPrimary(String orcid, String email) {
        if (PojoUtil.isEmpty(orcid) || PojoUtil.isEmpty(email)) {
            throw new IllegalArgumentException("orcid or email param is empty or null");
        }

        return emailDao.updateVerifySetCurrentAndPrimary(orcid, email);
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

    @Override
    @Transactional
    public void addEmail(HttpServletRequest request, String orcid, Email email) {
        SourceEntity sourceEntity = sourceManager.retrieveActiveSourceEntity();
        String sourceId = sourceEntity.getSourceProfile() == null ? null : sourceEntity.getSourceProfile().getId();
        String clientSourceId = sourceEntity.getSourceClient() == null ? null : sourceEntity.getSourceClient().getId();        
        Email currentPrimaryEmail = findPrimaryEmail(orcid);
        Map<String, String> emailKeys = getEmailKeys(email.getEmail());
        
        // Create the new email
        emailDao.addEmail(orcid, emailKeys.get(FILTERED_EMAIL), emailKeys.get(HASH), email.getVisibility().name(), sourceId, clientSourceId);
        if (email.isPrimary()) {
            // if primary email changed send notification.
            if (!StringUtils.equals(currentPrimaryEmail.getEmail(), emailKeys.get(FILTERED_EMAIL))) {
                request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
                notificationManager.sendEmailAddressChangedNotification(orcid, emailKeys.get(FILTERED_EMAIL), currentPrimaryEmail.getEmail());
            }
        }
        notificationManager.sendVerificationEmail(orcid, emailKeys.get(FILTERED_EMAIL));
    }
    
    @Override
    public boolean hideAllEmails(String orcid) {
        return emailDao.hideAllEmails(orcid);
    }

    @Override
    public boolean updateVisibility(String orcid, String email, Visibility visibility) {
        return emailDao.updateVisibility(orcid, email, visibility.name());
    }
    
    @Override
    public void setPrimary(String orcid, String email, HttpServletRequest request) {
        Email currentPrimaryEmail = this.findPrimaryEmail(orcid);
        EmailEntity newPrimary = emailDao.findByEmail(email); 
        if(newPrimary != null && !currentPrimaryEmail.getEmail().equals(email)) {
            emailDao.updatePrimary(orcid, email);                 
            notificationManager.sendEmailAddressChangedNotification(orcid, email, currentPrimaryEmail.getEmail());
            
            if (!newPrimary.getVerified()) {
                notificationManager.sendVerificationEmail(orcid, email);
                request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
            }
        }        
    }
    
    @Override
    public void editEmail(String orcid, String original, String edited, HttpServletRequest request) {
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
        updatedEntity.setProfile(originalEntity.getProfile());

        if (originalEntity.getPrimary()) {
            // if primary email changed send notification.
            if (!StringUtils.equals(original, emailKeys.get(FILTERED_EMAIL))) {
                request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
                notificationManager.sendEmailAddressChangedNotification(orcid, emailKeys.get(FILTERED_EMAIL), original);
            }
        }
        
        emailDao.persist(updatedEntity);
        emailDao.remove(originalEntity.getId());
        notificationManager.sendVerificationEmail(orcid, emailKeys.get(FILTERED_EMAIL));
    }

    @Override
    @Transactional
    public void reactivatePrimaryEmail(String orcid, String email) {
        Map<String, String> emailKeys = getEmailKeys(email);       
        String hash = emailKeys.get(HASH);
        EmailEntity entity = emailDao.find(hash);
        if(!orcid.equals(entity.getProfile().getId())) {
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
            if(orcid.equals(entity.getProfile().getId())) {
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
