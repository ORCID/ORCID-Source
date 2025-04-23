package org.orcid.core.common.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.manager.v3.impl.ProfileEmailDomainManagerImpl;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.emailDomain.EmailDomainValidator;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailDomainEntity.DomainCategory;

import com.google.common.net.InternetDomainName;
import org.orcid.pojo.EmailDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailDomainManagerImpl implements EmailDomainManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEmailDomainManagerImpl.class);

    public enum STATUS {CREATED, UPDATED};
    
    @Resource(name = "emailDomainDao")
    private EmailDomainDao emailDomainDao;

    @Resource(name = "emailDomainDaoReadOnly")
    private EmailDomainDao emailDomainDaoReadOnly;

    @Resource
    private SourceEntityUtils sourceEntityUtils;

    @Resource(name = "emailDomainCache")
    private Cache<String, List<EmailDomain>> emailDomainCache;

    private void validateEmailDomain(String emailDomain) {
        if (emailDomain == null || emailDomain.isBlank()) {
            throw new IllegalArgumentException("Email Domain must not be empty");
        }
        if(!InternetDomainName.isValid(emailDomain)) {
            throw new IllegalArgumentException("Email Domain '" + emailDomain + "' is invalid");
        }
    }
    
    @Override
    public EmailDomainEntity createEmailDomain(String emailDomain, DomainCategory category) {        
        validateEmailDomain(emailDomain);
        if (category == null) {
            throw new IllegalArgumentException("Category must not be empty");
        }
        return emailDomainDao.createEmailDomain(emailDomain, category);
    }

    @Override
    public boolean updateCategory(long id, DomainCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be empty");
        }
        return emailDomainDao.updateCategory(id, category);
    }

    private List<EmailDomain> getEmailDomainCache(String emailDomain) {
        if (emailDomainCache.containsKey(emailDomain)) {
            return emailDomainCache.get(emailDomain);
        }
        return null;
    }

    @Override
    public List<EmailDomain>  findByEmailDomain(String emailDomain) {
        if (emailDomain == null || emailDomain.isBlank()) {
            throw new IllegalArgumentException("Email Domain must not be empty");
        }

        List<EmailDomain> cachedEmailDomain = getEmailDomainCache(emailDomain);
        if (cachedEmailDomain != null) {
            return cachedEmailDomain;
        }

        List<EmailDomain> results = resolveEmailDomain(emailDomain);
        emailDomainCache.put(emailDomain, results);
        return results;
    }

    private List<EmailDomain> resolveEmailDomain(String emailDomain) {
        LOGGER.debug("Resolving email domain {}", emailDomain);
        // Fetch entries for the current email domain
        List<EmailDomainEntity> entities = emailDomainDaoReadOnly.findByEmailDomain(emailDomain);

        // If no results and domain contains a dot, strip the first subdomain and recurse
        if (entities.isEmpty() && emailDomain.contains(".")) {
            String strippedDomain = emailDomain.substring(emailDomain.indexOf(".") + 1);
            if(EmailDomainValidator.getInstance().isValidEmailDomain(strippedDomain)) {
                return resolveEmailDomain(strippedDomain); // Recursive call with stripped domain
            }
        }

        List<EmailDomain> results = new ArrayList<>();

        // convert to pojo to be more cache-friendly
        for (EmailDomainEntity entity : entities) {
            EmailDomain domain = new EmailDomain();
            domain.setEmailDomain(entity.getEmailDomain());
            domain.setCategory(entity.getCategory());
            domain.setRorId(entity.getRorId());
            results.add(domain);
        }

        // Return the domains (either found or empty if no more subdomains)
        return results;
    }

    @Override
    public List<EmailDomainEntity> findByCategory(DomainCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be empty");
        }
        return emailDomainDaoReadOnly.findByCategory(category);
    }

    @Override
    public STATUS createOrUpdateEmailDomain(String emailDomain, String rorId) {
        List<EmailDomainEntity>  existingEntities = emailDomainDaoReadOnly.findByEmailDomain(emailDomain);
        if(existingEntities != null && !existingEntities.isEmpty()) {
            if(existingEntities.size() == 1) {
                if(!rorId.equals(existingEntities.get(0).getRorId())) {
                    boolean updated = emailDomainDao.updateRorId(existingEntities.get(0).getId(), rorId);
                    if(updated)
                        return STATUS.UPDATED;
                }
            }
        } else {
            EmailDomainEntity newEntity = emailDomainDao.createEmailDomain(emailDomain, DomainCategory.PROFESSIONAL, rorId);
            if (newEntity != null) {
                return STATUS.CREATED;
            }
        }
        return null;
    }
}
