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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.SearchOrcidBioForm;

/**
 * Test class to assert that the form bindings of SearchOrcidForm produce the
 * query string with the correct permutations
 * 
 * @author jamesb
 * 
 */
public class SearchOrcidFormToQueryMapperTest {

    private SearchOrcidBioForm form;

    @Before
    public void setUp() throws Exception {
        form = new SearchOrcidBioForm();
    }

    @Test
    public void whenOrcidSuppliedAllOtherFieldsIgnored() {
        form.setOrcid("12-34");
        form.setFamilyName("Logan");
        form.setGivenName("Donald");
        form.setInstitutionName("University of Portsmouth");
        form.setPastInstitutionsSearchable(true);
        form.setOtherNamesSearchable(true);
        form.setKeyword("orcid");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("orcid:12-34", derivedQueryString);
    }

    @Test
    public void whenAllFieldsExceptOrcidProvidedQueryStringDisplaysCorrectPermutations() {
        form.setFamilyName("Logan");
        form.setGivenName("Donald");
        form.setInstitutionName("University of Portsmouth");
        form.setPastInstitutionsSearchable(true);
        form.setOtherNamesSearchable(true);
        form.setKeyword("orcid");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals(
                "given-names:donald* AND family-name:logan* OR other-names:donald* AND current-primary-institution-affiliation-name:university of portsmouth* OR past-institution-affiliation-name:university of portsmouth* AND keyword:orcid*",
                derivedQueryString);

    }

    @Test
    public void whenAllFieldsExceptOrcidAndCheckProvidedQueryStringDisplaysCorrectPermutations() {
        form.setFamilyName("Logan");
        form.setGivenName("Donald");
        form.setInstitutionName("University of Portsmouth");
        form.setPastInstitutionsSearchable(false);
        form.setOtherNamesSearchable(false);
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("given-names:donald* AND family-name:logan* AND current-primary-institution-affiliation-name:university of portsmouth*", derivedQueryString);

    }

    @Test
    public void whenFamilyNameOnlyProvidedOnlyFamilyNameDisplayed() {
        form.setFamilyName("Logan");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("family-name:logan*", derivedQueryString);
    }

    @Test
    public void whenGivenNameOnlyProvidedOnlyGivenNameDisplayed() {
        form.setGivenName("Donald");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("given-names:donald*", derivedQueryString);
    }

    @Test
    public void whenGivenNameAndOtherNameProvidedOtherNamesAlsoDisplayed() {
        form.setGivenName("Donald");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("given-names:donald*", derivedQueryString);
    }

    @Test
    public void whenPrimaryInstitutionNameOnlyProvidedOnlyPrimaryInstitutionDisplayed() {
        form.setInstitutionName("University of Portsmouth");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("current-primary-institution-affiliation-name:university of portsmouth*", derivedQueryString);
    }

    @Test
    public void whenKeywordOnlyProvidedOnlyKeywordsDisplayed() {
        form.setKeyword("Orcid");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("keyword:orcid*", derivedQueryString);
    }

    @Test
    public void whenPrimaryInstitutionNameAndPastProvidedBothInstitutionNamesDisplayed() {
        form.setInstitutionName("University of Portsmouth");
        form.setPastInstitutionsSearchable(true);
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("current-primary-institution-affiliation-name:university of portsmouth* OR past-institution-affiliation-name:university of portsmouth*",
                derivedQueryString);
    }

    @Test
    public void whenFamilyNameAndGivenNameProvidedQueryIsAnANDQuery() {
        form.setFamilyName("Logan");
        form.setGivenName("Donald");
        form.setOtherNamesSearchable(false);
        SearchOrcidSolrCriteria solrQuery = setupQuery();
        String derivedQueryString = solrQuery.deriveQueryString();
        assertEquals("given-names:donald* AND family-name:logan*", derivedQueryString);
    }

    @Test
    public void whenTextProvidedOnlyTextDisplayed() {
        form.setText("will");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("text:will*", derivedQueryString);
    }

    @Test
    public void whenTextProvidedWithSpecialCharacterOnlyTextDisplayed() {
        form.setText("will:");
        String derivedQueryString = setupQuery().deriveQueryString();
        assertEquals("text:will\\:*", derivedQueryString);
    }

    private SearchOrcidSolrCriteria setupQuery() {
        SearchOrcidSolrCriteria query = new SearchOrcidSolrCriteria();
        query.setFamilyName(form.getFamilyName());
        query.setGivenName(form.getGivenName());
        query.setOrcid(form.getOrcid());
        query.setInstitutionName(form.getInstitutionName());
        query.setIncludeOtherNames(form.isOtherNamesSearchable());
        query.setPastInstitutionsSearchable(form.isPastInstitutionsSearchable());
        query.setKeyword(form.getKeyword());
        query.setText(form.getText());
        return query;
    }

}
