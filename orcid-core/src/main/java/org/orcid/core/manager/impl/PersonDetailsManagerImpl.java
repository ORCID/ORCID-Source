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

import javax.annotation.Resource;

import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.BiographyManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PersonDetailsManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.version.impl.Api2_0_rc3_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc3.LastModifiedDate;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.Name;
import org.orcid.jaxb.model.record_rc3.Person;

public class PersonDetailsManagerImpl implements PersonDetailsManager {
    @Resource
    private AddressManager addressManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private ProfileKeywordManager profileKeywordManager;

    @Resource
    private PersonalDetailsManager personalDetailsManager;

    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;
    
    @Resource
    private EmailManager emailManager;
       
    @Resource
    private OtherNameManager otherNamesManager;
    
    @Resource
    private RecordNameManager recordNameManager;
    
    @Resource
    private BiographyManager biographyManager;
       
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    public void setAddressManager(AddressManager addressManager) {
        this.addressManager = addressManager;
    }

    public void setExternalIdentifierManager(ExternalIdentifierManager externalIdentifierManager) {
        this.externalIdentifierManager = externalIdentifierManager;
    }

    public void setProfileKeywordManager(ProfileKeywordManager profileKeywordManager) {
        this.profileKeywordManager = profileKeywordManager;
    }

    public void setPersonalDetailsManager(PersonalDetailsManager personalDetailsManager) {
        this.personalDetailsManager = personalDetailsManager;
    }

    public void setOtherNameManager(OtherNameManager otherNameManager) {
        this.otherNameManager = otherNameManager;
    }

    public void setResearcherUrlManager(ResearcherUrlManager researcherUrlManager) {
        this.researcherUrlManager = researcherUrlManager;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    public void setOtherNamesManager(OtherNameManager otherNamesManager) {
        this.otherNamesManager = otherNamesManager;
    }

    public void setRecordNameManager(RecordNameManager recordNameManager) {
        this.recordNameManager = recordNameManager;
    }

    public void setBiographyManager(BiographyManager biographyManager) {
        this.biographyManager = biographyManager;
    }

    @Override    
    public Person getPersonDetails(String orcid) {        
        long lastModifiedTime = profileEntityManager.getLastModified(orcid);
        Person person = new Person();
        Biography biography = biographyManager.getBiography(orcid);
        if(biography != null) {
            person.setBiography(biography);
        } 
        
        person.setName(personalDetailsManager.getName(orcid));
        
        person.setAddresses(addressManager.getAddresses(orcid, lastModifiedTime));
        LastModifiedDate latest = person.getAddresses().getLastModifiedDate();
        
        person.setExternalIdentifiers(externalIdentifierManager.getExternalIdentifiers(orcid, lastModifiedTime));
        LastModifiedDate temp = person.getExternalIdentifiers().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setKeywords(profileKeywordManager.getKeywords(orcid, lastModifiedTime));
        temp = person.getKeywords().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);                
        
        person.setOtherNames(otherNameManager.getOtherNames(orcid, lastModifiedTime));
        temp = person.getOtherNames().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setResearcherUrls(researcherUrlManager.getResearcherUrls(orcid, lastModifiedTime));  
        temp = person.getResearcherUrls().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setEmails(emailManager.getEmails(orcid, lastModifiedTime));
        temp = person.getEmails().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setLastModifiedDate(latest);    
        return person;
    }

    @Override    
    public Person getPublicPersonDetails(String orcid) {
        Person person = new Person();
        
        Biography bio = biographyManager.getPublicBiography(orcid);        
        if(bio != null) {
            person.setBiography(bio);
        } 
        
        Name name = personalDetailsManager.getName(orcid);
        if(Visibility.PUBLIC.equals(name.getVisibility())) {
            person.setName(name);
        }
                
        long lastModifiedTime = profileEntityManager.getLastModified(orcid);
        person.setAddresses(addressManager.getPublicAddresses(orcid, lastModifiedTime));
        LastModifiedDate latest = person.getAddresses().getLastModifiedDate();
        
        person.setExternalIdentifiers(externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModifiedTime));
        LastModifiedDate temp = person.getExternalIdentifiers().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setKeywords(profileKeywordManager.getPublicKeywords(orcid, lastModifiedTime));
        temp = person.getKeywords().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setOtherNames(otherNameManager.getPublicOtherNames(orcid, lastModifiedTime));
        temp = person.getOtherNames().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setResearcherUrls(researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime));
        temp = person.getResearcherUrls().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);

        person.setEmails(emailManager.getPublicEmails(orcid, lastModifiedTime));
        temp = person.getEmails().getLastModifiedDate();
        latest = Api2_0_rc3_LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setLastModifiedDate(latest);
        return person;
    }
}
