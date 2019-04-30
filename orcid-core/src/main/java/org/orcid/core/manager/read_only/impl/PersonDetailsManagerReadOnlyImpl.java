package org.orcid.core.manager.read_only.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;

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
        Person person = new Person();
        person.setName(recordNameManager.getRecordName(orcid));
        person.setBiography(biographyManager.getBiography(orcid));

        Addresses addresses = addressManager.getAddresses(orcid);
        if (addresses.getAddress() != null) {
            Addresses filteredAddresses = new Addresses();
            filteredAddresses.setAddress(new ArrayList<Address>(addresses.getAddress()));
            person.setAddresses(filteredAddresses);
        }

        PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(orcid);
        if (extIds.getExternalIdentifiers() != null) {
            PersonExternalIdentifiers filteredExtIds = new PersonExternalIdentifiers();
            filteredExtIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(extIds.getExternalIdentifiers()));
            person.setExternalIdentifiers(filteredExtIds);
        }

        Keywords keywords = profileKeywordManager.getKeywords(orcid);
        if (keywords.getKeywords() != null) {
            Keywords filteredKeywords = new Keywords();
            filteredKeywords.setKeywords(new ArrayList<Keyword>(keywords.getKeywords()));
            person.setKeywords(filteredKeywords);
        }

        OtherNames otherNames = otherNameManager.getOtherNames(orcid);
        if (otherNames.getOtherNames() != null) {
            OtherNames filteredOtherNames = new OtherNames();
            filteredOtherNames.setOtherNames(new ArrayList<OtherName>(otherNames.getOtherNames()));
            person.setOtherNames(filteredOtherNames);
        }

        ResearcherUrls rUrls = researcherUrlManager.getResearcherUrls(orcid);
        if (rUrls.getResearcherUrls() != null) {
            ResearcherUrls filteredRUrls = new ResearcherUrls();
            filteredRUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(rUrls.getResearcherUrls()));
            person.setResearcherUrls(filteredRUrls);
        }

        Emails emails = emailManager.getEmails(orcid);
        if (emails.getEmails() != null) {
            Emails filteredEmails = new Emails();
            
            if (Features.HIDE_UNVERIFIED_EMAILS.isActive()) {
                filteredEmails.setEmails(new ArrayList<Email>(emails.getEmails().stream().filter(e -> e.isVerified()).collect(Collectors.toList())));
            } else {
                filteredEmails.setEmails(new ArrayList<Email>(emails.getEmails()));
            }
            
            person.setEmails(filteredEmails);
        }
        return person;
    }

    @Override
    public Person getPublicPersonDetails(String orcid) {
        Person person = new Person();

        Name name = recordNameManager.getRecordName(orcid);
        if (Visibility.PUBLIC.equals(name.getVisibility())) {
            person.setName(name);
        }

        Biography bio = biographyManager.getPublicBiography(orcid);
        if (bio != null) {
            person.setBiography(bio);
        }

        Addresses addresses = addressManager.getPublicAddresses(orcid);
        if (addresses.getAddress() != null) {
            Addresses filteredAddresses = new Addresses();
            filteredAddresses.setAddress(new ArrayList<Address>(addresses.getAddress()));
            person.setAddresses(filteredAddresses);
        }

        PersonExternalIdentifiers extIds = externalIdentifierManager.getPublicExternalIdentifiers(orcid);
        if (extIds.getExternalIdentifiers() != null) {
            PersonExternalIdentifiers filteredExtIds = new PersonExternalIdentifiers();
            filteredExtIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(extIds.getExternalIdentifiers()));
            person.setExternalIdentifiers(filteredExtIds);
        }

        Keywords keywords = profileKeywordManager.getPublicKeywords(orcid);
        if (keywords.getKeywords() != null) {
            Keywords filteredKeywords = new Keywords();
            filteredKeywords.setKeywords(new ArrayList<Keyword>(keywords.getKeywords()));
            person.setKeywords(filteredKeywords);
        }

        OtherNames otherNames = otherNameManager.getPublicOtherNames(orcid);
        if (otherNames.getOtherNames() != null) {
            OtherNames filteredOtherNames = new OtherNames();
            filteredOtherNames.setOtherNames(new ArrayList<OtherName>(otherNames.getOtherNames()));
            person.setOtherNames(filteredOtherNames);
        }

        ResearcherUrls rUrls = researcherUrlManager.getPublicResearcherUrls(orcid);
        if (rUrls.getResearcherUrls() != null) {
            ResearcherUrls filteredRUrls = new ResearcherUrls();
            filteredRUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(rUrls.getResearcherUrls()));
            person.setResearcherUrls(filteredRUrls);
        }

        Emails emails = emailManager.getPublicEmails(orcid);
        if (emails.getEmails() != null) {
            Emails filteredEmails = new Emails();
            filteredEmails.setEmails(new ArrayList<Email>(emails.getEmails()));
            person.setEmails(filteredEmails);
        }

        return person;
    }
}
