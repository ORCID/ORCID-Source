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
package org.orcid.frontend.web.controllers.helper;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.utils.OrcidSolrQueryBuilder;
import org.orcid.core.utils.SolrQueryBuilder;
import org.orcid.frontend.web.forms.SearchOrcidBioForm;

/**
 * Helper class to be used by {@link SearchOrcidBioForm} to build up an
 * OrcidSolr query object. The permutations designated as valid here determine
 * which fields will be added to the query.
 * 
 * @See {@link OrcidSolrQueryBuilder}
 * @See {@link SolrQueryBuilder}
 * @See {@link OrcidSearchForm}
 * 
 * 
 * @author jamesb
 * 
 */
public class SearchOrcidSolrCriteria {

    private OrcidSolrQueryBuilder orcidSolrQueryBuilder;

    private String orcid;
    private String familyName;
    private String givenName;
    private String institutionName;
    private Boolean includeOtherNames = false;
    private Boolean isPastInstitutionsSearchable = false;
    private String keyword;
    private String text;

    public SearchOrcidSolrCriteria() {
        orcidSolrQueryBuilder = new OrcidSolrQueryBuilder();
    }

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

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Boolean getIncludeOtherNames() {
        return includeOtherNames;
    }

    public void setIncludeOtherNames(Boolean includeOtherNames) {
        this.includeOtherNames = includeOtherNames;
    }

    public Boolean getIsPastInstitutionsSearchable() {
        return isPastInstitutionsSearchable;
    }

    public void setPastInstitutionsSearchable(Boolean isPastInstitutionsSearchable) {
        this.isPastInstitutionsSearchable = isPastInstitutionsSearchable;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String deriveQueryString() {

        if (!StringUtils.isBlank(orcid)) {
            return "orcid:" + orcid;
        }

        deriveGivenNameFamilyNameClause(givenName, familyName);
        deriveOtherNamesCondition(includeOtherNames, givenName);
        derivePrimaryInstitutionName(institutionName);
        derivePastInstitutionsNamesCondition(isPastInstitutionsSearchable, institutionName);
        deriveKeywordSearchCondition(keyword);
        deriveTextSearchCondition(text);
        return orcidSolrQueryBuilder.retrieveQuery();
    }

    private void deriveGivenNameFamilyNameClause(String givenName, String familyName) {

        boolean givenNameSupplied = !StringUtils.isBlank(givenName);
        boolean familyNameSupplied = !StringUtils.isBlank(familyName);

        // when both names are supplied we want to restrict the query to match
        // on both names
        if (givenNameSupplied && familyNameSupplied) {
            orcidSolrQueryBuilder.addGivenNameAsLowercaseWildcardToQuery(givenName);
            orcidSolrQueryBuilder.addFamilyNameToQueryAsLowerCaseWildcardANDOperation(familyName);
            return;
        }

        // otherwise we just want the query to match on the value provided
        // (wildcarded*)
        if (givenNameSupplied || familyNameSupplied) {

            if (givenNameSupplied) {
                orcidSolrQueryBuilder.addGivenNameAsLowercaseWildcardToQuery(givenName);
            }

            else {
                orcidSolrQueryBuilder.addFamilyNameAsLowercaseWildcardToQuery(familyName);
            }

            return;
        }

    }

    private void derivePrimaryInstitutionName(String institutionName) {
        if (!orcidSolrQueryBuilder.isEmpty()) {
            orcidSolrQueryBuilder.addPrimaryInstitutionAsLowercaseWildcardANDOperation(institutionName);
        }

        else {
            orcidSolrQueryBuilder.addPrimaryInstitutionAsLowercaseWildcard(institutionName);
        }
    }

    private void deriveOtherNamesCondition(Boolean otherNamesSearchable, String givenName) {

        // don't do anything if not searchable
        // don't do anything if the given name not populated

        if (otherNamesSearchable && !StringUtils.isBlank(givenName)) {
            orcidSolrQueryBuilder.addOtherNamesLowercaseWildcardOROperation(givenName);
        }
    }

    private void derivePastInstitutionsNamesCondition(boolean pastInstitutionsSearchable, String institutionName) {

        // don't do anything if not searchable
        // don't do anything if the given name not populated

        if (pastInstitutionsSearchable && !StringUtils.isBlank(institutionName)) {
            orcidSolrQueryBuilder.addPastInstitutionNamesAsLowercaseWildcardOROperation(institutionName);
        }
    }

    private void deriveKeywordSearchCondition(String keyword) {

        if (StringUtils.isBlank(keyword)) {
            return;
        }
        // don't make this an AND term if this is the only term
        if (!orcidSolrQueryBuilder.isEmpty()) {
            orcidSolrQueryBuilder.addSingleKeywordAsLowercaseWildcardANDCondition(keyword);
        }

        else {
            orcidSolrQueryBuilder.addSingleKeywordAsLowercaseWildcard(keyword);
        }

    }

    private void deriveTextSearchCondition(String text) {

        if (StringUtils.isBlank(text)) {
            return;
        }
        // don't make this an AND term if this is the only term
        if (!orcidSolrQueryBuilder.isEmpty()) {
            orcidSolrQueryBuilder.addTextAsLowercaseWildcardANDCondition(text);
        }

        else {
            orcidSolrQueryBuilder.addTextAsLowercaseWildcard(text);
        }

    }

}
