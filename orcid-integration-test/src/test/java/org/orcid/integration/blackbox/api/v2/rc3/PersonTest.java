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
package org.orcid.integration.blackbox.api.v2.rc3;

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
import org.orcid.jaxb.model.common_rc3.Iso3166Country;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PersonTest extends BlackBoxBaseRC3 {
    @Resource(name = "memberV2ApiClient_rc3")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient;

    private String limitedEmail = "limited@test.orcid.org";

    private static boolean allSet = false;
    
    private static String researcherUrl1 = "http://test.orcid.org/1/" + System.currentTimeMillis();
    private static String researcherUrl2 = "http://test.orcid.org/2/" + System.currentTimeMillis();
    
    @Before
    public void setUpUserInUi() throws InterruptedException, JSONException {               
        if(allSet) {
            return;
        }
        
        signin();
        openEditAddressModal();
        deleteAddresses();
        createAddress(Iso3166Country.US.name());
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);        
        saveEditAddressModal();

        openEditOtherNamesModal();
        deleteOtherNames();
        createOtherName("other-name-1");
        createOtherName("other-name-2");
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveOtherNamesModal();
        
        openEditKeywordsModal();
        deleteKeywords();
        createKeyword("keyword-1");
        createKeyword("keyword-2");
        changeKeywordsVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveKeywordsModal();

        openEditResearcherUrlsModal();
        createResearcherUrl(researcherUrl1);
        createResearcherUrl(researcherUrl2);
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveResearcherUrlsModal();
        
        if(hasExternalIdentifiers()) {
            showMyOrcidPage();        
            openEditExternalIdentifiersModal();
            deleteExternalIdentifiers();
            saveExternalIdentifiersModal();
        }
        
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        updatePrimaryEmailVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        removePopOver();
        if (emailExists(limitedEmail)) {
            updateEmailVisibility(limitedEmail, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        } else {
            addEmail(limitedEmail, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        }
        
        String accessToken = getAccessToken();
        createExternalIdentifier("A-0001", getUser1OrcidId(), accessToken);
        createExternalIdentifier("A-0002", getUser1OrcidId(), accessToken);
        
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        updateExternalIdentifierVisibility("A-0001", org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        updateExternalIdentifierVisibility("A-0002", org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);        
        saveExternalIdentifiersModal();
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
        signout();
    }

    @Test
    public void testGetBioFromPublicAPI() {
        ClientResponse response = publicV2ApiClient.viewBiographyXML(getUser1OrcidId());
        assertNotNull(response);
        Biography bio = response.getEntity(Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testGetBioFromMemberAPI() throws Exception {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewBiography(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        Biography bio = response.getEntity(Biography.class);
        assertNotNull(bio);
        assertEquals(getUser1Bio(), bio.getContent());
        assertEquals(Visibility.PUBLIC, bio.getVisibility());
    }

    @Test
    public void testViewPersonFromMemberAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewPerson(getUser1OrcidId(), accessToken);
        assertNotNull(response);
        assertEquals("invalid " + response, 200, response.getStatus());
        Thread.sleep(100);
        Person person = response.getEntity(Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        EmailTest.assertListContainsEmail(getUser1UserName(), Visibility.PUBLIC, person.getEmails());
        EmailTest.assertListContainsEmail(limitedEmail, Visibility.LIMITED, person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(2, person.getExternalIdentifiers().getExternalIdentifiers().size());

        boolean foundPublic = false;
        boolean foundLimited = false;

        for (PersonExternalIdentifier e : person.getExternalIdentifiers().getExternalIdentifiers()) {
            if ("A-0001".equals(e.getValue())) {
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else if("A-0002".equals(e.getValue())) {
                assertEquals(Visibility.LIMITED, e.getVisibility());
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
        assertEquals(Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertTrue("We created researcher urls for this test, so there should be at least some", person.getResearcherUrls().getResearcherUrls().size() >= 2);
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
    }

    @Test
    public void testViewPersonFromPublicAPI() {
        ClientResponse response = publicV2ApiClient.viewPersonXML(getUser1OrcidId());
        assertNotNull(response);
        Person person = response.getEntity(Person.class);
        assertNotNull(person);
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals(getUser1Bio(), person.getBiography().getContent());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        EmailTest.assertListContainsEmail(getUser1UserName(), Visibility.PUBLIC, person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals("test", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getType());
        assertEquals("A-0001", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getValue());
        assertEquals(Visibility.PUBLIC, person.getExternalIdentifiers().getExternalIdentifiers().get(0).getVisibility());

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(2, person.getKeywords().getKeywords().size());
        assertThat(person.getKeywords().getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(person.getKeywords().getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, person.getKeywords().getKeywords().get(1).getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());
        assertThat(person.getOtherNames().getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(person.getOtherNames().getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, person.getOtherNames().getOtherNames().get(1).getVisibility());
        assertNotNull(person.getResearcherUrls());
        assertTrue("We created researcher urls for this test, so there should be some", person.getResearcherUrls().getResearcherUrls().size() >= 2);
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE));
    }
}
