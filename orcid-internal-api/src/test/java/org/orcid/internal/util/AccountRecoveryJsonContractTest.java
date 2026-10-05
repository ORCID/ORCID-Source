package org.orcid.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;
import org.orcid.api.common.jaxb.OrcidJacksonJaxbJsonProvider;
import org.orcid.internal.util.AccountRecoveryMatchResponse.RecordStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pins the JSON these endpoints put on the wire.
 *
 * The provider serving them extends JacksonXmlBindJsonProvider, so JAXB annotations take part in
 * naming. These assertions are what the account recovery poller reads, and what the module README
 * documents, so a rename here is a breaking change and should fail here first.
 */
public class AccountRecoveryJsonContractTest {

    private final ObjectMapper mapper = new OrcidJacksonJaxbJsonProvider().locateMapper(Object.class, null);

    @Test
    public void matchResponseFieldNamesTest() throws Exception {
        String json = mapper.writeValueAsString(AccountRecoveryMatchResponse.match(RecordStatus.LOCKED,
                Arrays.asList("user@example.com", "other@example.org")));
        assertTrue(json, json.contains("\"match\":true"));
        assertTrue(json, json.contains("\"recordStatus\":\"LOCKED\""));
        assertTrue(json, json.contains("\"emails\":[\"user@example.com\",\"other@example.org\"]"));
    }

    /** A match whose record lists no address still says so, rather than looking like an older registry. */
    @Test
    public void matchResponseWithNoEmailsCarriesAnEmptyListTest() throws Exception {
        String json = mapper.writeValueAsString(AccountRecoveryMatchResponse.match(RecordStatus.ACTIVE, Collections.<String> emptyList()));
        assertTrue(json, json.contains("\"emails\":[]"));
    }

    @Test
    public void noMatchResponseHidesRecordStatusTest() throws Exception {
        String json = mapper.writeValueAsString(AccountRecoveryMatchResponse.noMatch());
        assertTrue(json, json.contains("\"match\":false"));
        // A non match must not leak anything about a record, since there may not be one.
        assertTrue(json, json.contains("\"recordStatus\":null") || !json.contains("recordStatus"));
    }

    /**
     * A non match reads exactly as it did before the record's addresses were added to a match: not
     * an empty list, not a null, no key at all.
     */
    @Test
    public void noMatchResponseCarriesNoEmailsTest() throws Exception {
        String json = mapper.writeValueAsString(AccountRecoveryMatchResponse.noMatch());
        assertFalse(json, json.contains("emails"));
    }

    @Test
    public void matchResponseIsReadableTest() throws Exception {
        AccountRecoveryMatchResponse response = mapper.readValue(
                "{\"match\":true,\"recordStatus\":\"ACTIVE\",\"emails\":[\"user@example.com\"]}", AccountRecoveryMatchResponse.class);
        assertTrue(response.isMatch());
        assertEquals(Arrays.asList("user@example.com"), response.getEmails());
    }

    @Test
    public void resetLinkResponseFieldNamesTest() throws Exception {
        Date now = new Date();
        String json = mapper.writeValueAsString(new AccountRecoveryResetLinkResponse("https://orcid.org/reset-password-email/token", now, now));
        assertTrue(json, json.contains("\"resetLink\":"));
        assertTrue(json, json.contains("\"issueDate\":"));
        assertTrue(json, json.contains("\"expiryDate\":"));
    }

    @Test
    public void matchRequestIsReadableTest() throws Exception {
        AccountRecoveryMatchRequest request = mapper.readValue("{\"orcid\":\"0000-0001-0000-0000\",\"email\":\"user@example.com\"}",
                AccountRecoveryMatchRequest.class);
        assertEquals("0000-0001-0000-0000", request.getOrcid());
        assertEquals("user@example.com", request.getEmail());
    }

    @Test
    public void resetLinkRequestIsReadableTest() throws Exception {
        AccountRecoveryResetLinkRequest request = mapper.readValue("{\"orcid\":\"0000-0001-0000-0000\"}", AccountRecoveryResetLinkRequest.class);
        assertEquals("0000-0001-0000-0000", request.getOrcid());
    }
}
