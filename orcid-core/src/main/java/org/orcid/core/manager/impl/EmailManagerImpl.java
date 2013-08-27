/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.EmailDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerImpl implements EmailManager {

    @Resource
    private EmailDao emailDao;

    @Override
    public boolean emailExists(String email) {
        return emailDao.emailExists(email);
    }

    @Override
    @Transactional
    public void addEmail(String orcid, Email email) {
        emailDao.addEmail(orcid, email.getValue(), email.getVisibility(), email.getSource());
    }

    @Override
    @Transactional
    public void addEmail(String orcid, String email, Visibility visibility, String sourceId){
    	emailDao.addEmail(orcid, email, visibility, sourceId);
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

}
