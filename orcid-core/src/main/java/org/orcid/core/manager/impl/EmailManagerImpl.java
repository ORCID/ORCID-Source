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

import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.read_only.impl.EmailManagerReadOnlyImpl;
import org.orcid.jaxb.model.message.Email;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerImpl extends EmailManagerReadOnlyImpl implements EmailManager {

    @Resource
    private EmailDao emailDao;

    @Resource
    private JpaJaxbEmailAdapter jpaJaxbEmailAdapter; 
    
    @Override
    @Transactional
    public void addEmail(String orcid, Email email) {
        emailDao.addEmail(orcid, email.getValue(), email.getVisibility(), email.getSource(), email.getSourceClientId());
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
    
}
