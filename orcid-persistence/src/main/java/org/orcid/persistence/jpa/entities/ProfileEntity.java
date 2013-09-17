/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.Visibility;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p/>
 * JPA entity that contains the information at the top level of a profile. This
 * vaguely matches onto the {@link OrcidProfile} of the org.orcid.jaxb.entities
 * package.
 * <p/>
 * orcid-entities - Dec 6, 2011 - Profile
 * 
 * @author Declan Newman (declan)
 */

@Entity
@Table(name = "profile")
public class ProfileEntity extends BaseEntity<String> implements UserDetails {

    private static final long serialVersionUID = 7215593667128405456L;

    private static final String PROFILE = "profile";

    public ProfileEntity() {

    }

    public ProfileEntity(String orcid) {
        this.orcid = orcid;
    }

    // Main fields for publishing
    private String orcid;
    private OrcidType orcidType;
    private ClientType clientType;
    private GroupType groupType;
    private String givenNames;
    private String familyName;
    private String creditName;
    private SortedSet<OtherNameEntity> otherNames;
    private SortedSet<ResearcherUrlEntity> researcherUrls;
    private String biography;
    private String iso2Country;
    private SortedSet<ProfileKeywordEntity> keywords;
    private Set<ExternalIdentifierEntity> externalIdentifiers;
    private SortedSet<AffiliationEntity> affiliations;
    private Set<EmailEntity> emails;

    // Poor old vocative name :-(
    private String vocativeName;

    // Security fields
    private String encryptedPassword;
    private SecurityQuestionEntity securityQuestion;
    private String encryptedSecurityAnswer;
    private String encryptedVerificationCode;
    private Date accountExpiry;
    private Boolean accountNonLocked = Boolean.TRUE;
    private Date credentialsExpiry;
    private Boolean enabled = Boolean.TRUE;

    // Deprecation fields
    private ProfileEntity primaryRecord;
    private Date deprecatedDate;

    // Internally used fields
    private String creationMethod;
    private Date completedDate;
    private Date submissionDate = new Date();
    private Date lastIndexedDate;
    private Boolean claimed;
    private ProfileEntity source;
    private Set<ProfileEntity> sponsored;
    private Boolean isSelectableSponsor;
    private Collection<OrcidGrantedAuthority> authorities;
    private Set<GivenPermissionToEntity> givenPermissionTo;
    private Set<GivenPermissionByEntity> givenPermissionBy;
    private SortedSet<ProfileGrantEntity> profileGrants;
    private SortedSet<ProfilePatentEntity> profilePatents;
    private SortedSet<ProfileWorkEntity> profileWorks;
    private Locale locale = Locale.EN;
    private Boolean sendChangeNotifications;
    private Boolean sendOrcidNews;
    private String groupOrcid;
    private SortedSet<ProfileEntity> clientProfiles;
    private ClientDetailsEntity clientDetails;
    private SortedSet<OrcidOauth2TokenDetail> tokenDetails;
    private IndexingStatus indexingStatus = IndexingStatus.PENDING;
    private Set<ProfileEventEntity> profileEvents;

    // Visibility settings
    private Visibility creditNameVisibility;
    private Visibility otherNamesVisibility;
    private Visibility biographyVisibility;
    private Visibility keywordsVisibility;
    private Visibility externalIdentifiersVisibility;
    private Visibility researcherUrlsVisibility;
    private Visibility profileAddressVisibility;
    private Visibility workVisibilityDefault = Visibility.PRIVATE;

    private Date deactivationDate;

