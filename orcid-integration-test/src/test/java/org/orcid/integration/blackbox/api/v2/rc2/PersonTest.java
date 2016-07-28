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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
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
public class PersonTest extends BlackBoxBaseRC2 {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;

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
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
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
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewPerson(getUser1OrcidId(), accessToken);
        assertNotNull(response);        
        assertEquals("invalid "+response,200,response.getStatus());
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
        EmailTest.assertListContainsEmail("limited@test.orcid.org", Visibility.LIMITED, person.getEmails());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(2, person.getExternalIdentifiers().getExternalIdentifier().size());

        boolean foundPublic = false;
        boolean foundLimited = false;

        for (PersonExternalIdentifier e : person.getExternalIdentifiers().getExternalIdentifier()) {
            if ("A-0001".equals(e.getType())) {
                assertEquals("A-0001", e.getValue());
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else {
                assertEquals("A-0002", e.getValue());
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
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("A-0001", person.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("A-0001", person.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Visibility.PUBLIC, person.getExternalIdentifiers().getExternalIdentifier().get(0).getVisibility());

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
        assertNotNull(person.getName());
        assertEquals(getUser1GivenName(), person.getName().getGivenNames().getContent());
        assertNotNull(person.getName().getFamilyName());
        assertEquals(getUser1FamilyNames(), person.getName().getFamilyName().getContent());
        assertNotNull(person.getName().getCreditName());
        assertEquals(getUser1CreditName(), person.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
    }

    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_READ_LIMITED.value() + " " + ScopePathType.PERSON_UPDATE.value(), clientId, clientSecret,
                redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
