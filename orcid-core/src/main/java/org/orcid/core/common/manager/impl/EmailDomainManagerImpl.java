package org.orcid.core.common.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.emailDomain.EmailDomainValidator;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailDomainEntity.DomainCategory;

import com.google.common.net.InternetDomainName;

public class EmailDomainManagerImpl implements EmailDomainManager {

    public enum STATUS {CREATED, UPDATED};
    
    @Resource(name = "emailDomainDao")
    private EmailDomainDao emailDomainDao;

    @Resource(name = "emailDomainDaoReadOnly")
    private EmailDomainDao emailDomainDaoReadOnly;

    @Resource
    private SourceEntityUtils sourceEntityUtils;

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

    @Override
    public List<EmailDomainEntity>  findByEmailDomain(String emailDomain) {
        if (emailDomain == null || emailDomain.isBlank()) {
            throw new IllegalArgumentException("Email Domain must not be empty");
        }

        // Fetch entries for the current email domain
        List<EmailDomainEntity> results = emailDomainDaoReadOnly.findByEmailDomain(emailDomain);

        // If no results and domain contains a dot, strip the first subdomain and recurse
        if (results.isEmpty() && emailDomain.contains(".")) {
            String strippedDomain = emailDomain.substring(emailDomain.indexOf(".") + 1);
            if(EmailDomainValidator.getInstance().isValidEmailDomain(strippedDomain)) {
                return findByEmailDomain(strippedDomain); // Recursive call with stripped domain
            }
        }

        // Return the results (either found or empty if no more subdomains)
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

    @Override
    public void processProfessionalEmailsForV2API(org.orcid.jaxb.model.record_v2.Emails emails) {
        if(emails == null || emails.getEmails() == null) {
            return;
        }
        for (org.orcid.jaxb.model.record_v2.Email email : emails.getEmails()) {
            if (email.isVerified()) {
                String domain = email.getEmail().split("@")[1];
                List<EmailDomainEntity> domainsInfo = findByEmailDomain(domain);
                String category = EmailDomainEntity.DomainCategory.UNDEFINED.name();
                // Set appropriate source name and source id for professional
                // emails
                if (domainsInfo != null) {
                    for (EmailDomainEntity domainInfo : domainsInfo) {
                        category = domainInfo.getCategory().name();
                        if (StringUtils.equalsIgnoreCase(category, EmailDomainEntity.DomainCategory.PROFESSIONAL.name())) {
                            break;
                        }
                    }
                    if (StringUtils.equalsIgnoreCase(category, EmailDomainEntity.DomainCategory.PROFESSIONAL.name())) {
                        if(email.getSource() == null) {
                            email.setSource(new org.orcid.jaxb.model.common_v2.Source());
                        }
                        email.setSource(sourceEntityUtils.convertEmailSourceToOrcidValidator(email.getSource()));
                    }
                }
            }
        }
    }

    @Override
    public void processProfessionalEmailsForV3API(org.orcid.jaxb.model.v3.release.record.Emails emails) {
        if(emails == null || emails.getEmails() == null) {
            return;
        }
        for (org.orcid.jaxb.model.v3.release.record.Email email : emails.getEmails()) {
            if (email.isVerified()) {
                String domain = email.getEmail().split("@")[1];
                List<EmailDomainEntity> domainsInfo = findByEmailDomain(domain);
                String category = EmailDomainEntity.DomainCategory.UNDEFINED.name();
                // Set appropriate source name and source id for professional
                // emails
                if (domainsInfo != null) {
                    for (EmailDomainEntity domainInfo : domainsInfo) {
                        category = domainInfo.getCategory().name();
                        if (StringUtils.equalsIgnoreCase(category, EmailDomainEntity.DomainCategory.PROFESSIONAL.name())) {
                            break;
                        }
                    }
                    if (StringUtils.equalsIgnoreCase(category, EmailDomainEntity.DomainCategory.PROFESSIONAL.name())) {
                        if(email.getSource() == null) {
                            email.setSource(new org.orcid.jaxb.model.v3.release.common.Source());
                        }
                        email.setSource(sourceEntityUtils.convertEmailSourceToOrcidValidator(email.getSource()));
                    }
                }
            }
        }
    }

    // TODO: processProfessionalEmailsForV2API and processProfessionalEmailsForV3API can be merged if we make
    //  org.orcid.jaxb.model.record_v2.Emails and org.orcid.jaxb.model.v3.release.record.Emails implement from an
    //  interface that we can call EmailDomainsHolder
}
