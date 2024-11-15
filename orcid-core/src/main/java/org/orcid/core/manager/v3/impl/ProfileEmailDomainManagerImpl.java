package org.orcid.core.manager.v3.impl;


import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileEmailDomainManager;
import org.orcid.core.manager.v3.read_only.impl.ProfileEmailDomainManagerReadOnlyImpl;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Transactional
    public void updateEmailDomains(String orcid, org.orcid.pojo.ajaxForm.Emails newEmails) {
        if (orcid == null || orcid.isBlank()) {
            throw new IllegalArgumentException("ORCID must not be empty");
        }
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
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (orcid == null || orcid.isBlank()) {
            throw new IllegalArgumentException("ORCID must not be empty");
        }

        String domain = email.split("@")[1];
        EmailDomainEntity domainInfo = emailDomainDao.findByEmailDomain(domain);
        // Check if email is professional
        if (domainInfo != null && domainInfo.getCategory().equals(EmailDomainEntity.DomainCategory.PROFESSIONAL)) {
            ProfileEmailDomainEntity existingDomain = profileEmailDomainDao.findByEmailDomain(orcid, domain);
            // ADD NEW DOMAIN IF ONE DOESN'T EXIST
            if (existingDomain == null) {
                // Verify the user doesn't have more emails with that domain
                ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
                String domainVisibility = profile.getActivitiesVisibilityDefault();
                profileEmailDomainDao.addEmailDomain(orcid, domain, domainVisibility);
            }
        }
    }
}
