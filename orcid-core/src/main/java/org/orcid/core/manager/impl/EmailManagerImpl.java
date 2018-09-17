package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.EmailManagerReadOnlyImpl;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerImpl extends EmailManagerReadOnlyImpl implements EmailManager {

    @Resource
    private SourceManager sourceManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private NotificationManager notificationManager;

    @Override
    @Transactional
    public void removeEmail(String orcid, String email) {
        emailDao.removeEmail(orcid, email);
    }

    @Override
    @Transactional
    public void removeEmail(String orcid, String email, boolean removeIfPrimary) {
        emailDao.removeEmail(orcid, email, removeIfPrimary);
    }

    @Override
    public void addSourceToEmail(String email, String sourceId) {
        emailDao.addSourceToEmail(sourceId, email);
    }

    @Override
    public boolean verifyEmail(String email) {
        return emailDao.verifyEmail(email);
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
        try {
            String emailHash = encryptionManager.sha256Hash(email.trim().toLowerCase());
            return emailDao.isAutoDeprecateEnableForEmailUsingHash(emailHash);    
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hideAllEmails(String orcid) {
        return emailDao.hideAllEmails(orcid);
    }
}
