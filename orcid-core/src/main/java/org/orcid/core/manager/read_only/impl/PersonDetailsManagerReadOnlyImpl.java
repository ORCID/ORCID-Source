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
package org.orcid.core.manager.read_only.impl;

import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.Person;

public class PersonDetailsManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements PersonDetailsManagerReadOnly {
    
    protected AddressManagerReadOnly addressManager;

    protected ExternalIdentifierManagerReadOnly externalIdentifierManager;

    protected ProfileKeywordManagerReadOnly profileKeywordManager;

    protected OtherNameManagerReadOnly otherNameManager;

    protected ResearcherUrlManagerReadOnly researcherUrlManager;
    
    protected EmailManagerReadOnly emailManager;           
    
    protected RecordNameManagerReadOnly recordNameManager;
    
    protected BiographyManagerReadOnly biographyManager;
       
    public void setAddressManager(AddressManagerReadOnly addressManager) {
        this.addressManager = addressManager;
    }

    public void setExternalIdentifierManager(ExternalIdentifierManagerReadOnly externalIdentifierManager) {
        this.externalIdentifierManager = externalIdentifierManager;
    }

    public void setProfileKeywordManager(ProfileKeywordManagerReadOnly profileKeywordManager) {
        this.profileKeywordManager = profileKeywordManager;
    }    

    public void setOtherNameManager(OtherNameManagerReadOnly otherNameManager) {
        this.otherNameManager = otherNameManager;
    }

    public void setResearcherUrlManager(ResearcherUrlManagerReadOnly researcherUrlManager) {
        this.researcherUrlManager = researcherUrlManager;
    }

    public void setEmailManager(EmailManagerReadOnly emailManager) {
        this.emailManager = emailManager;
    }
    
    public void setRecordNameManager(RecordNameManagerReadOnly recordNameManager) {
        this.recordNameManager = recordNameManager;
    }

    public void setBiographyManager(BiographyManagerReadOnly biographyManager) {
        this.biographyManager = biographyManager;
    }

    @Override    
    public Person getPersonDetails(String orcid) {        
        long lastModifiedTime = getLastModified(orcid);
        Person person = new Person();        
        person.setName(recordNameManager.getRecordName(orcid, lastModifiedTime));
        person.setBiography(biographyManager.getBiography(orcid, lastModifiedTime));
        
        person.setAddresses(addressManager.getAddresses(orcid, lastModifiedTime));
        person.setExternalIdentifiers(externalIdentifierManager.getExternalIdentifiers(orcid, lastModifiedTime));
        person.setKeywords(profileKeywordManager.getKeywords(orcid, lastModifiedTime));
        person.setOtherNames(otherNameManager.getOtherNames(orcid, lastModifiedTime));
        person.setResearcherUrls(researcherUrlManager.getResearcherUrls(orcid, lastModifiedTime));  
        person.setEmails(emailManager.getEmails(orcid, lastModifiedTime));                   
        return person;
    }

    @Override    
    public Person getPublicPersonDetails(String orcid) {
        long lastModifiedTime = getLastModified(orcid);
        Person person = new Person();                
        
        Name name = recordNameManager.getRecordName(orcid, lastModifiedTime);
        if(Visibility.PUBLIC.equals(name.getVisibility())) {
            person.setName(name);
        }
        
        Biography bio = biographyManager.getPublicBiography(orcid, lastModifiedTime);        
        if(bio != null) {
            person.setBiography(bio);
        }
                        
        person.setAddresses(addressManager.getPublicAddresses(orcid, lastModifiedTime));
        person.setExternalIdentifiers(externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModifiedTime));
        person.setKeywords(profileKeywordManager.getPublicKeywords(orcid, lastModifiedTime));
        person.setOtherNames(otherNameManager.getPublicOtherNames(orcid, lastModifiedTime));
        person.setResearcherUrls(researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime));
        person.setEmails(emailManager.getPublicEmails(orcid, lastModifiedTime));
        return person;
    }
}
