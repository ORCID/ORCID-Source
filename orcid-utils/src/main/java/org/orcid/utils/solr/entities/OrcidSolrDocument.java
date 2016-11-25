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
package org.orcid.utils.solr.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.solr.client.solrj.beans.Field;

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
    private List<String> emailAddresses;

    @Field(SolrConstants.FAMILY_NAME)
    private String familyName;

    @Field(SolrConstants.GIVEN_NAMES)
    private String givenNames;

    @Field(SolrConstants.CREDIT_NAME)
    private String creditName;

    @Field(SolrConstants.OTHER_NAMES)
    private List<String> otherNames;

    @Field(SolrConstants.EXTERNAL_ID_SOURCE)
    private List<String> externalIdSources;

    @Field(SolrConstants.EXTERNAL_ID_REFERENCES)
    private List<String> externalIdReferences;

    @Field(SolrConstants.EXTERNAL_ID_SOURCE_AND_REFERENCES)
    private List<String> externalIdSourcesAndReferences;

    @Field(SolrConstants.DIGITAL_OBJECT_IDS)
    private List<String> digitalObjectIds = new ArrayList<String>();;

    @Field(SolrConstants.AGR)
    private List<String> agr = new ArrayList<String>();

    @Field(SolrConstants.ARXIV)
    private List<String> arxiv = new ArrayList<String>();

    @Field(SolrConstants.ASIN)
    private List<String> asin = new ArrayList<String>();

    @Field(SolrConstants.ASIN_TLD)
    private List<String> asintld = new ArrayList<String>();

    @Field(SolrConstants.BIBCODE)
    private List<String> bibcode = new ArrayList<String>();

    @Field(SolrConstants.CBA)
    private List<String> cba = new ArrayList<String>();

    @Field(SolrConstants.CIT)
    private List<String> cit = new ArrayList<String>();

    @Field(SolrConstants.CTX)
    private List<String> ctx = new ArrayList<String>();
    
    @Field(SolrConstants.EID)
    private List<String> eid = new ArrayList<String>();
    
    @Field(SolrConstants.ETHOS)
    private List<String> ethos = new ArrayList<String>();

    @Field(SolrConstants.HANDLE)
    private List<String> handle = new ArrayList<String>();

    @Field(SolrConstants.HIR)
    private List<String> hir = new ArrayList<String>();

    @Field(SolrConstants.ISBN)
    private List<String> isbn = new ArrayList<String>();

    @Field(SolrConstants.ISSN)
    private List<String> issn = new ArrayList<String>();

    @Field(SolrConstants.JFM)
    private List<String> jfm = new ArrayList<String>();

    @Field(SolrConstants.JSTOR)
    private List<String> jstor = new ArrayList<String>();

    @Field(SolrConstants.LCCN)
    private List<String> lccn = new ArrayList<String>();

    @Field(SolrConstants.MR)
    private List<String> mr = new ArrayList<String>();

    @Field(SolrConstants.OCLC)
    private List<String> oclc = new ArrayList<String>();

    @Field(SolrConstants.OL)
    private List<String> ol = new ArrayList<String>();

    @Field(SolrConstants.OSTI)
    private List<String> osti = new ArrayList<String>();

    @Field(SolrConstants.PAT)
    private List<String> pat = new ArrayList<String>();

    @Field(SolrConstants.PMC)
    private List<String> pmc = new ArrayList<String>();

    @Field(SolrConstants.PMID)
    private List<String> pmid = new ArrayList<String>();

    @Field(SolrConstants.RFC)
    private List<String> rfc = new ArrayList<String>();

    @Field(SolrConstants.SOURCE_WORK_ID)
    private List<String> sourceWorkId = new ArrayList<String>();
    
    @Field(SolrConstants.SSRN)
    private List<String> ssrn = new ArrayList<String>();
    
    @Field(SolrConstants.URI)
    private List<String> uri = new ArrayList<String>();

    @Field(SolrConstants.URN)
    private List<String> urn = new ArrayList<String>();

    @Field(SolrConstants.WOSUID)
    private List<String> wosuid = new ArrayList<String>();

    @Field(SolrConstants.ZBL)
    private List<String> zbl = new ArrayList<String>();

    @Field(SolrConstants.OTHER_IDENTIFIER_TYPE)
    private List<String> otherIdentifierType = new ArrayList<String>();

    @Field(SolrConstants.WORK_TITLES)
    private List<String> workTitles;

    @Field(SolrConstants.KEYWORDS)
    private List<String> keywords;

    @Field(SolrConstants.GRANT_NUMBERS)
    private List<String> grantNumbers;

    @Field(SolrConstants.FUNDING_TITLES)
    private List<String> fundingTitles;

    @Field(SolrConstants.PATENT_NUMBERS)
    private List<String> patentNumbers;

    @Field(SolrConstants.PROFILE_SUBMISSION_DATE)
    private Date profileSubmissionDate;
    
    @Field(SolrConstants.PROFILE_LAST_MODIFIED_DATE)
    private Date profileLastModifiedDate;

    @Field(SolrConstants.PUBLIC_PROFILE)
    private String publicProfileMessage;

    @Field(SolrConstants.GIVEN_AND_FAMILY_NAMES)
    private String givenAndFamilyNames;

    @Field(SolrConstants.PRIMARY_RECORD)
    private String primaryRecord;
    
    //e.g. doi-self
    @Field("*"+SolrConstants.DYNAMIC_SELF)
    private Map<String, List<String>> selfIds;

    //e.g. arxiv-part-of
    @Field("*"+SolrConstants.DYNAMIC_PART_OF)
    private Map<String, List<String>> partOfIds;
    
    //e.g. ringgold-org-id
    @Field("*"+SolrConstants.DYNAMIC_ORGANISATION_ID)
    private Map<String, List<String>> organisationIds;
    
    //e.g. education-org-name
    @Field("*"+SolrConstants.DYNAMIC_ORGANISATION_NAME)
    private Map<String, List<String>> organisationNames;

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
        generateCombinedGivenAndFamilyNames();
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
        generateCombinedGivenAndFamilyNames();
    }

    public String getGivenAndFamilyNames() {
        return givenAndFamilyNames;
    }

    public void setGivenAndFamilyNames(String givenAndFamilyNames) {
        this.givenAndFamilyNames = givenAndFamilyNames;
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

    public List<String> getExternalIdReferences() {
        return externalIdReferences;
    }

    public void setExternalIdReferences(List<String> externalIdReferences) {
        this.externalIdReferences = externalIdReferences;
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

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public void addEmailAddress(String emailAddress) {
        if (emailAddresses == null) {
            emailAddresses = new ArrayList<>();
        }
        emailAddresses.add(emailAddress);
    }

    public List<String> getGrantNumbers() {
        return grantNumbers;
    }

    public void setGrantNumbers(List<String> grantNumbers) {
        this.grantNumbers = grantNumbers;
    }

    public List<String> getFundingTitles() {
        return fundingTitles;
    }

    public void setFundingTitles(List<String> fundingTitles) {
        this.fundingTitles = fundingTitles;
    }

    public List<String> getPatentNumbers() {
        return patentNumbers;
    }

    public void setPatentNumbers(List<String> patentNumbers) {
        this.patentNumbers = patentNumbers;
    }

    public Date getProfileSubmissionDate() {
        return profileSubmissionDate;
    }

    public void setProfileSubmissionDate(Date profileSubmissionDate) {
        this.profileSubmissionDate = profileSubmissionDate;
    }

    public Date getProfileLastModifiedDate() {
        return profileLastModifiedDate;
    }

    public void setProfileLastModifiedDate(Date profileLastModifiedDate) {
        this.profileLastModifiedDate = profileLastModifiedDate;
    }

    public String getPublicProfileMessage() {
        return publicProfileMessage;
    }

    public void setPublicProfileMessage(String publicProfileMessage) {
        this.publicProfileMessage = publicProfileMessage;
    }

    public String getPrimaryRecord() {
        return primaryRecord;
    }

    public void setPrimaryRecord(String primaryRecord) {
        this.primaryRecord = primaryRecord;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creditName == null) ? 0 : creditName.hashCode());
        result = prime * result + ((digitalObjectIds == null) ? 0 : digitalObjectIds.hashCode());
        result = prime * result + ((emailAddresses == null) ? 0 : emailAddresses.hashCode());
        result = prime * result + ((getExternalIdSources() == null) ? 0 : getExternalIdSources().hashCode());
        result = prime * result + ((getExternalIdSourcesAndReferences() == null) ? 0 : getExternalIdSourcesAndReferences().hashCode());
        result = prime * result + ((externalIdReferences == null) ? 0 : externalIdReferences.hashCode());
        result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
        result = prime * result + ((fundingTitles == null) ? 0 : fundingTitles.hashCode());
        result = prime * result + ((givenAndFamilyNames == null) ? 0 : givenAndFamilyNames.hashCode());
        result = prime * result + ((givenNames == null) ? 0 : givenNames.hashCode());
        result = prime * result + ((grantNumbers == null) ? 0 : grantNumbers.hashCode());
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((otherNames == null) ? 0 : otherNames.hashCode());
        result = prime * result + ((patentNumbers == null) ? 0 : patentNumbers.hashCode());
        result = prime * result + ((publicProfileMessage == null) ? 0 : publicProfileMessage.hashCode());
        result = prime * result + ((workTitles == null) ? 0 : workTitles.hashCode());
        result = prime * result + ((primaryRecord == null) ? 0 : primaryRecord.hashCode());
        result = prime * result + ((arxiv == null) ? 0 : arxiv.hashCode());
        result = prime * result + ((asin == null) ? 0 : asin.hashCode());
        result = prime * result + ((asintld == null) ? 0 : asintld.hashCode());
        result = prime * result + ((bibcode == null) ? 0 : bibcode.hashCode());
        result = prime * result + ((eid == null) ? 0 : eid.hashCode());
        result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
        result = prime * result + ((issn == null) ? 0 : issn.hashCode());
        result = prime * result + ((jfm == null) ? 0 : jfm.hashCode());
        result = prime * result + ((jstor == null) ? 0 : jstor.hashCode());
        result = prime * result + ((lccn == null) ? 0 : lccn.hashCode());
        result = prime * result + ((mr == null) ? 0 : mr.hashCode());
        result = prime * result + ((oclc == null) ? 0 : oclc.hashCode());
        result = prime * result + ((ol == null) ? 0 : ol.hashCode());
        result = prime * result + ((osti == null) ? 0 : osti.hashCode());
        result = prime * result + ((pmc == null) ? 0 : pmc.hashCode());
        result = prime * result + ((pmid == null) ? 0 : pmid.hashCode());
        result = prime * result + ((rfc == null) ? 0 : rfc.hashCode());
        result = prime * result + ((ssrn == null) ? 0 : ssrn.hashCode());
        result = prime * result + ((zbl == null) ? 0 : zbl.hashCode());
        result = prime * result + ((otherIdentifierType == null) ? 0 : otherIdentifierType.hashCode());
        result = prime * result + ((agr == null) ? 0 : agr.hashCode());
        result = prime * result + ((cba == null) ? 0 : cba.hashCode());
        result = prime * result + ((cit == null) ? 0 : cit.hashCode());
        result = prime * result + ((ctx == null) ? 0 : ctx.hashCode());
        result = prime * result + ((ethos == null) ? 0 : ethos.hashCode());
        result = prime * result + ((handle == null) ? 0 : handle.hashCode());
        result = prime * result + ((hir == null) ? 0 : hir.hashCode());
        result = prime * result + ((pat == null) ? 0 : pat.hashCode());
        result = prime * result + ((sourceWorkId == null) ? 0 : sourceWorkId.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        result = prime * result + ((urn == null) ? 0 : urn.hashCode());
        result = prime * result + ((wosuid == null) ? 0 : wosuid.hashCode());
        result = prime * result + ((selfIds == null) ? 0 : selfIds.hashCode());
        result = prime * result + ((partOfIds == null) ? 0 : partOfIds.hashCode());
        result = prime * result + ((organisationIds == null) ? 0 : organisationIds.hashCode());
        result = prime * result + ((organisationNames == null) ? 0 : organisationNames.hashCode());
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
        if (emailAddresses == null) {
            if (other.emailAddresses != null)
                return false;
        } else if (!emailAddresses.equals(other.emailAddresses))
            return false;
        if (getExternalIdSources() == null) {
            if (other.getExternalIdSources() != null)
                return false;
        } else if (!getExternalIdSources().equals(other.getExternalIdSources()))
            return false;
        if (getExternalIdSourcesAndReferences() == null) {
            if (other.getExternalIdSourcesAndReferences() != null)
                return false;
        } else if (!getExternalIdSourcesAndReferences().equals(other.getExternalIdSourcesAndReferences()))
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
        if (fundingTitles == null) {
            if (other.fundingTitles != null)
                return false;
        } else if (!fundingTitles.equals(other.fundingTitles))
            return false;
        if (givenAndFamilyNames == null) {
            if (other.givenAndFamilyNames != null)
                return false;
        } else if (!givenAndFamilyNames.equals(other.givenAndFamilyNames))
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
        if (publicProfileMessage == null) {
            if (other.publicProfileMessage != null)
                return false;
        } else if (!publicProfileMessage.equals(other.publicProfileMessage))
            return false;
        if (workTitles == null) {
            if (other.workTitles != null)
                return false;
        } else if (!workTitles.equals(other.workTitles))
            return false;
        if (primaryRecord == null) {
            if (other.primaryRecord != null)
                return false;
        } else if (!primaryRecord.equals(other.primaryRecord))
            return false;
        if (arxiv == null) {
            if (other.arxiv != null)
                return false;
        } else if (!arxiv.equals(other.arxiv))
            return false;
        if (asin == null) {
            if (other.asin != null)
                return false;
        } else if (!asin.equals(other.asin))
            return false;
        if (asintld == null) {
            if (other.asintld != null)
                return false;
        } else if (!asintld.equals(other.asintld))
            return false;
        if (bibcode == null) {
            if (other.bibcode != null)
                return false;
        } else if (!bibcode.equals(other.bibcode))
            return false;
        if (eid == null) {
            if (other.eid != null)
                return false;
        } else if (!eid.equals(other.eid))
            return false;
        if (isbn == null) {
            if (other.isbn != null)
                return false;
        } else if (!isbn.equals(other.isbn))
            return false;
        if (issn == null) {
            if (other.issn != null)
                return false;
        } else if (!issn.equals(other.issn))
            return false;
        if (jfm == null) {
            if (other.jfm != null)
                return false;
        } else if (!jfm.equals(other.jfm))
            return false;
        if (jstor == null) {
            if (other.jstor != null)
                return false;
        } else if (!jstor.equals(other.jstor))
            return false;
        if (lccn == null) {
            if (other.lccn != null)
                return false;
        } else if (!lccn.equals(other.lccn))
            return false;
        if (mr == null) {
            if (other.mr != null)
                return false;
        } else if (!mr.equals(other.mr))
            return false;
        if (oclc == null) {
            if (other.oclc != null)
                return false;
        } else if (!oclc.equals(other.oclc))
            return false;
        if (ol == null) {
            if (other.ol != null)
                return false;
        } else if (!ol.equals(other.ol))
            return false;
        if (osti == null) {
            if (other.osti != null)
                return false;
        } else if (!osti.equals(other.osti))
            return false;
        if (pmc == null) {
            if (other.pmc != null)
                return false;
        } else if (!pmc.equals(other.pmc))
            return false;
        if (pmid == null) {
            if (other.pmid != null)
                return false;
        } else if (!pmid.equals(other.pmid))
            return false;
        if (rfc == null) {
            if (other.rfc != null)
                return false;
        } else if (!rfc.equals(other.rfc))
            return false;
        if (ssrn == null) {
            if (other.ssrn != null)
                return false;
        } else if (!ssrn.equals(other.ssrn))
            return false;
        if (zbl == null) {
            if (other.zbl != null)
                return false;
        } else if (!zbl.equals(other.zbl))
            return false;
        if (otherIdentifierType == null) {
            if (other.otherIdentifierType != null)
                return false;
        } else if (!otherIdentifierType.equals(other.otherIdentifierType))
            return false;
        if (agr == null) {
            if (other.agr != null)
                return false;
        } else if (!agr.equals(other.agr))
            return false;
        if (cba == null) {
            if (other.cba != null)
                return false;
        } else if (!cba.equals(other.cba))
            return false;
        if (cit == null) {
            if (other.cit != null)
                return false;
        } else if (!cit.equals(other.cit))
            return false;
        if (ctx == null) {
            if (other.ctx != null)
                return false;
        } else if (!ctx.equals(other.ctx))
            return false;
        if (ethos == null) {
            if (other.ethos != null)
                return false;
        } else if (!ethos.equals(other.ethos))
            return false;
        if (handle == null) {
            if (other.handle != null)
                return false;
        } else if (!handle.equals(other.handle))
            return false;
        if (hir == null) {
            if (other.hir != null)
                return false;
        } else if (!hir.equals(other.hir))
            return false;
        if (pat == null) {
            if (other.pat != null)
                return false;
        } else if (!pat.equals(other.pat))
            return false;
        if (sourceWorkId == null) {
            if (other.sourceWorkId != null)
                return false;
        } else if (!sourceWorkId.equals(other.sourceWorkId))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        if (urn == null) {
            if (other.urn != null)
                return false;
        } else if (!urn.equals(other.urn))
            return false;
        if (wosuid == null) {
            if (other.wosuid != null)
                return false;
        } else if (!wosuid.equals(other.wosuid))
            return false;
        
        if (selfIds == null) {
            if (other.selfIds != null)
                return false;
        } else if (!selfIds.equals(other.selfIds))
            return false;
        
        if (partOfIds == null) {
            if (other.partOfIds != null)
                return false;
        } else if (!partOfIds.equals(other.partOfIds))
            return false;
        
        if (organisationIds == null) {
            if (other.organisationIds != null)
                return false;
        } else if (!organisationIds.equals(other.organisationIds))
            return false;
        
        if (organisationNames == null) {
            if (other.organisationNames != null)
                return false;
        } else if (!organisationNames.equals(other.organisationNames))
            return false;
        
        
        return true;
    }

    private void generateCombinedGivenAndFamilyNames() {
        String givenAndFamilyNames = 
                (givenNames == null) ? ((familyName == null) ? null : familyName) : (givenNames + ((familyName == null) ? "" : " " + familyName));
        setGivenAndFamilyNames(givenAndFamilyNames);
    }

    public List<String> getArxiv() {
        return arxiv;
    }

    public void setArxiv(List<String> arxiv) {
        this.arxiv = arxiv;
    }

    public List<String> getAsin() {
        return asin;
    }

    public void setAsin(List<String> asin) {
        this.asin = asin;
    }

    public List<String> getAsintld() {
        return asintld;
    }

    public void setAsintld(List<String> asintld) {
        this.asintld = asintld;
    }

    public List<String> getBibcode() {
        return bibcode;
    }

    public void setBibcode(List<String> bibcode) {
        this.bibcode = bibcode;
    }

    public List<String> getEid() {
        return eid;
    }

    public void setEid(List<String> eid) {
        this.eid = eid;
    }

    public List<String> getIsbn() {
        return isbn;
    }

    public void setIsbn(List<String> isbn) {
        this.isbn = isbn;
    }

    public List<String> getIssn() {
        return issn;
    }

    public void setIssn(List<String> issn) {
        this.issn = issn;
    }

    public List<String> getJfm() {
        return jfm;
    }

    public void setJfm(List<String> jfm) {
        this.jfm = jfm;
    }

    public List<String> getJstor() {
        return jstor;
    }

    public void setJstor(List<String> jstor) {
        this.jstor = jstor;
    }

    public List<String> getLccn() {
        return lccn;
    }

    public void setLccn(List<String> lccn) {
        this.lccn = lccn;
    }

    public List<String> getMr() {
        return mr;
    }

    public void setMr(List<String> mr) {
        this.mr = mr;
    }

    public List<String> getOclc() {
        return oclc;
    }

    public void setOclc(List<String> oclc) {
        this.oclc = oclc;
    }

    public List<String> getOl() {
        return ol;
    }

    public void setOl(List<String> ol) {
        this.ol = ol;
    }

    public List<String> getOsti() {
        return osti;
    }

    public void setOsti(List<String> osti) {
        this.osti = osti;
    }

    public List<String> getPmc() {
        return pmc;
    }

    public void setPmc(List<String> pmc) {
        this.pmc = pmc;
    }

    public List<String> getPmid() {
        return pmid;
    }

    public void setPmid(List<String> pmid) {
        this.pmid = pmid;
    }

    public List<String> getRfc() {
        return rfc;
    }

    public void setRfc(List<String> rfc) {
        this.rfc = rfc;
    }

    public List<String> getSsrn() {
        return ssrn;
    }

    public void setSsrn(List<String> ssrn) {
        this.ssrn = ssrn;
    }

    public List<String> getZbl() {
        return zbl;
    }

    public void setZbl(List<String> zbl) {
        this.zbl = zbl;
    }

    public List<String> getOtherIdentifierType() {
        return otherIdentifierType;
    }

    public void setOtherIdentifierType(List<String> otherIdentifierType) {
        this.otherIdentifierType = otherIdentifierType;
    }

    public List<String> getExternalIdSources() {
        return externalIdSources;
    }

    public void setExternalIdSources(List<String> externalIdSources) {
        this.externalIdSources = externalIdSources;
    }

    public List<String> getExternalIdSourcesAndReferences() {
        return externalIdSourcesAndReferences;
    }

    public void setExternalIdSourcesAndReferences(List<String> externalIdSourcesAndReferences) {
        this.externalIdSourcesAndReferences = externalIdSourcesAndReferences;
    }

	public List<String> getAgr() {
		return agr;
	}

	public void setAgr(List<String> agr) {
		this.agr = agr;
	}

	public List<String> getCba() {
		return cba;
	}

	public void setCba(List<String> cba) {
		this.cba = cba;
	}

	public List<String> getCit() {
		return cit;
	}

	public void setCit(List<String> cit) {
		this.cit = cit;
	}

	public List<String> getCtx() {
		return ctx;
	}

	public void setCtx(List<String> ctx) {
		this.ctx = ctx;
	}

	public List<String> getEthos() {
		return ethos;
	}

	public void setEthos(List<String> ethos) {
		this.ethos = ethos;
	}

	public List<String> getHandle() {
		return handle;
	}

	public void setHandle(List<String> handle) {
		this.handle = handle;
	}

	public List<String> getHir() {
		return hir;
	}

	public void setHir(List<String> hir) {
		this.hir = hir;
	}

	public List<String> getPat() {
		return pat;
	}

	public void setPat(List<String> pat) {
		this.pat = pat;
	}

	public List<String> getSourceWorkId() {
		return sourceWorkId;
	}

	public void setSourceWorkId(List<String> sourceWorkId) {
		this.sourceWorkId = sourceWorkId;
	}

	public List<String> getUri() {
		return uri;
	}

	public void setUri(List<String> uri) {
		this.uri = uri;
	}

	public List<String> getUrn() {
		return urn;
	}

	public void setUrn(List<String> urn) {
		this.urn = urn;
	}

	public List<String> getWosuid() {
		return wosuid;
	}

	public void setWosuid(List<String> wosuid) {
		this.wosuid = wosuid;
	}
	
    public Map<String, List<String>> getSelfIds() {
        return selfIds;
    }

    public void setSelfIds(Map<String, List<String>> selfIds) {
        this.selfIds = selfIds;
    }

    public Map<String, List<String>> getPartOfIds() {
        return partOfIds;
    }

    public void setPartOfIds(Map<String, List<String>> partOfIds) {
        this.partOfIds = partOfIds;
    }
    
    public Map<String, List<String>> getOrganisationIds() {
        return organisationIds;
    }

    public void setOrganisationIds(Map<String, List<String>> organisationIds) {
        this.organisationIds = organisationIds;
    }
    
    public Map<String, List<String>> getOrganisationNames() {
        return organisationNames;
    }

    public void setOrganisationNames(Map<String, List<String>> organisationNames) {
        this.organisationNames = organisationNames;
    }
    
    
}