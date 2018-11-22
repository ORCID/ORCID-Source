package org.orcid.core.manager.v3.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.EmailManagerReadOnlyImpl;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerImpl extends EmailManagerReadOnlyImpl implements EmailManager {

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

    @Override
    @Transactional
    public void removeEmail(String orcid, String email) {
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
        return emailDao.verifyPrimaryEmail(orcid);
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
        return emailDao.isAutoDeprecateEnableForEmailUsingHash(encryptionManager.getEmailHash(email));        
    }

    @Override
    @Transactional
    public void updateEmails(HttpServletRequest request, String orcid, Emails emails) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {            
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                if (emails != null && !emails.getEmails().isEmpty()) {
                    for (Email email : emails.getEmails()) {
                        emailDao.updateEmail(orcid, email.getEmail().trim(), email.isCurrent(), email.getVisibility().name());
                    }
                }                
            }
        });        
    }

    @Override
    @Transactional
    public void addEmail(HttpServletRequest request, String orcid, Email email) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        String sourceId = sourceEntity.getSourceProfile() == null ? null : sourceEntity.getSourceProfile().getId();
        String clientSourceId = sourceEntity.getSourceClient() == null ? null : sourceEntity.getSourceClient().getId();        
        
        Email currentPrimaryEmail = findPrimaryEmail(orcid);
        
        // Create the new email
        emailDao.addEmail(orcid, email.getEmail(), encryptionManager.getEmailHash(email.getEmail()), email.getVisibility().name(), sourceId, clientSourceId);
        
        // if primary email changed send notification.
        if (email.isPrimary() && !StringUtils.equals(currentPrimaryEmail.getEmail(), email.getEmail())) {
            request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
            notificationManager.sendEmailAddressChangedNotification(orcid, email.getEmail(), currentPrimaryEmail.getEmail());
        }
                
        // send verifcation email for new address
        notificationManager.sendVerificationEmail(orcid, email.getEmail());
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
    @Transactional
    public void reactivatePrimaryEmail(String orcid, String email) {
        String hash = encryptionManager.getEmailHash(email);
        EmailEntity entity = emailDao.find(hash);
        if(!orcid.equals(entity.getProfile().getId())) {
            throw new IllegalArgumentException("Email with hash {}" + hash + " doesn't belong to " + orcid);
        }
        if(!PojoUtil.isEmpty(entity.getEmail()) && !email.equals(entity.getEmail())) {            
            throw new IllegalArgumentException("Email address with hash " + hash + " is already populated and doesn't match the given address " + email);            
        }
        entity.setEmail(email);
        entity.setPrimary(true);
        entity.setVerified(true);
        entity.setLastModified(new Date());
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
        String hash = encryptionManager.getEmailHash(email);
        EmailEntity entity = emailDao.find(hash);
        // If email doesn't exists, create it
        if(entity == null) {
            emailDao.addEmail(orcid, email, hash, visibility.name(), orcid, null);
            return true;
        } else {
            if(orcid.equals(entity.getProfile().getId())) {
                entity.setEmail(email);
                entity.setPrimary(false);
                entity.setVerified(false);
                entity.setVisibility(visibility.name());
                entity.setLastModified(new Date());
                emailDao.merge(entity);  
                emailDao.flush();
                if(!entity.getVerified()) {
                    return true;
                }
            } else {
                throw new IllegalArgumentException("Email " + email + " belongs to other record than " + orcid);
            }
        }
        
        return false;
    }
}
