package org.orcid.core.manager.v3.impl;


import org.orcid.core.manager.v3.ProfileEmailDomainManager;
import org.orcid.core.manager.v3.read_only.impl.ProfileEmailDomainManagerReadOnlyImpl;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.dao.ProfileEmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public class ProfileEmailDomainManagerImpl extends ProfileEmailDomainManagerReadOnlyImpl implements ProfileEmailDomainManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEmailDomainManagerImpl.class);

    @Resource
    protected ProfileEmailDomainDao profileEmailDomainDao;

    @Resource
    protected EmailDomainDao emailDomainDao;

    @Resource(name = "emailDaoReadOnly")
    protected EmailDao emailDaoReadOnly;

    private static final String DEFAULT_DOMAIN_VISIBILITY = Visibility.PRIVATE.toString().toUpperCase();

    @Transactional
    public void updateEmailDomains(String orcid, org.orcid.pojo.ajaxForm.Emails newEmails) {
        List<ProfileEmailDomainEntity> existingEmailDomains = profileEmailDomainDao.findByOrcid(orcid);

        if (existingEmailDomains != null) {
            // VISIBILITY UPDATE FOR EXISTING DOMAINS
            for (org.orcid.pojo.ajaxForm.ProfileEmailDomain emailDomain : newEmails.getEmailDomains()) {
                for (ProfileEmailDomainEntity existingEmailDomain : existingEmailDomains) {
                    if (existingEmailDomain.getEmailDomain().equals(emailDomain.getValue())) {
                        if (!existingEmailDomain.getVisibility().equals(emailDomain.getVisibility())) {
                            profileEmailDomainDao.updateVisibility(orcid, emailDomain.getValue(), emailDomain.getVisibility());
                        }
                    }
                }
            }

            // REMOVE DOMAINS
            for (ProfileEmailDomainEntity existingEmailDomain : existingEmailDomains) {
                boolean deleteEmail = true;
                for (org.orcid.pojo.ajaxForm.ProfileEmailDomain emailDomain : newEmails.getEmailDomains()) {
                    if (existingEmailDomain.getEmailDomain().equals(emailDomain.getValue())) {
                        deleteEmail = false;
                        break;
                    }
                }
                if (deleteEmail) {
                    profileEmailDomainDao.removeEmailDomain(orcid, existingEmailDomain.getEmailDomain());
                }
            }
        }
    }

    public void processDomain(String orcid, String email) {
        String domain = email.split("@")[1];
        EmailDomainEntity domainInfo = emailDomainDao.findByEmailDomain(domain);
        String domainVisibility = DEFAULT_DOMAIN_VISIBILITY;
        // Check if email is professional
        if (domainInfo != null && domainInfo.getCategory().equals(EmailDomainEntity.DomainCategory.PROFESSIONAL)) {
            ProfileEmailDomainEntity existingDomain = profileEmailDomainDao.findByEmailDomain(orcid, domain);
            // ADD NEW DOMAIN IF ONE DOESN'T EXIST
            if (existingDomain == null) {
                // Verify the user doesn't have more emails with that domain
                List<EmailEntity> existingEmails = emailDaoReadOnly.findByOrcid(orcid, System.currentTimeMillis());
                if(existingEmails != null && existingEmails.size() > 1) {
                    for(EmailEntity emailEntity : existingEmails) {
                        //If it is not the same emails that is being verified and it is verified
                        if(!email.equals(emailEntity.getEmail()) && emailEntity.getVerified()) {
                            try {
                                String emailEntityDomain = (emailEntity.getEmail() == null) ? null : (email.split("@")[1]);
                                // If one of the existing emails have the same domain as the email being verified check the visibility and select the less restrictive
                                if(domain.equals(emailEntityDomain)){
                                    String entityVisibility = emailEntity.getVisibility();
                                    domainVisibility = getLessRestrictiveVisibility(domainVisibility, entityVisibility);
                                }
                            } catch (Exception e) {
                                LOGGER.warn("Could not get email domain from email entity " + emailEntity.getEmail(), e);
                            }
                        }
                    }
                }
                profileEmailDomainDao.addEmailDomain(orcid, domain, domainVisibility);
            }
        }
    }

    private String getLessRestrictiveVisibility(String a, String b) {
        String visibility = DEFAULT_DOMAIN_VISIBILITY;
        if(Visibility.PUBLIC.name().equals(a) || Visibility.PUBLIC.name().equals(b)) {
            visibility = Visibility.PUBLIC.name();
        } else if(a.equals(b)) {
            visibility = a;
        } else if(Visibility.PRIVATE.name().equals(a)) {
            visibility = b;
        } else if(Visibility.PRIVATE.name().equals(b)) {
            visibility = a;
        }
        return visibility;
    }
}
