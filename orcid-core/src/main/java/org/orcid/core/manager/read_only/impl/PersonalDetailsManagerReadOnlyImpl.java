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

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.core.version.impl.Api2_0_rc4_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc4.LastModifiedDate;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.utils.DateUtils;

/**
* 
* @author Angel Montenegro
* 
*/
public class PersonalDetailsManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements PersonalDetailsManagerReadOnly {
    
    protected OtherNameManagerReadOnly otherNameManager;        
    
    protected RecordNameManagerReadOnly recordNameManager;
    
    protected BiographyManagerReadOnly biographyManager;
        
    public void setOtherNameManager(OtherNameManagerReadOnly otherNameManager) {
        this.otherNameManager = otherNameManager;
    }

    public void setRecordNameManager(RecordNameManagerReadOnly recordNameManager) {
        this.recordNameManager = recordNameManager;
    }

    public void setBiographyManager(BiographyManagerReadOnly biographyManager) {
        this.biographyManager = biographyManager;
    }

    @Override
    public PersonalDetails getPersonalDetails(String orcid) {
        Date lastModified = getLastModifiedDate(orcid);
        long lastModifiedTime = lastModified.getTime();
        PersonalDetails personalDetails = new PersonalDetails();
        Name name = recordNameManager.getRecordName(orcid, lastModifiedTime);
        XMLGregorianCalendar nameLastModified = null;
        if(name != null) {
            personalDetails.setName(name);
            if(name.getLastModifiedDate() != null) {
                nameLastModified = name.getLastModifiedDate().getValue();
            }
        }                               
        
        Biography bio = biographyManager.getBiography(orcid, lastModifiedTime);
        XMLGregorianCalendar bioLastModified = null;
        if(bio != null) {
            personalDetails.setBiography(bio);
            if(bio.getLastModifiedDate() != null) {
                bioLastModified = bio.getLastModifiedDate().getValue();
            }            
        }
                
        OtherNames otherNames = otherNameManager.getOtherNames(orcid, lastModifiedTime);
        XMLGregorianCalendar otherNamesLatest = null;
        if(otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {            
            otherNamesLatest = Api2_0_rc4_LastModifiedDatesHelper.calculateLatest(otherNames);
            otherNames.setLastModifiedDate(new LastModifiedDate(otherNamesLatest));
            personalDetails.setOtherNames(otherNames);            
        }               
        
        personalDetails.setLastModifiedDate(new LastModifiedDate(Api2_0_rc4_LastModifiedDatesHelper.calculateLatest(nameLastModified, bioLastModified, otherNamesLatest)));
                
        if(personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {                       
            personalDetails.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified)));
        }
        
        return personalDetails;
    }   
    
    @Override
    public PersonalDetails getPublicPersonalDetails(String orcid) {        
        Date lastModified = getLastModifiedDate(orcid);
        long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();        
        PersonalDetails personalDetails = new PersonalDetails();
        
        Biography bio = biographyManager.getPublicBiography(orcid, lastModifiedTime);
        XMLGregorianCalendar bioLastModified = null;
        if(bio != null) {
            personalDetails.setBiography(bio);
            if(bio.getLastModifiedDate() != null) {
                bioLastModified = bio.getLastModifiedDate().getValue();
            }            
        }               
        
        Name name = recordNameManager.getRecordName(orcid, lastModifiedTime);        
        XMLGregorianCalendar nameLastModified = null;
        if(name != null && !Visibility.PUBLIC.equals(name.getVisibility())) {
            personalDetails.setName(null);
        } else {
            personalDetails.setName(name);
            if(name.getLastModifiedDate() != null) {
                nameLastModified = name.getLastModifiedDate().getValue();
            }
        }
        
        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid, lastModifiedTime);
        XMLGregorianCalendar otherNamesLatest = null;
        if(otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            personalDetails.setOtherNames(otherNames);  
            otherNamesLatest = otherNames.getLastModifiedDate().getValue();
        }                
        
        personalDetails.setLastModifiedDate(new LastModifiedDate(Api2_0_rc4_LastModifiedDatesHelper.calculateLatest(nameLastModified, bioLastModified, otherNamesLatest)));
        
        if(personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {            
            personalDetails.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified)));
        }
        
        return personalDetails;
    }        
}
