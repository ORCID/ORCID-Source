package org.orcid.pojo;

import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Biography;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;

public class PublicRecordPersonDetails {

    private String title;
    
    private String displayName;
    
    private Biography biography;
    
    private  Map<String, List<OtherName>> publicGroupedOtherNames;
    
    private Address publicAddress;
    
    private Map<String, String> countryNames;
    
    private Map<String, List<Address>> publicGroupedAddresses;
    
    private Map<String, List<Keyword>> publicGroupedKeywords;
    
    private Map<String, List<ResearcherUrl>> publicGroupedResearcherUrls;
    
    private Map<String, List<Email>> publicGroupedEmails;
    
    private Map<String, List<PersonExternalIdentifier>> publicGroupedPersonExternalIdentifiers;
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Biography getBiography() {
        return biography;
    }

    public void setBiography(Biography biography) {
        this.biography = biography;
    }

    public Map<String, List<OtherName>> getPublicGroupedOtherNames() {
        return publicGroupedOtherNames;
    }

    public void setPublicGroupedOtherNames(Map<String, List<OtherName>> publicGroupedOtherNames) {
        this.publicGroupedOtherNames = publicGroupedOtherNames;
    }

    public Address getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(Address publicAddress) {
        this.publicAddress = publicAddress;
    }

    public Map<String, String> getCountryNames() {
        return countryNames;
    }

    public void setCountryNames(Map<String, String> countryNames) {
        this.countryNames = countryNames;
    }

    public Map<String, List<Address>> getPublicGroupedAddresses() {
        return publicGroupedAddresses;
    }

    public void setPublicGroupedAddresses(Map<String, List<Address>> publicGroupedAddresses) {
        this.publicGroupedAddresses = publicGroupedAddresses;
    }

    public Map<String, List<Keyword>> getPublicGroupedKeywords() {
        return publicGroupedKeywords;
    }

    public void setPublicGroupedKeywords(Map<String, List<Keyword>> groupedKeywords) {
        this.publicGroupedKeywords = groupedKeywords;
    }

    public Map<String, List<ResearcherUrl>> getPublicGroupedResearcherUrls() {
        return publicGroupedResearcherUrls;
    }

    public void setPublicGroupedResearcherUrls(Map<String, List<ResearcherUrl>> publicGroupedResearcherUrls) {
        this.publicGroupedResearcherUrls = publicGroupedResearcherUrls;
    }

    public Map<String, List<Email>> getPublicGroupedEmails() {
        return publicGroupedEmails;
    }

    public void setPublicGroupedEmails(Map<String, List<Email>> publicGroupedEmails) {
        this.publicGroupedEmails = publicGroupedEmails;
    }

    public Map<String, List<PersonExternalIdentifier>> getPublicGroupedPersonExternalIdentifiers() {
        return publicGroupedPersonExternalIdentifiers;
    }

    public void setPublicGroupedPersonExternalIdentifiers(Map<String, List<PersonExternalIdentifier>> publicGroupedPersonExternalIdentifiers) {
        this.publicGroupedPersonExternalIdentifiers = publicGroupedPersonExternalIdentifiers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
