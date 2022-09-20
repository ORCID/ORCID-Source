package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.MalformedURLException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

public class AnonymizeTextTest {

    @Test
    public void testAnonymizeString() {
        String commonString = "Nonlinear transfer in heated L-mo;des threshold";
        assertNotEquals(commonString, AnonymizeText.anonymizeString(commonString));
    }

    @Test
    public void testAnonymizeURL() throws MalformedURLException {
        String url1 = "http://hdl.handle.net/1814/51605";
        assertNotEquals(url1, AnonymizeText.anonymizeURL(url1));
        String url2 = "dx.doi.org/10.1093/ajae/aaq063";
        assertNotEquals(url2, AnonymizeText.anonymizeURL(url2));
        String url3 = null;
        assertEquals(url3, AnonymizeText.anonymizeURL(url3));
        String url4 = "";
        assertEquals(url4, AnonymizeText.anonymizeURL(url4));
    }

    @Test
    public void testAnonymizeWorkContributor() throws MalformedURLException, JSONException {
        String contributor1 = "{\"contributorOrcid\":null,\"creditName\":{\"content\":\"Vogel, Peter\"},\"contributorEmail\":null,\"contributorAttributes\":{\"contributorSequence\":\"ADDITIONAL\",\"contributorRole\":\"AUTHOR\"}}";
        JSONObject anonymizedContributor1 = AnonymizeText.anonymizeWorkContributor(new JSONObject(contributor1));
        assertNotEquals(contributor1, anonymizedContributor1);

        String contributor2 = "{\"contributorOrcid\":{\"uri\":\"https://sandbox.orcid.org/0000-0002-8811-9027\",\"path\":\"0000-0002-8811-9027\",\"host\":null},\"creditName\":{\"content\":\"Paula Demain\"},\"contributorEmail\":null,\"contributorAttributes\":{\"contributorSequence\":null,\"contributorRole\":\"WRITING_REVIEW_EDITING\"}}";
        JSONObject anonymizedContributor2 = AnonymizeText.anonymizeWorkContributor(new JSONObject(contributor2));
        assertNotEquals(contributor2, anonymizedContributor2);
    }

    @Test
    public void testAnonymizeWorkExternalIdentifier() throws MalformedURLException, JSONException {
        String externalId1 = "{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"ISBN\",\"workExternalIdentifierId\":{\"content\":\"978-88-8212-926-2\"}}";
        assertNotEquals(externalId1, AnonymizeText.anonymizeWorkExternalIdentifier(new JSONObject(externalId1)));
        String externalId2 = "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://hdl.handle.net/1814/51605\"},\"workExternalIdentifierType\":\"HANDLE\",\"workExternalIdentifierId\":{\"content\":\"http://hdl.handle.net/1814/51605\"}}";
        assertNotEquals(externalId1, AnonymizeText.anonymizeWorkExternalIdentifier(new JSONObject(externalId2)));

    }

}
