/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.EmailManagerReadOnlyImpl;
import org.orcid.jaxb.model.message.Email;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerImpl extends EmailManagerReadOnlyImpl implements EmailManager {

    @Resource
    private SourceManager sourceManager;
    
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
        return emailDao.moveEmailToOtherAccountAsNonPrimary(email, origin, destination);
    }
        
    @Override
    public boolean verifySetCurrentAndPrimary(String orcid, String email) {
        if(PojoUtil.isEmpty(orcid) || PojoUtil.isEmpty(email)) {
            throw new IllegalArgumentException("orcid or email param is empty or null");
        }
        
        return emailDao.verifySetCurrentAndPrimary(orcid, email);
    }

    /***
     * Indicates if the given email address could be auto deprecated given the
     * ORCID rules. See
     * https://trello.com/c/ouHyr0mp/3144-implement-new-auto-deprecate-workflow-
     * for-members-unclaimed-ids
     * 
     * @param email
     *            Email address
     * @return true if the email exists in a non claimed record and the
     *         client source of the record allows auto deprecating records
     */
    @Override
    public boolean isAutoDeprecateEnableForEmail(String email) {
        if(PojoUtil.isEmpty(email)) {
            return false;
        }
        return emailDao.isAutoDeprecateEnableForEmail(email);
    }

    @Override
    @Transactional
    public void updateEmails(String orcid, Collection<Email> emails) {
        int primaryCount = 0;
        for (Email email : emails) {
            emailDao.updateEmail(orcid, email.getValue(), email.isCurrent(), email.getVisibility());
            if (email.isPrimary()) {
                primaryCount++;
                emailDao.updatePrimary(orcid, email.getValue());
            }
        }
        if (primaryCount != 1) {
            throw new IllegalArgumentException("Wrong number of primary emails: " + primaryCount);
        }
    }

    @Override
    @Transactional
    public void addEmail(String orcid, Email email) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        String sourceId = null;
        String clientSourceId = null;
        if(sourceEntity.getSourceProfile() != null) {
            sourceId = sourceEntity.getSourceProfile().getId();
        }
        
        if(sourceEntity.getSourceClient() != null) {
            clientSourceId = sourceEntity.getSourceClient().getId();
        } 
        emailDao.addEmail(orcid, email.getValue(), email.getVisibility(), sourceId, clientSourceId);
    }	
}
