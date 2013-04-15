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
package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.jaxb.model.message.*;

public class SolrAndDBSearchManagerImplTest extends BaseTest {

    @Resource
    private SolrAndDBSearchManagerImpl solrAndDBSearchManager;

    @Mock
    private OrcidSearchManager orcidSearchManager;

    @Mock
    private OrcidProfileManager orcidProfileManager;

    @Before
    public void setUp() throws Exception {
        solrAndDBSearchManager.setOrcidProfileManager(orcidProfileManager);
        solrAndDBSearchManager.setOrcidSearchManager(orcidSearchManager);
    }

    @Test
    public void testThatQueriesReturnedBySolrHaveVisibilityApplied() throws Exception {
        String solrSearchString = "A string that results in 3 orcids returned";
        when(orcidSearchManager.findOrcidsByQuery(solrSearchString, null, null)).thenReturn(solrOrcidMessage());
        when(orcidProfileManager.retrieveOrcidProfile("1234X")).thenReturn(orcidAllPublicVisibility());
        when(orcidProfileManager.retrieveOrcidProfile("4567Y")).thenReturn(orcid2SomeProtectedVisibility());
        when(orcidProfileManager.retrieveOrcidProfile("8910Z")).thenReturn(null);

        OrcidMessage retrievedMessage = solrAndDBSearchManager.findFilteredOrcidsBasedOnQuery(solrSearchString, null, null);
        List<OrcidSearchResult> retrievedResults = retrievedMessage.getOrcidSearchResults().getOrcidSearchResult();
        assertTrue(retrievedResults.size() == 2);

        Collections.sort(retrievedResults, new Comparator<OrcidSearchResult>() {
            public int compare(OrcidSearchResult searchRes1, OrcidSearchResult searchRes2) {
                return ((String) searchRes1.getOrcidProfile().getOrcid().getValue()).compareToIgnoreCase((String) searchRes2.getOrcidProfile().getOrcid().getValue());
            }
        });

        OrcidSearchResult searchRes1 = retrievedResults.get(0);
        // to string is adequate test since we're using mock objects.. don't
        // worry about null/empty list differences..

        // the default orcid returned unchanged from filter
        assertEquals(orcidAllPublicVisibility().toString(), searchRes1.getOrcidProfile().toString());
        OrcidSearchResult searchRes2 = retrievedResults.get(1);
        // the protected visibility retrieved from solr with non-public fields
        // negated
        assertEquals(orcidWithNonPublicFieldsNegated().toString(), searchRes2.getOrcidProfile().toString());

    }

    private OrcidMessage solrOrcidMessage() {

        OrcidMessage orcidMessage = new OrcidMessage();

        OrcidProfile orcidProfile1 = new OrcidProfile();
        orcidProfile1.setOrcid("1234X");

        OrcidProfile orcidProfile2 = new OrcidProfile();
        orcidProfile2.setOrcid("4567Y");

        OrcidProfile orcidProfile3 = new OrcidProfile();
        orcidProfile3.setOrcid("8910Z");

        OrcidSearchResult orcidSearchResult1 = new OrcidSearchResult();
        orcidSearchResult1.setRelevancyScore(new RelevancyScore(0.3f));
        orcidSearchResult1.setOrcidProfile(orcidProfile1);

        OrcidSearchResult orcidSearchResult2 = new OrcidSearchResult();
        orcidSearchResult2.setRelevancyScore(new RelevancyScore(0.6f));
        orcidSearchResult2.setOrcidProfile(orcidProfile2);

        OrcidSearchResult orcidSearchResult3 = new OrcidSearchResult();
        orcidSearchResult3.setRelevancyScore(new RelevancyScore(0.8f));
        orcidSearchResult3.setOrcidProfile(orcidProfile3);

        OrcidSearchResults searchResults = new OrcidSearchResults();
        searchResults.getOrcidSearchResult().add(orcidSearchResult1);
        searchResults.getOrcidSearchResult().add(orcidSearchResult2);
        searchResults.getOrcidSearchResult().add(orcidSearchResult3);
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;

    }

    private OrcidProfile orcidAllPublicVisibility() {
        OrcidProfile allDetailsPublicOrcid = buildDefaultOrcid();
        allDetailsPublicOrcid.setOrcid("1234X");
        return allDetailsPublicOrcid;
    }