    @Id
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }

    public void setId(String orcid) {
        this.orcid = orcid;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "orcid_type")
    public OrcidType getOrcidType() {
        return orcidType;
    }

    public void setOrcidType(OrcidType orcidType) {
        this.orcidType = orcidType;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "group_type")
    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }
    
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "client_type")
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }
    
    @Column(name = "given_names", length = 150)
    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    @Column(name = "family_name", length = 150)
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Column(name = "credit_name", length = 150)
    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    @Column(name = "biography", length = 5000)
    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * @return the accountNonLocked
     */
    @Column(name = "account_non_locked", columnDefinition = "boolean default true")
    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * @param accountNonLocked
     *            the accountNonLocked to set
     */
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * @return the enabled
     */
    @Column(name = "enabled", columnDefinition = "boolean default true")
    public Boolean getEnabled() {
        return enabled != null ? enabled : Boolean.TRUE;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the creationMethod
     */
    @Column(name = "creation_method", length = 10)
    public String getCreationMethod() {
        return creationMethod;
    }

    /**
     * @param creationMethod
     *            the creationMethod to set
     */
    public void setCreationMethod(String creationMethod) {
        this.creationMethod = creationMethod;
    }

    /**
     * @return the completedDate
     */
    @Column(name = "completed_date")
    public Date getCompletedDate() {
        return completedDate;
    }

    /**
     * @param completedDate
     *            the completedDate to set
     */
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * @return the submissionDate
     */
    @Column(name = "submission_date")
    public Date getSubmissionDate() {
        return submissionDate;
    }

    /**
     * @param submissionDate
     *            the submissionDate to set
     */
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    @Column(name = "last_indexed_date")
    public Date getLastIndexedDate() {
        return lastIndexedDate;
    }

    public void setLastIndexedDate(Date lastIndexedDate) {
        this.lastIndexedDate = lastIndexedDate;
    }

    /**
     * @return the confirmed
     */
    @Column(name = "claimed", columnDefinition = "boolean default false")
    public Boolean getClaimed() {
        return claimed;
    }

    /**
     * @param claimed
     *            the confirmed to set
     */
    public void setClaimed(Boolean claimed) {
        this.claimed = claimed;
    }

    /**
     * @return the sponsor
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    public ProfileEntity getSource() {
        return source;
    }

    /**
     * @param source
     *            the sponsor to set
     */
    public void setSource(ProfileEntity source) {
        this.source = source;
    }

    @OneToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "source")
    public Set<ProfileEntity> getSponsored() {
        return sponsored;
    }

    public void setSponsored(Set<ProfileEntity> sponsored) {
        this.sponsored = sponsored;
    }

    @Column(name = "is_selectable_sponsor")
    public Boolean getIsSelectableSponsor() {
        return isSelectableSponsor;
    }

    public void setIsSelectableSponsor(Boolean isSelectableSponsor) {
        this.isSelectableSponsor = isSelectableSponsor;
    }

    /**
     * @return the vocativeName
     */
    @Column(name = "vocative_name", length = 450)
    public String getVocativeName() {
        return vocativeName;
    }

    /**
     * @param vocativeName
     *            the vocativeName to set
     */
    public void setVocativeName(String vocativeName) {
        this.vocativeName = vocativeName;
    }

    /**
     * @return the otherNames
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE)
    @Sort(type = SortType.NATURAL)
    public SortedSet<OtherNameEntity> getOtherNames() {
        return otherNames;
    }

    /**
     * @param otherNames
     *            the otherNames to set
     */
    public void setOtherNames(SortedSet<OtherNameEntity> otherNames) {
        this.otherNames = otherNames;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    public SortedSet<ProfileKeywordEntity> getKeywords() {
        return keywords;
    }

    public void setKeywords(SortedSet<ProfileKeywordEntity> keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the affiliations
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE)
    @Sort(type = SortType.NATURAL)
    public SortedSet<AffiliationEntity> getAffiliations() {
        return affiliations;
    }

    /**
     * @param affiliations
     *            the affiliations to set
     */
    public void setAffiliations(SortedSet<AffiliationEntity> affiliations) {
        this.affiliations = affiliations;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE, orphanRemoval = true)
    public Set<EmailEntity> getEmails() {
        return emails;
    }

    @Transient
    public EmailEntity getPrimaryEmail() {
        if (emails == null) {
            return null;
        }
        for (EmailEntity email : emails) {
            if (Boolean.TRUE.equals(email.getPrimary())) {
                return email;
            }
        }
        return null;
    }

    public void setPrimaryEmail(EmailEntity primaryEmail) {
        if (emails == null) {
            emails = new HashSet<>();
        }
        Iterator<EmailEntity> emailIterator = emails.iterator();
        while (emailIterator.hasNext()) {
            EmailEntity emailEntity = emailIterator.next();
            if (Boolean.TRUE.equals(emailEntity.getPrimary())) {
                emailIterator.remove();
            }
        }
        primaryEmail.setPrimary(true);
        primaryEmail.setProfile(this);
        emails.add(primaryEmail);
    }

    public void setEmails(Set<EmailEntity> emails) {
        this.emails = emails;
    }

    /**
     * @return the externalIdentifiers
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    public Set<ExternalIdentifierEntity> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    /**
     * @param externalIdentifiers
     *            the externalIdentifiers to set
     */
    public void setExternalIdentifiers(Set<ExternalIdentifierEntity> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    @OneToMany(mappedBy = "giver", cascade = CascadeType.ALL)
    public Set<GivenPermissionToEntity> getGivenPermissionTo() {
        return givenPermissionTo;
    }

    public void setGivenPermissionTo(Set<GivenPermissionToEntity> givenPermissionTo) {
        this.givenPermissionTo = givenPermissionTo;
    }

    @OneToMany(mappedBy = "receiver", cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.MERGE })
    public Set<GivenPermissionByEntity> getGivenPermissionBy() {
        return givenPermissionBy;
    }

    public void setGivenPermissionBy(Set<GivenPermissionByEntity> givenPermissionBy) {
        this.givenPermissionBy = givenPermissionBy;
    }

    /**
     * @return the grants
     */
    @OneToMany(mappedBy = PROFILE, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    public SortedSet<ProfileGrantEntity> getProfileGrants() {
        return profileGrants;
    }

    /**
     * @param grants
     *            the grants to set
     */
    public void setProfileGrants(SortedSet<ProfileGrantEntity> grants) {
        this.profileGrants = grants;
    }

    /**
     * @return the patents
     */
    @OneToMany(mappedBy = PROFILE, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    public SortedSet<ProfilePatentEntity> getProfilePatents() {
        return profilePatents;
    }

    /**
     * @param patents
     *            the works to set
     */
    public void setProfilePatents(SortedSet<ProfilePatentEntity> patents) {
        this.profilePatents = patents;
    }

    /**
     * @return the works
     */
    @OneToMany(mappedBy = PROFILE, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Sort(type = SortType.COMPARATOR, comparator = ProfileWorkEntity.ChronologicallyOrderedProfileWorkEntityComparator.class)
    public SortedSet<ProfileWorkEntity> getProfileWorks() {
        return profileWorks;
    }

    /**
     * @param works
     *            the works to set
     */
    public void setProfileWorks(SortedSet<ProfileWorkEntity> works) {
        this.profileWorks = works;
    }

    /**
     * @return the researcherUrls
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Sort(type = SortType.NATURAL)
    public SortedSet<ResearcherUrlEntity> getResearcherUrls() {
        return researcherUrls;
    }

    /**
     * @param researcherUrls
     *            the researcherUrls to set
     */
    public void setResearcherUrls(SortedSet<ResearcherUrlEntity> researcherUrls) {
        this.researcherUrls = researcherUrls;
    }

    @Column(name = "encrypted_password")
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @ManyToOne
    @JoinColumn(name = "security_question_id")
    public SecurityQuestionEntity getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestionEntity securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    @Column(name = "encrypted_security_answer")
    public String getEncryptedSecurityAnswer() {
        return encryptedSecurityAnswer;
    }

    public void setEncryptedSecurityAnswer(String encryptedSecurityAnswer) {
        this.encryptedSecurityAnswer = encryptedSecurityAnswer;
    }

    @Column(name = "encrypted_verification_code")
    public String getEncryptedVerificationCode() {
        return encryptedVerificationCode;
    }

    public void setEncryptedVerificationCode(String encryptedVerificationCode) {
        this.encryptedVerificationCode = encryptedVerificationCode;
    }

    @Column(name = "account_expiry")
    public Date getAccountExpiry() {
        return accountExpiry;
    }

    @Column(name = "iso2_country", length = 2)
    public String getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(String iso2Country) {
        this.iso2Country = iso2Country;
    }

    /**
     * Returns the authorities granted to the user. Cannot return
     * <code>null</code>.
     * 
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profileEntity")
    public Collection<OrcidGrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * @param authorities
     *            the authorities to set
     */
    public void setAuthorities(Collection<OrcidGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * Returns the password used to authenticate the user. Cannot return
     * <code>null</code>.
     * 
     * @return the password (never <code>null</code>)
     */
    @Override
    @Transient
    public String getPassword() {
        return encryptedPassword;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     * 
     * @return the username (never <code>null</code>)
     */
    @Override
    @Transient
    public String getUsername() {
        return orcid;
    }

    /**
     * Indicates whether the user's account has expired. An expired account
     * cannot be authenticated.
     * 
     * @return <code>true</code> if the user's account is valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return false;
    }

    @Transient
    public boolean isAccountNonLocked() {
        return accountNonLocked != null ? accountNonLocked : Boolean.FALSE;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     * 
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return (accountExpiry == null || accountExpiry.after(new Date()));
    }

    @Column(name = "credentials_expiry")
    public Date getCredentialsExpiry() {
        return credentialsExpiry;
    }

    /**
     * @param accountExpiry
     *            the accountExpiry to set
     */
    public void setAccountExpiry(Date accountExpiry) {
        this.accountExpiry = accountExpiry;
    }

    /**
     * @param credentialsExpiry
     *            the credentialsExpiry to set
     */
    public void setCredentialsExpiry(Date credentialsExpiry) {
        this.credentialsExpiry = credentialsExpiry;
    }

    @Transient
    public boolean isEnabled() {
        return enabled != null ? enabled : Boolean.TRUE;
    }

    @Column(name = "send_change_notifications")
    public Boolean getSendChangeNotifications() {
        return sendChangeNotifications;
    }

    public void setSendChangeNotifications(Boolean sendChangeNotifications) {
        this.sendChangeNotifications = sendChangeNotifications;
    }

    @Column(name = "send_orcid_news")
    public Boolean getSendOrcidNews() {
        return sendOrcidNews;
    }

    public void setSendOrcidNews(Boolean sendOrcidNews) {
        this.sendOrcidNews = sendOrcidNews;
    }

    @Column(name = "group_orcid")
    public String getGroupOrcid() {
        return groupOrcid;
    }

    public void setGroupOrcid(String groupOrcid) {
        this.groupOrcid = groupOrcid;
    }

    @OneToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_orcid")
    @Sort(type = SortType.COMPARATOR, comparator = OrcidEntityIdComparator.class)
    public SortedSet<ProfileEntity> getClientProfiles() {
        return clientProfiles;
    }

    public void setClientProfiles(SortedSet<ProfileEntity> clientProfiles) {
        this.clientProfiles = clientProfiles;
    }

    @OneToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "orcid")
    public ClientDetailsEntity getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetailsEntity clientDetails) {
        this.clientDetails = clientDetails;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE)
    @Sort(type = SortType.NATURAL)
    public SortedSet<OrcidOauth2TokenDetail> getTokenDetails() {
        return tokenDetails;
    }

    public void setTokenDetails(SortedSet<OrcidOauth2TokenDetail> tokenDetails) {
        this.tokenDetails = tokenDetails;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "indexing_status")
    public IndexingStatus getIndexingStatus() {
        return indexingStatus;
    }

    public void setIndexingStatus(IndexingStatus indexingStatus) {
        this.indexingStatus = indexingStatus;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "orcid")
    public Set<ProfileEventEntity> getProfileEvents() {
        return profileEvents;
    }

    public void setProfileEvents(Set<ProfileEventEntity> profileEvents) {
        this.profileEvents = profileEvents;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_name_visibility")
    public Visibility getCreditNameVisibility() {
        return creditNameVisibility;
    }

    public void setCreditNameVisibility(Visibility creditNameVisibility) {
        this.creditNameVisibility = creditNameVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "other_names_visibility")
    public Visibility getOtherNamesVisibility() {
        return otherNamesVisibility;
    }

    public void setOtherNamesVisibility(Visibility otherNamesVisibility) {
        this.otherNamesVisibility = otherNamesVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "biography_visibility")
    public Visibility getBiographyVisibility() {
        return biographyVisibility;
    }

    public void setBiographyVisibility(Visibility biographyVisibility) {
        this.biographyVisibility = biographyVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "keywords_visibility")
    public Visibility getKeywordsVisibility() {
        return keywordsVisibility;
    }

    public void setKeywordsVisibility(Visibility keywordsVisibility) {
        this.keywordsVisibility = keywordsVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "external_identifiers_visibility")
    public Visibility getExternalIdentifiersVisibility() {
        return externalIdentifiersVisibility;
    }

    public void setExternalIdentifiersVisibility(Visibility externalIdentifiersVisibility) {
        this.externalIdentifiersVisibility = externalIdentifiersVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "researcher_urls_visibility")
    public Visibility getResearcherUrlsVisibility() {
        return researcherUrlsVisibility;
    }

    public void setResearcherUrlsVisibility(Visibility researcherUrlsVisibility) {
        this.researcherUrlsVisibility = researcherUrlsVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_address_visibility")
    public Visibility getProfileAddressVisibility() {
        return profileAddressVisibility;
    }

    public void setProfileAddressVisibility(Visibility profileAddressVisibility) {
        this.profileAddressVisibility = profileAddressVisibility;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "work_visibility_default")
    public Visibility getWorkVisibilityDefault() {
        return workVisibilityDefault;
    }

    public void setWorkVisibilityDefault(Visibility workVisibilityDefault) {
        this.workVisibilityDefault = workVisibilityDefault;
    }

    @Column(name = "profile_deactivation_date")
    public Date getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Date deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    /**
     * @return the primary profile for this deprecated account
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_record")
    public ProfileEntity getPrimaryRecord() {
        return this.primaryRecord;
    }

    /**
     * @param primaryRecord
     *            the primary profile to set
     */
    public void setPrimaryRecord(ProfileEntity primaryRecord) {
        this.primaryRecord = primaryRecord;
    }

    /**
     * @return the deprecation date for this record
     * */
    @Column(name = "deprecated_date")
    public Date getDeprecatedDate() {
        return deprecatedDate;
    }

    /**
     * @param deprecationDate
     *            The deprecation date for this record
     * */
    public void setDeprecatedDate(Date deprecatedDate) {
        this.deprecatedDate = deprecatedDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfileEntity other = (ProfileEntity) obj;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        return true;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "locale")
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
