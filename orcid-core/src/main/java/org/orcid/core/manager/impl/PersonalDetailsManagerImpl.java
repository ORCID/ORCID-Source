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
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.version.impl.LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc3.CreatedDate;
import org.orcid.jaxb.model.common_rc3.CreditName;
import org.orcid.jaxb.model.common_rc3.LastModifiedDate;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.FamilyName;
import org.orcid.jaxb.model.record_rc3.GivenNames;
import org.orcid.jaxb.model.record_rc3.Name;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.OtherNames;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

/**
* 
* @author Angel Montenegro
* 
*/
public class PersonalDetailsManagerImpl implements PersonalDetailsManager {
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private OtherNameManager otherNameManager;
    
    
    @Override
    public Name getName(String orcid) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        Visibility nameVisibility = Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value());
        Name name = new Name();
        if (profileEntity != null) {            
            RecordNameEntity recordName = profileEntity.getRecordNameEntity();
            if(recordName != null) {
                if(recordName.getVisibility() != null) {
                    nameVisibility = recordName.getVisibility();
                }            
                name.setVisibility(nameVisibility);            
                if (!PojoUtil.isEmpty(recordName.getCreditName())) {
                    name.setCreditName(new CreditName(recordName.getCreditName()));
                }
                if (!PojoUtil.isEmpty(recordName.getFamilyName())) {
                    name.setFamilyName(new FamilyName(recordName.getFamilyName()));
                }
                if (!PojoUtil.isEmpty(recordName.getGivenNames())) {
                    name.setGivenNames(new GivenNames(recordName.getGivenNames()));
                }
                
                name.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(recordName.getDateCreated())));
                name.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(recordName.getLastModified())));
            }                                   
        }
        return name;
    }
    
    @Override
    public PersonalDetails getPersonalDetails(String orcid) {
        PersonalDetails personalDetails = new PersonalDetails();
        Name name = getName(orcid);
        XMLGregorianCalendar nameLastModified = null;
        if(name != null) {
            personalDetails.setName(name);
            if(name.getLastModifiedDate() != null) {
                nameLastModified = name.getLastModifiedDate().getValue();
            }
        }                               
        
        Biography bio = getBiography(orcid);
        XMLGregorianCalendar bioLastModified = null;
        if(bio != null) {
            personalDetails.setBiography(bio);
            if(bio.getLastModifiedDate() != null) {
                bioLastModified = bio.getLastModifiedDate().getValue();
            }            
        }
        
        
        OtherNames otherNames = getOtherNames(orcid);
        XMLGregorianCalendar otherNamesLatest = null;
        if(otherNames != null && otherNames.getOtherNames() != null) {            
            otherNamesLatest = LastModifiedDatesHelper.calculateLatest(otherNames);
            otherNames.setLastModifiedDate(new LastModifiedDate(otherNamesLatest));
            personalDetails.setOtherNames(otherNames);            
        }               
        
        personalDetails.setLastModifiedDate(new LastModifiedDate(LastModifiedDatesHelper.calculateLatest(nameLastModified, bioLastModified, otherNamesLatest)));
                
        if(personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);                
            Date lastModified = profileEntity.getLastModified();            
            personalDetails.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified)));
        }
        
        return personalDetails;
    }   
    
    @Override
    public PersonalDetails getPublicPersonalDetails(String orcid) {
        PersonalDetails personalDetails = new PersonalDetails();
        
        Biography bio = getBiography(orcid);
        XMLGregorianCalendar bioLastModified = null;        
        if(bio != null && !Visibility.PUBLIC.equals(bio.getVisibility())) {
            personalDetails.setBiography(null);
        } else {
            personalDetails.setBiography(bio);
            if(bio.getLastModifiedDate() != null) {
                bioLastModified = bio.getLastModifiedDate().getValue();
            }
        }
        
        Name name = getName(orcid);        
        XMLGregorianCalendar nameLastModified = null;
        if(name != null && !Visibility.PUBLIC.equals(name.getVisibility())) {
            personalDetails.setName(null);
        } else {
            personalDetails.setName(name);
            if(name.getLastModifiedDate() != null) {
                nameLastModified = name.getLastModifiedDate().getValue();
            }
        }
        
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);                
        Date lastModified = profileEntity.getLastModified();
        long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
        
        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid, lastModifiedTime);
        XMLGregorianCalendar otherNamesLatest = null;
        if(otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            List<OtherName> publicOtherNames = new ArrayList<OtherName>();
            for(OtherName otherName : otherNames.getOtherNames()) {
                if(Visibility.PUBLIC.equals(otherName.getVisibility())) {
                    publicOtherNames.add(otherName);
                }
            }
            if(publicOtherNames.isEmpty()) {
                personalDetails.setOtherNames(null);
            } else {
                otherNames.setOtherNames(publicOtherNames);
                otherNamesLatest = LastModifiedDatesHelper.calculateLatest(otherNames);
                otherNames.setLastModifiedDate(new LastModifiedDate(otherNamesLatest));
                personalDetails.setOtherNames(otherNames);                
            }            
        } else {
            personalDetails.setOtherNames(null);
        }
        
        personalDetails.setLastModifiedDate(new LastModifiedDate(LastModifiedDatesHelper.calculateLatest(nameLastModified, bioLastModified, otherNamesLatest)));
        
        if(personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {            
            personalDetails.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified)));
        }
        
        return personalDetails;
    }
    
    public Biography getBiography(String orcid) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);        
        Biography bio = new Biography();
        if(profileEntity.getBiographyEntity() != null) {
            BiographyEntity biographyEntity = profileEntity.getBiographyEntity(); 
            if(!PojoUtil.isEmpty(biographyEntity.getBiography())) {
                bio.setContent(biographyEntity.getBiography());
            }      
            
            Visibility bioVisibility = Visibility.fromValue(OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility().value());
            if(biographyEntity.getVisibility() != null) {
                bioVisibility = biographyEntity.getVisibility();
            } else if(profileEntity.getActivitiesVisibilityDefault() != null) {
                bioVisibility = Visibility.fromValue(profileEntity.getActivitiesVisibilityDefault().value());
            }
            bio.setVisibility(bioVisibility);
            bio.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(biographyEntity.getLastModified())));
            bio.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(biographyEntity.getDateCreated())));
        }                 
        return bio;
    }
    
    public OtherNames getOtherNames(String orcid) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);                
        Date lastModified = profileEntity.getLastModified();
        long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
        
        return otherNameManager.getOtherNames(orcid, lastModifiedTime);        
    }
}
