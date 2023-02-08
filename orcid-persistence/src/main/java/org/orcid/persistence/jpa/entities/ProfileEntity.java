package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Transient;

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
    
    // Security fields
    private String encryptedPassword;
    private Date accountExpiry;
    private Boolean recordLocked = Boolean.FALSE;
    private String reasonLocked;
    private String reasonLockedDescription;
    private Date recordLockedDate;
    private String recordLockingAdmin;    
    private Boolean enabled = Boolean.TRUE;
    private String referredBy;
    private Date lastLogin;
    private Date signinLockStart;
    private Date signinLockLastAttempt;
    private Integer signinLockCount;

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
    private String locale = DEFAULT_LOCALE;
    
    private IndexingStatus indexingStatus = IndexingStatus.PENDING;
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
     * @return the recordLockedDate
     */
    @Column(name = "record_locked_date")
    public Date getRecordLockedDate() {
        return recordLockedDate;
    }
    
    /**
     * @param recordLockedDate
     *            a timestamp of when the record was locked
     */
    public void setRecordLockedDate(Date recordLockedDate) {
        this.recordLockedDate = recordLockedDate;
    }
    
    /**
     * @return the recordLockingAdmin
     */
    @Column(name = "record_locked_admin_id")
    public String getRecordLockingAdmin() {
        return recordLockingAdmin;
    }
    
    /**
     * @param recordLockingAdmin
     *            the iD of the admin who locked the record
     */
    public void setRecordLockingAdmin(String recordLockingAdmin) {
        this.recordLockingAdmin = recordLockingAdmin;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orcid")
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

    /**
     * @param accountExpiry
     *            the accountExpiry to set
     */
    public void setAccountExpiry(Date accountExpiry) {
        this.accountExpiry = accountExpiry;
    }

    @Transient
    public boolean isEnabled() {
        return enabled != null ? enabled : Boolean.TRUE;
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

    @Column(name="last_login")
    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /*************** SIGNIN LOCK COLUMNS ***************
     * 
     *     private Date signinLockDate;
    private Date signinLockLastAttempt;
    private Integer signinLockCount;
     */
    
    /**
     * @return the signinLockDate
     */
    @Column(name = "signin_lock_start")
    public Date getSigninLockStart() {
        return signinLockStart;
    }
    
    /**
     * @param signinLockDate
     *            a timestamp of when signin lock window started
     */
    public void setSigninLockStart(Date signinLockStart) {
        this.signinLockStart = signinLockStart;
    }
    
    /**
     * @return the signinLockLastAttempt
     */
    @Column(name = "signin_lock_last_attempt")
    public Date getSigninLockLastAttempt() {
        return signinLockLastAttempt;
    }
    
    /**
     * @param signinLockLastAttempt
     *            a timestamp of when signin last failed
     */
    public void setSigninLockLastAttempt(Date signinLockLastAttempt) {
        this.signinLockLastAttempt = signinLockLastAttempt;
    }
    
    /**
     * @return the signinLockCount
     */
    @Column(name = "signin_lock_count")
    public Integer getSigninLockCount() {
        return signinLockCount;
    }
    
    /**
     * @param signinLockCount
     *            a timestamp of when signin lock window started
     */
    public void setSigninLockCount(Integer signinLockCount) {
        this.signinLockCount = signinLockCount;
    }
}
