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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.common_rc4.LastModifiedDate;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherName;
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
        if (name != null) {
            personalDetails.setName(name);
        }

        Biography bio = biographyManager.getBiography(orcid, lastModifiedTime);
        if (bio != null) {
            personalDetails.setBiography(bio);
        }

        OtherNames otherNames = otherNameManager.getOtherNames(orcid, lastModifiedTime);
        if (otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            // Lets copy the list so we don't modify the cached collection
            List<OtherName> filteredList = new ArrayList<OtherName>(otherNames.getOtherNames());
            OtherNames filteredOtherNames = new OtherNames();
            filteredOtherNames.setOtherNames(filteredList);
            personalDetails.setOtherNames(filteredOtherNames);
        }

        if (personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {
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
        if (bio != null) {
            personalDetails.setBiography(bio);
        }

        Name name = recordNameManager.getRecordName(orcid, lastModifiedTime);
        if (name != null && !Visibility.PUBLIC.equals(name.getVisibility())) {
            personalDetails.setName(null);
        } else {
            personalDetails.setName(name);
        }

        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid, lastModifiedTime);
        if (otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            // Lets copy the list so we don't modify the cached collection
            List<OtherName> filteredList = new ArrayList<OtherName>(otherNames.getOtherNames());
            OtherNames filteredOtherNames = new OtherNames();
            filteredOtherNames.setOtherNames(filteredList);
            personalDetails.setOtherNames(filteredOtherNames);
        }

        if (personalDetails.getLastModifiedDate() == null || personalDetails.getLastModifiedDate().getValue() == null) {
            personalDetails.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified)));
        }

        return personalDetails;
    }
}
