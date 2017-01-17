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
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;

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

        Addresses addresses = addressManager.getAddresses(orcid, lastModifiedTime);
        if (addresses.getAddress() != null) {
            Addresses filteredAddresses = new Addresses();
            filteredAddresses.setAddress(new ArrayList<Address>(addresses.getAddress()));
            person.setAddresses(filteredAddresses);
        }

        PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(orcid, lastModifiedTime);
        if (extIds.getExternalIdentifiers() != null) {
            PersonExternalIdentifiers filteredExtIds = new PersonExternalIdentifiers();
            filteredExtIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(extIds.getExternalIdentifiers()));
            person.setExternalIdentifiers(filteredExtIds);
        }

        Keywords keywords = profileKeywordManager.getKeywords(orcid, lastModifiedTime);
        if (keywords.getKeywords() != null) {
            Keywords filteredKeywords = new Keywords();
            filteredKeywords.setKeywords(new ArrayList<Keyword>(keywords.getKeywords()));
            person.setKeywords(filteredKeywords);
        }

        OtherNames otherNames = otherNameManager.getOtherNames(orcid, lastModifiedTime);
        if (otherNames.getOtherNames() != null) {
            OtherNames filteredOtherNames = new OtherNames();
            filteredOtherNames.setOtherNames(new ArrayList<OtherName>(otherNames.getOtherNames()));
            person.setOtherNames(filteredOtherNames);
        }

        ResearcherUrls rUrls = researcherUrlManager.getResearcherUrls(orcid, lastModifiedTime);
        if (rUrls.getResearcherUrls() != null) {
            ResearcherUrls filteredRUrls = new ResearcherUrls();
            filteredRUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(rUrls.getResearcherUrls()));
            person.setResearcherUrls(filteredRUrls);
        }

        Emails emails = emailManager.getEmails(orcid, lastModifiedTime);
        if (emails.getEmails() != null) {
            Emails filteredEmails = new Emails();
            filteredEmails.setEmails(new ArrayList<Email>(emails.getEmails()));
            person.setEmails(filteredEmails);
        }
        return person;
    }

    @Override
    public Person getPublicPersonDetails(String orcid) {
        long lastModifiedTime = getLastModified(orcid);
        Person person = new Person();

        Name name = recordNameManager.getRecordName(orcid, lastModifiedTime);
        if (Visibility.PUBLIC.equals(name.getVisibility())) {
            person.setName(name);
        }

        Biography bio = biographyManager.getPublicBiography(orcid, lastModifiedTime);
        if (bio != null) {
            person.setBiography(bio);
        }

        Addresses addresses = addressManager.getPublicAddresses(orcid, lastModifiedTime);
        if (addresses.getAddress() != null) {
            Addresses filteredAddresses = new Addresses();
            filteredAddresses.setAddress(new ArrayList<Address>(addresses.getAddress()));
            person.setAddresses(filteredAddresses);
        }

        PersonExternalIdentifiers extIds = externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModifiedTime);
        if (extIds.getExternalIdentifiers() != null) {
            PersonExternalIdentifiers filteredExtIds = new PersonExternalIdentifiers();
            filteredExtIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(extIds.getExternalIdentifiers()));
            person.setExternalIdentifiers(filteredExtIds);
        }

        Keywords keywords = profileKeywordManager.getPublicKeywords(orcid, lastModifiedTime);
        if (keywords.getKeywords() != null) {
            Keywords filteredKeywords = new Keywords();
            filteredKeywords.setKeywords(new ArrayList<Keyword>(keywords.getKeywords()));
            person.setKeywords(filteredKeywords);
        }

        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid, lastModifiedTime);
        if (otherNames.getOtherNames() != null) {
            OtherNames filteredOtherNames = new OtherNames();
            filteredOtherNames.setOtherNames(new ArrayList<OtherName>(otherNames.getOtherNames()));
            person.setOtherNames(filteredOtherNames);
        }

        ResearcherUrls rUrls = researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime);
        if (rUrls.getResearcherUrls() != null) {
            ResearcherUrls filteredRUrls = new ResearcherUrls();
            filteredRUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(rUrls.getResearcherUrls()));
            person.setResearcherUrls(filteredRUrls);
        }

        Emails emails = emailManager.getPublicEmails(orcid, lastModifiedTime);
        if (emails.getEmails() != null) {
            Emails filteredEmails = new Emails();
            filteredEmails.setEmails(new ArrayList<Email>(emails.getEmails()));
            person.setEmails(filteredEmails);
        }

        return person;
    }
}
