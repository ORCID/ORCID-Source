package org.orcid.core.manager.v3.read_only.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.orcid.core.manager.v3.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.v3.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Biography;
import org.orcid.jaxb.model.v3.dev1.record.Name;
import org.orcid.jaxb.model.v3.dev1.record.OtherNames;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.PersonalDetails;
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
        PersonalDetails personalDetails = new PersonalDetails();
        Name name = recordNameManager.getRecordName(orcid);
        if (name != null) {
            personalDetails.setName(name);
        }

        Biography bio = biographyManager.getBiography(orcid);
        if (bio != null) {
            personalDetails.setBiography(bio);
        }

        OtherNames otherNames = otherNameManager.getOtherNames(orcid);
        OtherNames filteredOtherNames = new OtherNames();
        personalDetails.setOtherNames(filteredOtherNames);
        if (otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            // Lets copy the list so we don't modify the cached collection
            List<OtherName> filteredList = new ArrayList<OtherName>(otherNames.getOtherNames());
            filteredOtherNames.setOtherNames(filteredList);            
        }

        if (personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {
            personalDetails.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified)));
        }

        return personalDetails;
    }

    @Override
    public PersonalDetails getPublicPersonalDetails(String orcid) {
        Date lastModified = getLastModifiedDate(orcid);
        PersonalDetails personalDetails = new PersonalDetails();

        Biography bio = biographyManager.getPublicBiography(orcid);
        if (bio != null) {
            personalDetails.setBiography(bio);
        }

        Name name = recordNameManager.getRecordName(orcid);
        if (name != null && !Visibility.PUBLIC.equals(name.getVisibility())) {
            personalDetails.setName(null);
        } else {
            personalDetails.setName(name);
        }

        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid);
        OtherNames filteredOtherNames = new OtherNames();
        personalDetails.setOtherNames(filteredOtherNames);
        if (otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            // Lets copy the list so we don't modify the cached collection
            List<OtherName> filteredList = new ArrayList<OtherName>(otherNames.getOtherNames());
            filteredOtherNames.setOtherNames(filteredList);
        }

        if (personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {
            personalDetails.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified)));
        }

        return personalDetails;
    }
}
