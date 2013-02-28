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
package org.orcid.core.utils;

import schema.constants.SolrConstants;

/**
 * Wrapper class for SolrQueryBuilder that encapsulates the fields named in the
 * Solr schema and exposes strongly typed methods. This allows the
 * SolrQueryBuilder to be re-used in a generic fashion
 * 
 * @See {@link SolrQueryBuilder}
 * @author jamesb
 * 
 */
public class OrcidSolrQueryBuilder {

    private SolrQueryBuilder solrQueryBuilder;
    private static final String GIVEN_NAME_SOLR_FIELD = SolrConstants.GIVEN_NAMES;
    private static final String OTHER_NAME_SOLR_FIELD = SolrConstants.OTHER_NAMES;
    private static final String FAMILY_NAME_SOLR_FIELD = SolrConstants.FAMILY_NAME;
    private static final String PRIMARY_INST_NAME_SOLR_FIELD = SolrConstants.AFFILIATE_PRIMARY_INSTITUTION_NAMES;
    private static final String PAST_INST_NAME_SOLR_FIELD = SolrConstants.AFFILIATE_PAST_INSTITUTION_NAMES;
    private static final String KEYWORD_FIELD = SolrConstants.KEYWORDS;
    private static final String TEXT_FIELD = SolrConstants.TEXT;

    public OrcidSolrQueryBuilder() {
        solrQueryBuilder = new SolrQueryBuilder();
    }

    /**
     * Get the current query string based on the parameters passed sequentially
     * to the builder
     * 
     * @return
     */
    public String retrieveQuery() {
        return solrQueryBuilder.retrieveQuery();
    }

    public boolean isEmpty() {
        return solrQueryBuilder.isEmpty();
    }

    public void addGivenNameToQuery(String givenName) {
        solrQueryBuilder.appendFieldValuePair(GIVEN_NAME_SOLR_FIELD, givenName);
    }

    public void addGivenNameAsLowercaseWildcardToQuery(String givenName) {
        solrQueryBuilder.appendFieldValuePairAsLowercaseWildcard(GIVEN_NAME_SOLR_FIELD, givenName);
    }

    public void addFamilyNameToQuery(String familyName) {
        solrQueryBuilder.appendFieldValuePair(FAMILY_NAME_SOLR_FIELD, familyName);
    }

    public void addFamilyNameAsLowercaseWildcardToQuery(String givenName) {
        solrQueryBuilder.appendFieldValuePairAsLowercaseWildcard(FAMILY_NAME_SOLR_FIELD, givenName);
    }

    public void addFamilyNameToQueryAsLowerCaseWildcardANDOperation(String familyName) {
        solrQueryBuilder.appendLowercaseWildcardANDCondition(FAMILY_NAME_SOLR_FIELD, familyName);
    }

    public void addPrimaryInstitution(String institutionName) {
        solrQueryBuilder.appendFieldValuePair(PRIMARY_INST_NAME_SOLR_FIELD, institutionName);
    }

    public void addPrimaryInstitutionAsLowercaseWildcard(String institutionName) {
        solrQueryBuilder.appendFieldValuePairAsLowercaseWildcard(PRIMARY_INST_NAME_SOLR_FIELD, institutionName);
    }

    public void addPrimaryInstitutionAsANDOperation(String institutionName) {
        solrQueryBuilder.appendANDCondition(PRIMARY_INST_NAME_SOLR_FIELD, institutionName);
    }

    public void addPrimaryInstitutionAsLowercaseWildcardANDOperation(String institutionName) {
        solrQueryBuilder.appendLowercaseWildcardANDCondition(PRIMARY_INST_NAME_SOLR_FIELD, institutionName);
    }

    public void addOtherNamesAsOROperation(String otherName) {
        solrQueryBuilder.appendORCondition(OTHER_NAME_SOLR_FIELD, otherName);
    }

    public void addOtherNamesLowercaseWildcardOROperation(String otherName) {
        solrQueryBuilder.appendLowercaseWildcardORCondition(OTHER_NAME_SOLR_FIELD, otherName);
    }

    public void addPastInstitutionNamesAsOROperation(String institutionName) {
        solrQueryBuilder.appendORCondition(PAST_INST_NAME_SOLR_FIELD, institutionName);
    }

    public void addPastInstitutionNamesAsLowercaseWildcardOROperation(String institutionName) {
        solrQueryBuilder.appendLowercaseWildcardORCondition(PAST_INST_NAME_SOLR_FIELD, institutionName);
    }

    public void addSingleKeywordAsLowercaseWildcard(String keyword) {
        solrQueryBuilder.appendFieldValuePairAsLowercaseWildcard(KEYWORD_FIELD, keyword);
    }

    public void addSingleKeywordAsLowercaseWildcardANDCondition(String keyword) {
        solrQueryBuilder.appendLowercaseWildcardANDCondition(KEYWORD_FIELD, keyword);
    }

    public void addTextAsLowercaseWildcard(String text) {
        solrQueryBuilder.appendFieldValuePairAsLowercaseWildcard(TEXT_FIELD, text);
    }

    public void addTextAsLowercaseWildcardANDCondition(String text) {
        solrQueryBuilder.appendLowercaseWildcardANDCondition(TEXT_FIELD, text);
    }
}
