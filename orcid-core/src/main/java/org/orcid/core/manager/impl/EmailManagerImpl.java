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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.version.impl.Api2_0_rc3_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerImpl implements EmailManager {

    @Resource
    private EmailDao emailDao;

    @Resource
    private JpaJaxbEmailAdapter jpaJaxbEmailAdapter; 
    
    @Override
    public boolean emailExists(String email) {
        return emailDao.emailExists(email);
    }

    @Override
    @Transactional
    public void addEmail(String orcid, Email email) {
        emailDao.addEmail(orcid, email.getValue(), email.getVisibility(), email.getSource(), email.getSourceClientId());
    }

    @Override
    @Transactional
    public void addEmail(String orcid, EmailEntity email){
        String sourceId = email.getSourceId();
        String clientSourceId = email.getClientSourceId();
    	emailDao.addEmail(orcid, email.getId(), email.getVisibility(), sourceId, clientSourceId, email.getVerified(), email.getCurrent());
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
    public String findOrcidIdByEmail(String email) {
        return emailDao.findOrcidIdByCaseInsenitiveEmail(email);
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public Map<String, String> findOricdIdsByCommaSeparatedEmails(String csvEmail) {
        Map<String, String> emailIds = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
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
        emailDao.addSourceToEmail(sourceId, email);
    }
    
    @Override
    public boolean verifyEmail(String email) {
        return emailDao.verifyEmail(email);
    }
    
    @Override
    public boolean isPrimaryEmailVerified(String orcid) {
        return emailDao.isPrimaryEmailVerified(orcid);
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
    @Cacheable(value = "emails", key = "#orcid.concat('-').concat(#lastModified)")
    public Emails getEmails(String orcid, long lastModified) {
        return getEmails(orcid, null);
    }
    
    @Override
    @Cacheable(value = "public-emails", key = "#orcid.concat('-').concat(#lastModified)")
    public Emails getPublicEmails(String orcid, long lastModified) {
        return getEmails(orcid, Visibility.PUBLIC);
    }
    
    private Emails getEmails(String orcid, Visibility visibility) {
        List<EmailEntity> entities = new ArrayList<EmailEntity>();
        if(visibility == null) {
            entities = emailDao.findByOrcid(orcid);
        } else {
            entities = emailDao.findByOrcid(orcid, Visibility.PUBLIC);
        }
        List<org.orcid.jaxb.model.record_rc3.Email> emailList = jpaJaxbEmailAdapter.toEmailList(entities);
        Emails emails = new Emails();
        emails.setEmails(emailList);
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(emails);
        return emails;
    }
    
    @Override
    public boolean haveAnyEmailVerified(String orcid) {
        List<EmailEntity> entities = emailDao.findByOrcid(orcid);
        if(entities != null) {
            for(EmailEntity email : entities) {
                if(email.getVerified()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public org.orcid.pojo.ajaxForm.Emails getEmailsAsForm(String orcid) {
        List<EmailEntity> entities = emailDao.findByOrcid(orcid);
        org.orcid.pojo.ajaxForm.Emails form = new org.orcid.pojo.ajaxForm.Emails();
        
        if(entities != null) {
            form.setEmails(new ArrayList<org.orcid.pojo.Email>());
            for(EmailEntity entity : entities) {
                org.orcid.pojo.Email email = new org.orcid.pojo.Email();
                email.setCurrent(entity.getCurrent());
                email.setPrimary(entity.getPrimary());
                email.setSource(entity.getSourceId());
                email.setSourceClientId(entity.getClientSourceId());
                email.setValue(entity.getId());
                email.setVerified(entity.getVerified());
                email.setVisibility(entity.getVisibility());     
                form.getEmails().add(email);
            }
        }
                
        return form;
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
}
