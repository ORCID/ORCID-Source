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
package org.orcid.persistence.solr.entities;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.solr.client.solrj.beans.Field;

import schema.constants.SolrConstants;

/**
 * Class representing public information that can be returned from the Solr
 * persistence level. Used by OrcidSearchManager as an abstraction of the
 * results from Solr.
 * 
 * @see
 * @author jamesb
 * 
 */

public class OrcidSolrDocument {

    @Field
    private String orcid;

    @Field(SolrConstants.EMAIL_ADDRESS)
    private String emailAddress;

    @Field(SolrConstants.FAMILY_NAME)
    private String familyName;

    @Field(SolrConstants.GIVEN_NAMES)
    private String givenNames;

    @Field(SolrConstants.AFFILIATE_PAST_INSTITUTION_NAMES)
    private List<String> affiliatePastInstitutionNames;

    @Field(SolrConstants.AFFILIATE_PRIMARY_INSTITUTION_NAMES)
    private List<String> affiliatePrimaryInstitutionNames;

    @Field(SolrConstants.AFFILIATE_INSTITUTION_NAME)
    private List<String> affiliateInstitutionNames;

    @Field(SolrConstants.CREDIT_NAME)
    private String creditName;

    @Field(SolrConstants.OTHER_NAMES)
    private List<String> otherNames;

    @Field(SolrConstants.EXTERNAL_ID_ORCIDS)
    private List<String> externalIdOrcids;

    @Field(SolrConstants.EXTERNAL_ID_REFERENCES)
    private List<String> externalIdReferences;

    @Field(SolrConstants.EXTERNAL_ID_ORCIDS_AND_REFERENCES)
    private List<String> externalIdOrcidsAndReferences;

    @Field(SolrConstants.DIGITAL_OBJECT_IDS)
    private List<String> digitalObjectIds;

    @Field(SolrConstants.WORK_TITLES)
    private List<String> workTitles;

    @Field(SolrConstants.KEYWORDS)
    private List<String> keywords;

    @Field(SolrConstants.GRANT_NUMBERS)
    private List<String> grantNumbers;

    @Field(SolrConstants.PATENT_NUMBERS)
    private List<String> patentNumbers;

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public List<String> getPastInstitutionNames() {
        return affiliatePastInstitutionNames;
    }

    public void setPastInstitutionNames(List<String> pastInstitutionNames) {
        this.affiliatePastInstitutionNames = pastInstitutionNames;
    }

    public List<String> getAffiliateInstitutionNames() {
        return affiliateInstitutionNames;
    }

    public void setAffiliateInstitutionNames(List<String> affiliateInstitutionNames) {
        this.affiliateInstitutionNames = affiliateInstitutionNames;
    }

    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    public List<String> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(List<String> otherNames) {
        this.otherNames = otherNames;
    }

    public List<String> getExternalIdOrcids() {
        return externalIdOrcids;
    }

    public void setExternalIdOrcids(List<String> externalIdOrcids) {
        this.externalIdOrcids = externalIdOrcids;
    }

    public List<String> getExternalIdReferences() {
        return externalIdReferences;
    }

    public void setExternalIdReferences(List<String> externalIdReferences) {
        this.externalIdReferences = externalIdReferences;
    }

    public List<String> getExternalIdOrcidsAndReferences() {
        return externalIdOrcidsAndReferences;
    }

    public void setExternalIdOrcidsAndReferences(List<String> externalIdOrcidsAndReferences) {
        this.externalIdOrcidsAndReferences = externalIdOrcidsAndReferences;
    }

    public List<String> getDigitalObjectIds() {
        return digitalObjectIds;
    }

    public void setDigitalObjectIds(List<String> digitalObjectIds) {
        this.digitalObjectIds = digitalObjectIds;
    }

    public List<String> getWorkTitles() {
        return workTitles;
    }

