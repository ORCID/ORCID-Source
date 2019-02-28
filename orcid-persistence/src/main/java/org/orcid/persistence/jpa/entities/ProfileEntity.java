package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.utils.DateUtils;
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
public class ProfileEntity extends BaseEntity<String> implements UserDetails, Serializable {

    private static final long serialVersionUID = 7215593667128405456L;

    private static final String PROFILE = "profile";
    
    public static final String USER_DRIVEN_DEPRECATION = "USER_DRIVEN";
    
    public static final String ADMIN_DEPRECATION = "ADMIN";
    
    public static final String AUTO_DEPRECATION = "AUTO";
    
    private static final String DEFAULT_LOCALE = "EN";
    
    private static final String DEFAULT_ACTIVITIES_VISIBILITY_DEFAULT = "PRIVATE";

    public ProfileEntity() {

    }

    public ProfileEntity(String orcid) {
        this.orcid = orcid;
    }

    // Main fields for publishing
    private String orcid;
    private String orcidType;
    private String groupType;
    private SortedSet<OtherNameEntity> otherNames;
    private SortedSet<ResearcherUrlEntity> researcherUrls;
    private SortedSet<ProfileKeywordEntity> keywords;
    private Set<ExternalIdentifierEntity> externalIdentifiers;
    private SortedSet<OrgAffiliationRelationEntity> orgAffiliationRelations;
    private Set<EmailEntity> emails;
    private SortedSet<ResearchResourceEntity> researchResources;

    // Security fields
    private String encryptedPassword;
    private String encryptedVerificationCode;
    private Date accountExpiry;
    private Boolean recordLocked = Boolean.FALSE;
    private String reasonLocked;
    private String reasonLockedDescription;
    private Date credentialsExpiry;
    private Boolean enabled = Boolean.TRUE;
    private String referredBy;
    private Date lastLogin;

    // Deprecation fields
    private ProfileEntity primaryRecord;
    private Date deprecatedDate;
    private String deprecatedMethod;
    private String deprecatingAdmin;

    // Internally used fields
    private String creationMethod;
    private Date completedDate;
    private Date submissionDate = new Date();
    private Date lastIndexedDate;
    private Boolean claimed;
    private SourceEntity source;
    private Boolean isSelectableSponsor;
    private Collection<OrcidGrantedAuthority> authorities;
    private Set<GivenPermissionToEntity> givenPermissionTo;
    private Set<GivenPermissionByEntity> givenPermissionBy;
    private SortedSet<ProfileFundingEntity> profileFunding;
    private Set<AddressEntity> addresses;
    private SortedSet<PeerReviewEntity> peerReviews;
    private String locale = DEFAULT_LOCALE;
    
    private SortedSet<ClientDetailsEntity> clients;
    private SortedSet<OrcidOauth2TokenDetail> tokenDetails;
    private IndexingStatus indexingStatus = IndexingStatus.PENDING;
    private Set<ProfileEventEntity> profileEvents;
    private boolean enableDeveloperTools;
    private Date developerToolsEnabledDate;
    
    // Salesfore ID
    private String salesforeId;

    private Date deactivationDate;

    // Captcha validator used on register
    private Boolean usedRecaptchaOnRegistration;

    private String userLastIp;
    private boolean reviewed = Boolean.FALSE;

    private String activitiesVisibilityDefault = DEFAULT_ACTIVITIES_VISIBILITY_DEFAULT;   
    
    private RecordNameEntity recordNameEntity;
    
    private BiographyEntity biographyEntity;    
    
    private String hashedOrcid;
    
    // 2FA
    private Boolean using2FA = Boolean.FALSE;
    private String secretFor2FA;
    