    private OrcidProfile orcid2SomeProtectedVisibility() {
        OrcidProfile profile = buildDefaultOrcid();
        profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().setVisibility(Visibility.LIMITED);
        profile.setOrcid("4567Y");
        List<Affiliation> affiliations = profile.getOrcidBio().getAffiliations();
        for (Affiliation affiliation : affiliations) {
            affiliation.setVisibility(Visibility.LIMITED);
        }

        for (OrcidWork work : profile.retrieveOrcidWorks().getOrcidWork()) {
            work.setVisibility(Visibility.LIMITED);
        }
        // other names visibility protected but personal details generally
        // private
        profile.getOrcidBio().getPersonalDetails().getOtherNames().setVisibility(Visibility.LIMITED);
        return profile;
    }

    private OrcidProfile orcidWithNonPublicFieldsNegated() {
        OrcidProfile profile = buildDefaultOrcid();
        profile.setOrcid("4567Y");
        profile.getOrcidBio().setContactDetails(null);
        profile.getOrcidBio().getAffiliations().clear();
        profile.setOrcidActivities(null);
        profile.getOrcidBio().getPersonalDetails().setOtherNames(null);
        return profile;
    }

    private OrcidProfile buildDefaultOrcid() {
        OrcidProfile profile1 = new OrcidProfile();

        OrcidBio bio = new OrcidBio();

        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email("will@orcid.org"));
        contactDetails.retrievePrimaryEmail().setVisibility(Visibility.PUBLIC);
        bio.setContactDetails(contactDetails);

        ExternalIdentifier extId1 = new ExternalIdentifier(new ExternalIdOrcid("sponsOrc1"), new ExternalIdReference("sponsRef1"));
        ExternalIdentifier extId2 = new ExternalIdentifier(new ExternalIdOrcid("sponsOrc2"), new ExternalIdReference("sponsRef2"));

        ExternalIdentifiers externalIdentifiers = new ExternalIdentifiers();
        externalIdentifiers.setVisibility(Visibility.PUBLIC);
        externalIdentifiers.getExternalIdentifier().add(extId1);
        externalIdentifiers.getExternalIdentifier().add(extId2);
        bio.setExternalIdentifiers(externalIdentifiers);

        profile1.setOrcidBio(bio);
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setGivenNames(new GivenNames("William"));
        personalDetails.setFamilyName(new FamilyName("Simpson"));
        personalDetails.setCreditName(new CreditName("W. J. R. Simpson"));
        personalDetails.getCreditName().setVisibility(Visibility.PUBLIC);
        OtherNames otherNames = new OtherNames();
        otherNames.setVisibility(Visibility.PUBLIC);
        otherNames.addOtherName("Homer");
        otherNames.addOtherName("Thompson");
        personalDetails.setOtherNames(otherNames);

        bio.setPersonalDetails(personalDetails);

        Affiliation affiliation = getAffiliation();
        affiliation.setVisibility(Visibility.PUBLIC);
        bio.getAffiliations().add(affiliation);

        ResearcherUrls researcherUrls = new ResearcherUrls();
        bio.setResearcherUrls(researcherUrls);
        researcherUrls.getResearcherUrl().add(new ResearcherUrl(new Url("http://www.wjrs.co.uk")));
        researcherUrls.getResearcherUrl().add(new ResearcherUrl(new Url("http://www.vvs.com")));
        researcherUrls.setVisibility(Visibility.PUBLIC);

        Keywords keywords = new Keywords();
        bio.setKeywords(keywords);
        keywords.getKeyword().add(new Keyword("Java"));
        keywords.setVisibility(Visibility.PUBLIC);

        OrcidWork orcidWork = new OrcidWork();
        orcidWork.setVisibility(Visibility.PUBLIC);

        OrcidWorks orcidWorks = new OrcidWorks();
        orcidWorks.getOrcidWork().add(orcidWork);
        profile1.setOrcidWorks(orcidWorks);

        return profile1;
    }

    private Affiliation getAffiliation() {
        Affiliation affiliation = new Affiliation();
        affiliation.setAffiliationType(AffiliationType.CURRENT_INSTITUTION);
        affiliation.setAffiliationName("Past Institution");
        affiliation.setRoleTitle("A Role");
        Address address = new Address();
        address.setCountry(new Country("United Kingdom"));
        address.getCountry().setVisibility(Visibility.PUBLIC);
        affiliation.setAddress(address);
        return affiliation;
    }

}
