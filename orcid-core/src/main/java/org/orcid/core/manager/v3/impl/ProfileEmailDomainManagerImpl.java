package org.orcid.core.manager.v3.impl;


import org.orcid.core.manager.v3.ProfileEmailDomainManager;
import org.orcid.core.manager.v3.read_only.impl.ProfileEmailDomainManagerReadOnlyImpl;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.dao.ProfileEmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public class ProfileEmailDomainManagerImpl extends ProfileEmailDomainManagerReadOnlyImpl implements ProfileEmailDomainManager {
    @Resource
    protected ProfileEmailDomainDao profileEmailDomainDao;

    @Resource
    protected EmailDomainDao emailDomainDao;

    @Resource
    protected EmailDao emailDao;

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
        // Check if email is professional
        if (domainInfo != null && domainInfo.getCategory().equals(EmailDomainEntity.DomainCategory.PROFESSIONAL)) {
            ProfileEmailDomainEntity existingDomain = profileEmailDomainDao.findByEmailDomain(orcid, domain);
            // ADD NEW DOMAIN IF ONE DOESN'T EXIST
            if (existingDomain == null) {
                profileEmailDomainDao.addEmailDomain(orcid, domain, DEFAULT_DOMAIN_VISIBILITY);
            }
        }
    }
}