    public void setWorkTitles(List<String> workTitles) {
        this.workTitles = workTitles;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<String> getGrantNumbers() {
        return grantNumbers;
    }

    public void setGrantNumbers(List<String> grantNumbers) {
        this.grantNumbers = grantNumbers;
    }

    public List<String> getPatentNumbers() {
        return patentNumbers;
    }

    public void setPatentNumbers(List<String> patentNumbers) {
        this.patentNumbers = patentNumbers;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((affiliateInstitutionNames == null) ? 0 : affiliateInstitutionNames.hashCode());
        result = prime * result + ((affiliatePastInstitutionNames == null) ? 0 : affiliatePastInstitutionNames.hashCode());
        result = prime * result + ((affiliatePrimaryInstitutionNames == null) ? 0 : affiliatePrimaryInstitutionNames.hashCode());
        result = prime * result + ((creditName == null) ? 0 : creditName.hashCode());
        result = prime * result + ((digitalObjectIds == null) ? 0 : digitalObjectIds.hashCode());
        result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        result = prime * result + ((externalIdOrcids == null) ? 0 : externalIdOrcids.hashCode());
        result = prime * result + ((externalIdOrcidsAndReferences == null) ? 0 : externalIdOrcidsAndReferences.hashCode());
        result = prime * result + ((externalIdReferences == null) ? 0 : externalIdReferences.hashCode());
        result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
        result = prime * result + ((givenNames == null) ? 0 : givenNames.hashCode());
        result = prime * result + ((grantNumbers == null) ? 0 : grantNumbers.hashCode());
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((otherNames == null) ? 0 : otherNames.hashCode());
        result = prime * result + ((patentNumbers == null) ? 0 : patentNumbers.hashCode());
        result = prime * result + ((workTitles == null) ? 0 : workTitles.hashCode());
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
        OrcidSolrDocument other = (OrcidSolrDocument) obj;
        if (affiliateInstitutionNames == null) {
            if (other.affiliateInstitutionNames != null)
                return false;
        } else if (!affiliateInstitutionNames.equals(other.affiliateInstitutionNames))
            return false;
        if (affiliatePastInstitutionNames == null) {
            if (other.affiliatePastInstitutionNames != null)
                return false;
        } else if (!affiliatePastInstitutionNames.equals(other.affiliatePastInstitutionNames))
            return false;
        if (affiliatePrimaryInstitutionNames == null) {
            if (other.affiliatePrimaryInstitutionNames != null)
                return false;
        } else if (!affiliatePrimaryInstitutionNames.equals(other.affiliatePrimaryInstitutionNames))
            return false;
        if (creditName == null) {
            if (other.creditName != null)
                return false;
        } else if (!creditName.equals(other.creditName))
            return false;
        if (digitalObjectIds == null) {
            if (other.digitalObjectIds != null)
                return false;
        } else if (!digitalObjectIds.equals(other.digitalObjectIds))
            return false;
        if (emailAddress == null) {
            if (other.emailAddress != null)
                return false;
        } else if (!emailAddress.equals(other.emailAddress))
            return false;
        if (externalIdOrcids == null) {
            if (other.externalIdOrcids != null)
                return false;
        } else if (!externalIdOrcids.equals(other.externalIdOrcids))
            return false;
        if (externalIdOrcidsAndReferences == null) {
            if (other.externalIdOrcidsAndReferences != null)
                return false;
        } else if (!externalIdOrcidsAndReferences.equals(other.externalIdOrcidsAndReferences))
            return false;
        if (externalIdReferences == null) {
            if (other.externalIdReferences != null)
                return false;
        } else if (!externalIdReferences.equals(other.externalIdReferences))
            return false;
        if (familyName == null) {
            if (other.familyName != null)
                return false;
        } else if (!familyName.equals(other.familyName))
            return false;
        if (givenNames == null) {
            if (other.givenNames != null)
                return false;
        } else if (!givenNames.equals(other.givenNames))
            return false;
        if (grantNumbers == null) {
            if (other.grantNumbers != null)
                return false;
        } else if (!grantNumbers.equals(other.grantNumbers))
            return false;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        } else if (!keywords.equals(other.keywords))
            return false;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (otherNames == null) {
            if (other.otherNames != null)
                return false;
        } else if (!otherNames.equals(other.otherNames))
            return false;
        if (patentNumbers == null) {
            if (other.patentNumbers != null)
                return false;
        } else if (!patentNumbers.equals(other.patentNumbers))
            return false;
        if (workTitles == null) {
            if (other.workTitles != null)
                return false;
        } else if (!workTitles.equals(other.workTitles))
            return false;
        return true;
    }

    public List<String> getAffiliatePastInstitutionNames() {
        return affiliatePastInstitutionNames;
    }

    public void setAffiliatePastInstitutionNames(List<String> affiliatePastInstitutionNames) {
        this.affiliatePastInstitutionNames = affiliatePastInstitutionNames;
    }

    public List<String> getAffiliatePrimaryInstitutionNames() {
        return affiliatePrimaryInstitutionNames;
    }

    public void setAffiliatePrimaryInstitutionNames(List<String> affiliatePrimaryInstitutionNames) {
        this.affiliatePrimaryInstitutionNames = affiliatePrimaryInstitutionNames;
    }

}