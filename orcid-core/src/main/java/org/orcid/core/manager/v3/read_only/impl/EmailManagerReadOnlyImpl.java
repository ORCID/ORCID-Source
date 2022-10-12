package org.orcid.core.manager.v3.read_only.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.v3.JpaJaxbEmailAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.pojo.EmailFrequencyOptions;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements EmailManagerReadOnly {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailManagerReadOnlyImpl.class);
    
    @Resource(name = "jpaJaxbEmailAdapterV3")
    protected JpaJaxbEmailAdapter jpaJaxbEmailAdapter;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private LocaleManager localeManager;    
    
    protected EmailDao emailDao;
    
    public void setEmailDao(EmailDao emailDao) {
        this.emailDao = emailDao;
    }

    @Override
    public boolean emailExists(String email) {
        Map<String, String> emailKeys = getEmailKeys(email);
        return emailDao.emailExists(emailKeys.get(EmailManager.HASH));
    }

    @Override
    public String findOrcidIdByEmail(String email) {
        Map<String, String> emailKeys = getEmailKeys(email);
        return emailDao.findOrcidIdByEmailHash(emailKeys.get(EmailManager.HASH));
    }
    
    @Override
    public boolean isPrimaryEmail(String orcid, String email) {
        return emailDao.isPrimaryEmail(orcid, email);
    }

    @Override
    public String findOrcidByVerifiedEmail(String email) {
        Map<String, String> emailKeys = getEmailKeys(email);
        return emailDao.findOrcidByVerifiedEmail(emailKeys.get(EmailManager.HASH));
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
    @SuppressWarnings("rawtypes")
    public Map<String, String> findIdsByEmails(List<String> emailList) {
        Map<String, String> emailIds = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        List ids = emailDao.findIdByCaseInsensitiveEmail(emailList);
        for (Iterator it = ids.iterator(); it.hasNext();) {
            Object[] orcidEmail = (Object[]) it.next();
            String orcid = (String) orcidEmail[0];
            String email = (String) orcidEmail[1];
            emailIds.put(email, orcid);
        }
        return emailIds;
    }
    
    @Override
    public boolean isPrimaryEmailVerified(String orcid) {
        try {
            String primaryEmail = emailDao.findPrimaryEmail(orcid).getEmail();
            return emailDao.isVerified(orcid, primaryEmail);
        } catch (javax.persistence.NoResultException nre) {
            String alternativePrimaryEmail = emailDao.findNewestVerifiedOrNewestEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);
            
            String message = String.format("User with orcid %s have no primary email, so, we are setting the newest verified email, or, the newest email in case non is verified as the primary one", orcid);
            LOGGER.error(message);            
            throw nre;
        } catch (javax.persistence.NonUniqueResultException nure) {
            String alternativePrimaryEmail = emailDao.findNewestPrimaryEmail(orcid);
            emailDao.updatePrimary(orcid, alternativePrimaryEmail);
            
            String message = String.format("User with orcid %s have more than one primary email, so, we are setting the latest modified primary as the primary one", orcid);
            LOGGER.error(message);    
            throw nure;
        }                        
    }
    
    @Override
    public Emails getVerifiedEmails(String orcid) {
        List<EmailEntity> entities = emailDao.findByOrcid(orcid, getLastModified(orcid));
        if (entities == null) {
            return new Emails();
        }
        return toEmails(entities.stream().filter(e -> e.getVerified()).collect(Collectors.toList()));
    }
    
    @Override
    
    public Emails getEmails(String orcid) {
        List<EmailEntity> entities = emailDao.findByOrcid(orcid, getLastModified(orcid));
        return toEmails(entities);
    }
    
    @Override
    public Emails getPublicEmails(String orcid) {
        List<EmailEntity> entities = emailDao.findPublicEmails(orcid, getLastModified(orcid));
        return toEmails(entities);
    }
    
    private Emails toEmails(List<EmailEntity> entities) {
        List<org.orcid.jaxb.model.v3.release.record.Email> emailList = jpaJaxbEmailAdapter.toEmailList(entities);
        Emails emails = new Emails();
        emails.setEmails(emailList);        
        return emails;
    }
    
    @Override
    public boolean haveAnyEmailVerified(String orcid) {
        List<EmailEntity> entities = emailDao.findByOrcid(orcid, getLastModified(orcid));
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
    public EmailEntity find(String email) {
        Map<String, String> emailKeys = getEmailKeys(email);
        return emailDao.find(emailKeys.get(EmailManager.HASH));
    }

    @Override
    public Email findPrimaryEmail(String orcid) {
        if(PojoUtil.isEmpty(orcid)) {
            return null;
        }
        return jpaJaxbEmailAdapter.toEmail(emailDao.findPrimaryEmail(orcid));
    }
    
    @Override
    public boolean isUsersOnlyEmail(String orcid, String email) {
        List<EmailEntity> emails = emailDao.findByOrcid(orcid, getLastModified(orcid));
        if (emails.size() != 1) {
            return false;
        }
        
        return email.equals(emails.get(0).getEmail());
    }

    @Override
    public EmailFrequencyOptions getEmailFrequencyOptions() {
        Map<String, String> frequencies = new LinkedHashMap<>();
        for (SendEmailFrequency freq : SendEmailFrequency.values()) {
            frequencies.put(String.valueOf(freq.value()), localeManager.resolveMessage(SendEmailFrequency.class.getName() + "." + freq.name()));                
        }
        EmailFrequencyOptions options = new EmailFrequencyOptions();
        options.setEmailFrequencies(frequencies);
        options.setEmailFrequencyKeys(new ArrayList<>(frequencies.keySet()));
        return options;
    }      
    
    public Map<String, String> getEmailKeys(String email) {
        String filteredEmail = OrcidStringUtils.filterEmailAddress(email);
        String hash = encryptionManager.getEmailHash(filteredEmail);
        Map<String, String> result = new HashMap<String, String>();
        result.put(EmailManager.FILTERED_EMAIL, filteredEmail);
        result.put(EmailManager.HASH, hash);
        return result;
    }
}
