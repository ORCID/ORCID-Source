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
package org.orcid.persistence.jpa.entities;

import static org.orcid.utils.NullUtils.compareObjectsNullSafe;

import java.math.BigDecimal;
import java.util.SortedSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.utils.NullUtils;

/**
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntity
 * 
 * @author Declan Newman (declan) Angel Montenegro
 */

@Entity
@Table(name = "profile_funding")
public class ProfileFundingEntity extends BaseEntity<Long> implements Comparable<ProfileFundingEntity>, ProfileAware, SourceAware, DisplayIndexInterface {

    private static final long serialVersionUID = -3187757614938904392L;

    private static final String PROFILE_FUNDING = "profileFunding";

    private Long id;
    private OrgEntity org;
    private ProfileEntity profile;
    private String title;
    private String translatedTitle;
    private String translatedTitleLanguageCode;
    private String description;
    private FundingType type;
    private String organizationDefinedType;
    private String currencyCode;
    private String amount;
    private String url;
    private String contributorsJson;
    private String externalIdentifiersJson;
    private StartDateEntity startDate;
    private EndDateEntity endDate;
    private Visibility visibility;
    private SortedSet<FundingExternalIdentifierEntity> externalIdentifiers;
    private SourceEntity source;
    private BigDecimal numericAmount;
    private Long displayIndex; 

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "profile_funding_seq")
    @SequenceGenerator(name = "profile_funding_seq", sequenceName = "profile_funding_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "org_id", nullable = false)
    public OrgEntity getOrg() {
        return org;
    }

    public void setOrg(OrgEntity org) {
        this.org = org;
    }

    @Override
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "translated_title")
    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    @Column(name = "translated_title_language_code", length = 25)
    public String getTranslatedTitleLanguageCode() {
        return translatedTitleLanguageCode;
    }

    public void setTranslatedTitleLanguageCode(String translatedTitleLanguageCode) {
        this.translatedTitleLanguageCode = translatedTitleLanguageCode;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public FundingType getType() {
        return type;
    }

    public void setType(FundingType type) {
        this.type = type;
    }

    @Basic
    @Column(name = "organization_defined_type")
    public String getOrganizationDefinedType() {
        return organizationDefinedType;
    }

    public void setOrganizationDefinedType(String organizationDefinedType) {
        this.organizationDefinedType = organizationDefinedType;
    }

    @Basic
    @Column(name = "currency_code")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    public StartDateEntity getStartDate() {
        return startDate;
    }

    public void setStartDate(StartDateEntity startDate) {
        this.startDate = startDate;
    }

    public EndDateEntity getEndDate() {
        return endDate;
    }

    public void setEndDate(EndDateEntity endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = PROFILE_FUNDING, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Sort(type = SortType.NATURAL)
    public SortedSet<FundingExternalIdentifierEntity> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(SortedSet<FundingExternalIdentifierEntity> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    @Column(name = "numeric_amount")
    public BigDecimal getNumericAmount() {
        return numericAmount;
    }

    public void setNumericAmount(BigDecimal numericAmount) {
        this.numericAmount = numericAmount;
    }
    
    /*
     * Dictates the display order for works (and versions of funding)
     * works with higher numbers should be displayed first. 
     *
     * Currently only only updatable via ProfileWorkDaoImpl.updateToMaxDisplay
     *
     */    
    @Column(name = "display_index", updatable=false, insertable=false)
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
    
    @Column(name = "external_identifiers_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    @Override
    public int compareTo(ProfileFundingEntity other) {
        if (other == null) {
            return 1;
        }

        int compareTypes = compareTypes(type, other.getType());
        if (compareTypes != 0) {
            return compareTypes;
        }

        int compareTitles = compareStrings(title, other.getTitle());
        if (compareTitles != 0) {
            return compareTitles;
        }

        int compareDescriptions = compareStrings(description, other.getDescription());
        if (compareDescriptions != 0) {
            return compareDescriptions;
        }

        int compareEnds = compareEnds(endDate, other.getEndDate());
        if (compareEnds != 0) {
            return compareEnds;
        }
        int compareStarts = compareStarts(startDate, other.getStartDate());
        if (compareStarts != 0) {
            return compareStarts;
        }

        int compareAmounts = compareStrings(amount, other.getAmount());
        if (compareAmounts != 0) {
            return compareAmounts;
        }

        int compareNumericAmounts = compareObjectsNullSafe(numericAmount, other.getNumericAmount());
        if (compareNumericAmounts != 0) {
            return compareNumericAmounts;
        }

        int compareCurrency = compareStrings(currencyCode, other.getCurrencyCode());
        if (compareCurrency != 0) {
            return compareCurrency;
        }

        int compareOrgName = compareStrings(org.getName(), other.getOrg().getName());
        if (compareOrgName != 0) {
            return compareOrgName;
        }

        int compareOrgCountry = compareStrings(org.getCountry() == null ? null : org.getCountry().value(), other.getOrg().getCountry() == null ? null : other.getOrg()
                .getCountry().value());
        if (compareOrgCountry != 0) {
            return compareOrgCountry;
        }

        int compareOrgCity = compareStrings(org.getCity(), other.getOrg().getCity());
        if (compareOrgCity != 0) {
            return compareOrgCity;
        }

        int compareDisplayIndex = compareLongs(displayIndex, other.displayIndex);
        if (compareDisplayIndex != 0) {
            return compareDisplayIndex;
        }

        int compareExternalIds = compareStrings(externalIdentifiersJson, other.getExternalIdentifiersJson());
        if (compareExternalIds != 0)
            return compareExternalIds;
        
        return compareStrings(url, other.getUrl());
    }

    private int compareTypes(FundingType type, FundingType otherType) {
        if (NullUtils.anyNull(type, otherType)) {
            return -NullUtils.compareNulls(type, otherType);
        }
        return -type.compareTo(otherType);
    }

    private int compareEnds(FuzzyDateEntity endDate, FuzzyDateEntity otherEndDate) {
        if (NullUtils.anyNull(endDate, otherEndDate)) {
            return -NullUtils.compareNulls(endDate, otherEndDate);
        }
        return -endDate.compareTo(otherEndDate);
    }

    private int compareStarts(FuzzyDateEntity startDate, FuzzyDateEntity otherStartDate) {
        if (NullUtils.anyNull(startDate, otherStartDate)) {
            return NullUtils.compareNulls(startDate, otherStartDate);
        }
        return -startDate.compareTo(otherStartDate);
    }

    private int compareStrings(String string, String otherString) {
        if (NullUtils.anyNull(string, otherString)) {
            return NullUtils.compareNulls(string, otherString);
        }
        return string.compareTo(otherString);
    }
    
    private int compareLongs(Long l1, Long l2 ) {
        if (NullUtils.anyNull(l1, l2)) {
            return NullUtils.compareNulls(l1, l2);
        }
        return l1.compareTo(l2);
    }
    

    /**
     * Clean simple fields so that entity can be reused.
     */
    public void clean() {
        type = null;
        title = null;
        translatedTitle = null;
        translatedTitleLanguageCode = null;
        startDate = null;
        endDate = null;
        visibility = null;
        description = null;
        currencyCode = null;
        amount = null;
        numericAmount = null;
        contributorsJson = null;
        url = null;
        displayIndex = null;
        externalIdentifiersJson = null;
    }
    

}
