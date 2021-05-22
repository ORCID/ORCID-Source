package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.*;

import java.util.Date;

public class PublicRecord {

    private String title;
    private String displayName;
    private NamesForm names;
    private BiographyForm biography;
    private OtherNamesForm otherNames;
    private AddressesForm countries;
    private KeywordsForm keyword;
    private Emails emails;
    private ExternalIdentifiersForm externalIdentifier;
    private WebsitesForm website;
    private Date lastModifiedTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public NamesForm getNames() {
        return names;
    }

    public void setNames(NamesForm names) {
        this.names = names;
    }

    public BiographyForm getBiography() {
        return biography;
    }

    public void setBiography(BiographyForm biography) {
        this.biography = biography;
    }

    public OtherNamesForm getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(OtherNamesForm otherNames) {
        this.otherNames = otherNames;
    }

    public AddressesForm getCountries() {
        return countries;
    }

    public void setCountries(AddressesForm countries) {
        this.countries = countries;
    }

    public KeywordsForm getKeyword() {
        return keyword;
    }

    public void setKeyword(KeywordsForm keyword) {
        this.keyword = keyword;
    }

    public Emails getEmails() {
        return emails;
    }

    public void setEmails(Emails emails) {
        this.emails = emails;
    }

    public ExternalIdentifiersForm getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(ExternalIdentifiersForm externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public WebsitesForm getWebsite() {
        return website;
    }

    public void setWebsite(WebsitesForm website) {
        this.website = website;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