    @Id
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }

    public void setId(String orcid) {
        this.orcid = orcid;
    }
    
    /**
     * @return the hashedOrcid
     */
    @Column(name = "hashed_orcid")
    public String getHashedOrcid() {
        return hashedOrcid;
    }

    /**
     * @param hashedOrcid
     *            a hashed version of the orcid id
     */
    public void setHashedOrcid(String hashedOrcid) {
        this.hashedOrcid = hashedOrcid;
    }
    
    @Column(name = "using_2fa")
    public Boolean getUsing2FA() {
        return using2FA != null ? using2FA : Boolean.FALSE;
    }

    public void setUsing2FA(Boolean using2FA) {
        this.using2FA = using2FA;
    }
    
    @Column(name = "secret_for_2fa")
    public String getSecretFor2FA() {
        return secretFor2FA;
    }

    public void setSecretFor2FA(String secretFor2FA) {
        this.secretFor2FA = secretFor2FA;
    }

    @Column(name = "orcid_type")
    public String getOrcidType() {
        return orcidType;
    }

    public void setOrcidType(String orcidType) {
        this.orcidType = orcidType;
    }

    @Column(name = "group_type")
    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * @return the recordLocked
     */
    @Column(name = "record_locked", columnDefinition = "boolean default false")
    public Boolean getRecordLocked() {
        return recordLocked;
    }

    /**
     * @param reasonLocked
     *            the reason the record was locked
     */
    public void setReasonLocked(String reasonLocked) {
        this.reasonLocked = reasonLocked;
    }
    
    /**
     * @return the reasonLocked
     */
    @Column(name = "reason_locked")
    public String getReasonLocked() {
        return reasonLocked;
    }
    
    /**
     * @param reasonLockedDescription
     *            a description of the reason the record was locked
     */
    public void setReasonLockedDescription(String reasonLockedDescription) {
        this.reasonLockedDescription = reasonLockedDescription;
    }
    
    /**
     * @return the lockedReason
     */
    @Column(name = "reason_locked_description")
    public String getReasonLockedDescription() {
        return reasonLockedDescription;
    }

    /**
     * @param recordLocked
     *            the recordLocked to set
     */
    public void setRecordLocked(Boolean recordLocked) {
        this.recordLocked = recordLocked;
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

    public SourceEntity getSource() {
        return source;
    }

    /**
     * @param source
     *            the sponsor to set
     */
    public void setSource(SourceEntity source) {
        this.source = source;
    }

    @Column(name = "is_selectable_sponsor")
    public Boolean getIsSelectableSponsor() {
        return isSelectableSponsor;
    }

    public void setIsSelectableSponsor(Boolean isSelectableSponsor) {
        this.isSelectableSponsor = isSelectableSponsor;
    }

    /**
     * @return the otherNames
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE, orphanRemoval = true)
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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    public SortedSet<OrgAffiliationRelationEntity> getOrgAffiliationRelations() {
        return orgAffiliationRelations;
    }

    /**
     * @param affiliations
     *            the affiliations to set
     */
    public void setOrgAffiliationRelations(SortedSet<OrgAffiliationRelationEntity> affiliations) {
        this.orgAffiliationRelations = affiliations;
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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = PROFILE, orphanRemoval = true)
    @Sort(type = SortType.COMPARATOR, comparator = ProfileFundingEntityDisplayIndexComparatorDesc.class)
    public SortedSet<ProfileFundingEntity> getProfileFunding() {
        return profileFunding;
    }

    /**
     * @param grants
     *            the grants to set
     */
    public void setProfileFunding(SortedSet<ProfileFundingEntity> funding) {
        this.profileFunding = funding;
    }

    /**
     * @return the peer reviews
     * */
    @OneToMany(mappedBy = PROFILE, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Sort(type = SortType.COMPARATOR, comparator = PeerReviewEntityDisplayIndexComparatorDesc.class)
    public SortedSet<PeerReviewEntity> getPeerReviews() {
        return peerReviews;
    }

    /**
     * @param peerReviews
     *            the peer reviews set
     * */
    public void setPeerReviews(SortedSet<PeerReviewEntity> peerReviews) {
        this.peerReviews = peerReviews;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public Set<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    /**
     * @return the researcherUrls
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
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

    @Column(name = "account_expiry")
    public Date getAccountExpiry() {
        return accountExpiry;
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
        return recordLocked != null ? !recordLocked : Boolean.FALSE;
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

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_orcid")
    @Sort(type = SortType.COMPARATOR, comparator = OrcidEntityIdComparator.class)
    public SortedSet<ClientDetailsEntity> getClients() {
        return clients;
    }

    public void setClients(SortedSet<ClientDetailsEntity> clients) {
        this.clients = clients;
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
    @Column(name = "enable_developer_tools")
    public Boolean getEnableDeveloperTools() {
        return this.enableDeveloperTools;
    }

    public void setEnableDeveloperTools(boolean enableDeveloperTools) {
        this.enableDeveloperTools = enableDeveloperTools;
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
    
    @Column(name = "deprecated_method")
    public String getDeprecatedMethod() {
        return deprecatedMethod;
    }

    public void setDeprecatedMethod(String deprecatedMethod) {
        this.deprecatedMethod = deprecatedMethod;
    }

    @Column(name = "deprecating_admin")
    public String getDeprecatingAdmin() {
        return deprecatingAdmin;
    }

    public void setDeprecatingAdmin(String deprecatingAdmin) {
        this.deprecatingAdmin = deprecatingAdmin;
    }

    @Column(name = "salesforce_id")
    public String getSalesforeId() {
        return salesforeId;
    }

    public void setSalesforeId(String salesforeId) {
        this.salesforeId = salesforeId;
    }

    @Column(name = "developer_tools_enabled_date")
    public Date getDeveloperToolsEnabledDate() {
        return developerToolsEnabledDate;
    }

    public void setDeveloperToolsEnabledDate(Date developerToolsEnabledDate) {
        this.developerToolsEnabledDate = developerToolsEnabledDate;
    }

    @Column(name = "used_captcha_on_registration")
    public Boolean getUsedRecaptchaOnRegistration() {
        return usedRecaptchaOnRegistration;
    }

    public void setUsedRecaptchaOnRegistration(Boolean usedRecaptchaOnRegistration) {
        this.usedRecaptchaOnRegistration = usedRecaptchaOnRegistration;
    }
    
    @Column(name = "activities_visibility_default")
    public String getActivitiesVisibilityDefault() {
        return activitiesVisibilityDefault;
    }

    public void setActivitiesVisibilityDefault(String activitesVisibilityDefault) {
        this.activitiesVisibilityDefault = activitesVisibilityDefault;
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

    @Column(name = "locale")
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Column(name = "referred_by")
    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    @Column(name = "user_last_ip")
    public String getUserLastIp() {
        return userLastIp;
    }

    public void setUserLastIp(String userLastIp) {
        this.userLastIp = userLastIp;
    }

    @Column(name = "reviewed")
    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    /**
     * Generates a string that will be used for caching proposes
     * 
     * @param profile
     * @return a string containing the orcid id and the last modified day,
     *         concatenated by '_'
     * */
    @Transient
    public String getCacheKey() {
        String orcid = this.getId();
        Date lastModified = this.getLastModified() == null ? new Date() : this.getLastModified();
        String lastModifiedString = DateUtils.convertToXMLGregorianCalendar(lastModified).toXMLFormat();
        return StringUtils.join(new String[] { orcid, lastModifiedString }, "_");
    }

    @OneToOne(mappedBy = "profile", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})    
    public RecordNameEntity getRecordNameEntity() {
        return recordNameEntity;
    }

    public void setRecordNameEntity(RecordNameEntity recordNameEntity) {
        this.recordNameEntity = recordNameEntity;
    }

    @OneToOne(mappedBy = "profile", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})        
    public BiographyEntity getBiographyEntity() {
        return biographyEntity; 
    }

    public void setBiographyEntity(BiographyEntity biographyEntity) {
        this.biographyEntity = biographyEntity;
    }

    @Column(name="last_login")
    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * @return the peer reviews
     * */
    @OneToMany(mappedBy = PROFILE, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)//TODO: ?  this seems like it might need a display id comparator.
    public SortedSet<ResearchResourceEntity> getResearchResource() {
        return researchResources;
    }

    /**
     * @param peerReviews
     *            the peer reviews set
     * */
    public void setResearchResource(SortedSet<ResearchResourceEntity> researchResources) {
        this.researchResources = researchResources;
    }
}
