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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.EmailManager;
import org.orcid.jaxb.model.message.Email;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
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
    public void addEmail(String orcid, EmailEntity email){
    	emailDao.addEmail(orcid, email.getId(), email.getVisibility(), email.getSource() == null ? null : email.getSource().getId(), email.getVerified(), email.getCurrent());
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
    public Map<String, String> findIdByEmail(String csvEmail) {
        Map<String, String> emailIds = new TreeMap<String, String>();
        List<String> emailList = new ArrayList<String>();
        String [] emails = csvEmail.split(",");
        for(String email : emails) {
            if(StringUtils.isNotBlank(email.trim()))
                emailList.add(email.trim());
        }
        List ids = emailDao.findIdByCaseInsensitiveEmail(emailList);
        for (Iterator it = ids.iterator(); it.hasNext(); ) {
            Object[] orcidEmail = (Object[]) it.next();
            String orcid = (String) orcidEmail[0];
            String email = (String) orcidEmail[1];
            emailIds.put(email, orcid);
        }
        return emailIds;
    }
    
    @Override
    public void addSourceToEmail(String email, String sourceId) {
        emailDao.addSourceToEmail(email, sourceId);
    }
}
