package org.orcid.persistence.jpa.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserconnectionEntityTest {

    @Test
    public void testToStringRedactsOAuthCredentials() {
        UserconnectionEntity entity = new UserconnectionEntity();
        entity.setAccesstoken("access-token-value");
        entity.setRefreshtoken("refresh-token-value");
        entity.setSecret("client-secret-value");
        entity.setDisplayname("ORCID test connection");

        String value = entity.toString();

        assertFalse(value.contains("access-token-value"));
        assertFalse(value.contains("refresh-token-value"));
        assertFalse(value.contains("client-secret-value"));
        assertTrue(value.contains("accesstoken=[REDACTED]"));
        assertTrue(value.contains("refreshtoken=[REDACTED]"));
        assertTrue(value.contains("secret=[REDACTED]"));
        assertTrue(value.contains("displayname=ORCID test connection"));
    }
}