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
package org.orcid.integration.blackbox.api.v2.tests;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PersonTest extends BlackBoxBaseV2Release {
    @Resource(name = "memberV2ApiClient_rc2")
    private org.orcid.integration.blackbox.api.v2.rc2.MemberV2ApiClientImpl memberV2ApiClient_rc2;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient_rc2;

    @Resource(name = "memberV2ApiClient_rc3")
    private org.orcid.integration.blackbox.api.v2.rc3.MemberV2ApiClientImpl memberV2ApiClient_rc3;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient_rc3;

    @Resource(name = "memberV2ApiClient_rc4")
    private org.orcid.integration.blackbox.api.v2.rc4.MemberV2ApiClientImpl memberV2ApiClient_rc4;
    @Resource(name = "publicV2ApiClient_rc4")
    private PublicV2ApiClientImpl publicV2ApiClient_rc4;

    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient_release;
    @Resource(name = "publicV2ApiClient")
    private PublicV2ApiClientImpl publicV2ApiClient_release;
    
    private String limitedEmail = "limited@test.orcid.org";

    private static boolean allSet = false;

    private static String researcherUrl1 = "http://test.orcid.org/1/" + System.currentTimeMillis();
    private static String researcherUrl2 = "http://test.orcid.org/2/" + System.currentTimeMillis();

    @Before
    public void setUpUserInUi() throws Exception {
        if (allSet) {
            return;
        }

        signin();
        
        //Set the default visibility to public, so, all elements created are public by default
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        
        showMyOrcidPage();

        openEditAddressModal();
        deleteAddresses();
        createAddress(org.orcid.jaxb.model.common_rc4.Iso3166Country.US.name());
        saveEditAddressModal();

        openEditOtherNamesModal();
        deleteOtherNames();
        createOtherName("other-name-1");
        createOtherName("other-name-2");
        saveOtherNamesModal();

        openEditKeywordsModal();
        deleteKeywords();
        createKeyword("keyword-1");
        createKeyword("keyword-2");
        saveKeywordsModal();

        openEditResearcherUrlsModal();
        deleteResearcherUrls();
        createResearcherUrl(researcherUrl1);
        createResearcherUrl(researcherUrl2);
        saveResearcherUrlsModal();

        if (hasExternalIdentifiers()) {
            showMyOrcidPage();
            openEditExternalIdentifiersModal();
            deleteExternalIdentifiers();
            saveExternalIdentifiersModal();
        }

        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        updatePrimaryEmailVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        removePopOver();
        if (emailExists(limitedEmail)) {
            updateEmailVisibility(limitedEmail, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        } else {
            addEmail(limitedEmail, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        }

        String accessToken = getAccessToken(getScopes(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
        createExternalIdentifier("A-0001", getUser1OrcidId(), accessToken);
        createExternalIdentifier("A-0002", getUser1OrcidId(), accessToken);

        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        updateExternalIdentifierVisibility("A-0001", org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        updateExternalIdentifierVisibility("A-0002", org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        saveExternalIdentifiersModal();

        // Set biography to public
        changeBiography(null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        // Set names to public
        changeNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        allSet = true;
    }

    @AfterClass
    public static void afterClass() {
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        saveEditAddressModal();

        openEditOtherNamesModal();
        deleteOtherNames();
        saveOtherNamesModal();

        openEditKeywordsModal();
        deleteKeywords();
        saveKeywordsModal();

        openEditResearcherUrlsModal();
        deleteResearcherUrls();
        saveResearcherUrlsModal();

        openEditExternalIdentifiersModal();
        deleteExternalIdentifiers();
        saveExternalIdentifiersModal();
        
        openEditResearcherUrlsModal();
        deleteResearcherUrls();
        saveResearcherUrlsModal();
        
        signout();
    }

    /**
     * 
     * RC2
     * 
     */
    @Test
    public void testGetBioFromPublicAPI_rc2() {
        ClientResponse response = publicV2ApiClient_rc2.viewBiographyXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc2.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc2.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testGetBioFromMemberAPI_rc2() throws Exception {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc2.viewBiography(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc2.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc2.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testViewPersonFromMemberAPI_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc2.viewPerson(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Thread.sleep(100);
        org.orcid.jaxb.model.record_rc2.Person person = response.getEntity(org.orcid.jaxb.model.record_rc2.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc2.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc2(getUser1UserName(), org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC,
                person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc2(limitedEmail, org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(2, person.getExternalIdentifiers().getExternalIdentifiers().size());

        boolean foundPublic = false;
        boolean foundLimited = false;

        for (org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier e : person.getExternalIdentifiers().getExternalIdentifiers()) {
            if ("A-0001".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else if ("A-0002".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, e.getVisibility());
                foundLimited = true;
            }
        }

        assertTrue(foundPublic);
        assertTrue(foundLimited);

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getName().getVisibility());
    }

    @Test
    public void testViewPersonFromPublicAPI_rc2() {
        ClientResponse response = publicV2ApiClient_rc2.viewPersonXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc2.Person person = response.getEntity(org.orcid.jaxb.model.record_rc2.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc2.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc2(getUser1UserName(), org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC,
                person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals("test", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getType());
        assertEquals("A-0001", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getValue());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getExternalIdentifiers().getExternalIdentifiers().get(0).getVisibility());

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());        
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());        
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, person.getName().getVisibility());
    }

    /**
     * 
     * RC3
     * 
     */
    @Test
    public void testGetBioFromPublicAPI_rc3() {
        ClientResponse response = publicV2ApiClient_rc3.viewBiographyXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc3.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc3.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testGetBioFromMemberAPI_rc3() throws Exception {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc3.viewBiography(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc3.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc3.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testViewPersonFromMemberAPI_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc3.viewPerson(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Thread.sleep(100);
        org.orcid.jaxb.model.record_rc3.Person person = response.getEntity(org.orcid.jaxb.model.record_rc3.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc3.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc3(getUser1UserName(), org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC,
                person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc3(limitedEmail, org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(2, person.getExternalIdentifiers().getExternalIdentifiers().size());

        boolean foundPublic = false;
        boolean foundLimited = false;

        for (org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier e : person.getExternalIdentifiers().getExternalIdentifiers()) {
            if ("A-0001".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else if ("A-0002".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, e.getVisibility());
                foundLimited = true;
            }
        }

        assertTrue(foundPublic);
        assertTrue(foundLimited);

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getName().getVisibility());
    }

    @Test
    public void testViewPersonFromPublicAPI_rc3() {
        ClientResponse response = publicV2ApiClient_rc3.viewPersonXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc3.Person person = response.getEntity(org.orcid.jaxb.model.record_rc3.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc3.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc3(getUser1UserName(), org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC,
                person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals("test", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getType());
        assertEquals("A-0001", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getValue());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getExternalIdentifiers().getExternalIdentifiers().get(0).getVisibility());

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, person.getName().getVisibility());
    }

    /**
     * 
     * RC4
     * 
     */
    @Test
    public void testGetBioXmlFromPublicAPI_rc4() {
        ClientResponse response = publicV2ApiClient_rc4.viewBiographyXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc4.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, bio.getVisibility());
    }
    
    @Test
    public void testGetBioJsonFromPublicAPI_rc4() {
        ClientResponse response = publicV2ApiClient_rc4.viewBiographyJson(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc4.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testGetBioFromMemberAPI_rc4() throws Exception {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc4.viewBiography(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc4.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, bio.getVisibility());
    }
    
    @Test
    public void testGetBioJsonFromMemberAPI_rc4() throws Exception {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc4.viewBiographyJson(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.Biography bio = response.getEntity(org.orcid.jaxb.model.record_rc4.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testViewPersonFromMemberAPI_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc4.viewPerson(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Thread.sleep(100);
        org.orcid.jaxb.model.record_rc4.Person person = response.getEntity(org.orcid.jaxb.model.record_rc4.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc4.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc4(getUser1UserName(), org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC,
                person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc4(limitedEmail, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(2, person.getExternalIdentifiers().getExternalIdentifiers().size());

        boolean foundPublic = false;
        boolean foundLimited = false;

        for (org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier e : person.getExternalIdentifiers().getExternalIdentifiers()) {
            if ("A-0001".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else if ("A-0002".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, e.getVisibility());
                foundLimited = true;
            }
        }

        assertTrue(foundPublic);
        assertTrue(foundLimited);

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getName().getVisibility());
    }

    @Test
    public void testViewPersonFromPublicAPI_rc4() {
        ClientResponse response = publicV2ApiClient_rc4.viewPersonXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.Person person = response.getEntity(org.orcid.jaxb.model.record_rc4.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc4.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_rc4(getUser1UserName(), org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC,
                person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals("test", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getType());
        assertEquals("A-0001", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getValue());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getExternalIdentifiers().getExternalIdentifiers().get(0).getVisibility());

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, person.getName().getVisibility());
    }

    /**
     * 
     * Release
     * 
     */
    @Test
    public void testGetBioFromPublicAPI_release() {
        ClientResponse response = publicV2ApiClient_release.viewBiographyXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_v2.Biography bio = response.getEntity(org.orcid.jaxb.model.record_v2.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testGetBioFromMemberAPI_release() throws Exception {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_release.viewBiography(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_v2.Biography bio = response.getEntity(org.orcid.jaxb.model.record_v2.Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testViewPersonFromMemberAPI_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_release.viewPerson(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Thread.sleep(100);
        org.orcid.jaxb.model.record_v2.Person person = response.getEntity(org.orcid.jaxb.model.record_v2.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_release(getUser1UserName(), org.orcid.jaxb.model.common_v2.Visibility.PUBLIC,
                person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_release(limitedEmail, org.orcid.jaxb.model.common_v2.Visibility.LIMITED, person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(2, person.getExternalIdentifiers().getExternalIdentifiers().size());

        boolean foundPublic = false;
        boolean foundLimited = false;

        for (org.orcid.jaxb.model.record_v2.PersonExternalIdentifier e : person.getExternalIdentifiers().getExternalIdentifiers()) {
            if ("A-0001".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else if ("A-0002".equals(e.getValue())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, e.getVisibility());
                foundLimited = true;
            }
        }

        assertTrue(foundPublic);
        assertTrue(foundLimited);

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getName().getVisibility());
    }

    @Test
    public void testViewPersonFromPublicAPI_release() {
        ClientResponse response = publicV2ApiClient_release.viewPersonXML(getUser1OrcidId());
        assertNotNull(response);
        org.orcid.jaxb.model.record_v2.Person person = response.getEntity(org.orcid.jaxb.model.record_v2.Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        org.orcid.integration.blackbox.api.v2.tests.EmailTest.assertListContainsEmail_release(getUser1UserName(), org.orcid.jaxb.model.common_v2.Visibility.PUBLIC,
                person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals("test", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getType());
        assertEquals("A-0001", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getValue());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getExternalIdentifiers().getExternalIdentifiers().get(0).getVisibility());

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(2, person.getResearcherUrls().getResearcherUrls().size());
        assertThat(person.getResearcherUrls().getResearcherUrls().get(0).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertThat(person.getResearcherUrls().getResearcherUrls().get(1).getUrl().getValue(), anyOf(is(researcherUrl1), is(researcherUrl2)));
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(0).getVisibility());        
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getResearcherUrls().getResearcherUrls().get(1).getVisibility());
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, person.getName().getVisibility());
    }
    
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED));
    }
}
