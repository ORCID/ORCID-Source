package org.orcid.core.oauth.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.orcid.core.togglz.Features;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.togglz.junit.TogglzRule;

public class OrcidOauthRedirectResolverTest {

    private Collection<String> allRedirectGrantTypes = Arrays.asList("implicit", "refresh_token", "client_credentials", "authorization_code",
            "urn:ietf:params:oauth:grant-type:token-exchange");

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    private OrcidOauthRedirectResolver resolver = new OrcidOauthRedirectResolver();

    {
        resolver.setRedirectGrantTypes(allRedirectGrantTypes);
    }

    @Test
    public void resolveRedirect_emptyGrantTypesTest() {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        // Empty authorized grant types should fail
        clientDetails.setClientAuthorizedGrantTypes(Set.of());
        try {
            resolver.resolveRedirect("", clientDetails);
        } catch (InvalidGrantException e) {
            assertEquals("A client must have at least one authorized grant type.", e.getMessage());
        }
    }

    @Test
    public void resolveRedirect_invalidGrantTypesTest() {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        // Empty authorized grant types should fail
        ClientAuthorisedGrantTypeEntity gte1 = new ClientAuthorisedGrantTypeEntity();
        gte1.setGrantType("other");
        clientDetails.setClientAuthorizedGrantTypes(Set.of(gte1));
        try {
            resolver.resolveRedirect("", clientDetails);
        } catch (InvalidGrantException e) {
            assertEquals("A redirect_uri can only be used by implicit or authorization_code grant types.", e.getMessage());
        }
    }

    @Test
    public void resolveRedirect_noRegisteredRedirectUriTest() {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        // Empty authorized grant types should fail
        ClientAuthorisedGrantTypeEntity gte1 = new ClientAuthorisedGrantTypeEntity();
        gte1.setGrantType("authorization_code");
        clientDetails.setClientAuthorizedGrantTypes(Set.of(gte1));
        // Null redirect uris
        try {
            resolver.resolveRedirect("", clientDetails);
        } catch (InvalidRequestException e) {
            assertEquals("At least one redirect_uri must be registered with the client.", e.getMessage());
        }
        // Empty redirect uris
        try {
            clientDetails.setClientRegisteredRedirectUris(new TreeSet());
            resolver.resolveRedirect("", clientDetails);
        } catch (InvalidRequestException e) {
            assertEquals("At least one redirect_uri must be registered with the client.", e.getMessage());
        }
    }

    @Test
    public void resolveRedirect_NoMatchingRedirectTest() {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        // Empty authorized grant types should fail
        ClientAuthorisedGrantTypeEntity gte1 = new ClientAuthorisedGrantTypeEntity();
        gte1.setGrantType("authorization_code");
        clientDetails.setClientAuthorizedGrantTypes(Set.of(gte1));

        TreeSet<ClientRedirectUriEntity> redirectUris = new TreeSet<ClientRedirectUriEntity>();
        ClientRedirectUriEntity r1 = new ClientRedirectUriEntity("https://qa.orcid.org/1", clientDetails);
        ClientRedirectUriEntity r2 = new ClientRedirectUriEntity("https://qa.orcid.org/2", clientDetails);
        ClientRedirectUriEntity r3 = new ClientRedirectUriEntity("https://qa.orcid.org/3", clientDetails);
        redirectUris.add(r1);
        redirectUris.add(r2);
        redirectUris.add(r3);
        clientDetails.setClientRegisteredRedirectUris(redirectUris);

        // Root url should not match if it is not registered
        try {
            resolver.resolveRedirect("https://qa.orcid.org", clientDetails);
        } catch (RedirectMismatchException e) {
            assertEquals("Unable to find a matching redirect_uri for the client.", e.getMessage());
        }

        // Different protocol should not match
        try {
            resolver.resolveRedirect("http://qa.orcid.org/1", clientDetails);
        } catch (RedirectMismatchException e) {
            assertEquals("Unable to find a matching redirect_uri for the client.", e.getMessage());
        }

        // Different domain should not match
        try {
            resolver.resolveRedirect("https://orcid.org/1", clientDetails);
        } catch (RedirectMismatchException e) {
            assertEquals("Unable to find a matching redirect_uri for the client.", e.getMessage());
        }

        // Different domain should not match
        try {
            resolver.resolveRedirect("https://example.com/1", clientDetails);
        } catch (RedirectMismatchException e) {
            assertEquals("Unable to find a matching redirect_uri for the client.", e.getMessage());
        }

        // Same domain but different patch should not match
        try {
            resolver.resolveRedirect("https://qa.orcid.org/4", clientDetails);
        } catch (RedirectMismatchException e) {
            assertEquals("Unable to find a matching redirect_uri for the client.", e.getMessage());
        }
    }

    @Test
    public void resolveRedirectTest() {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        // Empty authorized grant types should fail
        ClientAuthorisedGrantTypeEntity gte1 = new ClientAuthorisedGrantTypeEntity();
        gte1.setGrantType("authorization_code");
        clientDetails.setClientAuthorizedGrantTypes(Set.of(gte1));

        TreeSet<ClientRedirectUriEntity> redirectUris = new TreeSet<ClientRedirectUriEntity>();
        ClientRedirectUriEntity r1 = new ClientRedirectUriEntity("https://qa.orcid.org/1", clientDetails);
        ClientRedirectUriEntity r2 = new ClientRedirectUriEntity("https://qa.orcid.org/2", clientDetails);
        ClientRedirectUriEntity r3 = new ClientRedirectUriEntity("https://qa.orcid.org/3", clientDetails);
        redirectUris.add(r1);
        redirectUris.add(r2);
        redirectUris.add(r3);
        clientDetails.setClientRegisteredRedirectUris(redirectUris);

        assertEquals("https://qa.orcid.org/1", resolver.resolveRedirect("https://qa.orcid.org/1", clientDetails));
        assertEquals("https://qa.orcid.org/2", resolver.resolveRedirect("https://qa.orcid.org/2", clientDetails));
        assertEquals("https://qa.orcid.org/3", resolver.resolveRedirect("https://qa.orcid.org/3", clientDetails));
        assertEquals("https://qa.orcid.org/1/subdirectory", resolver.resolveRedirect("https://qa.orcid.org/1/subdirectory", clientDetails));
        assertEquals("https://qa.orcid.org/1/2/3", resolver.resolveRedirect("https://qa.orcid.org/1/2/3", clientDetails));
    }

    @Test
    public void redirectUriGeneralTests() {
        redirectUriGeneralTest();
    }
    
    private void redirectUriGeneralTest() {
        // No matches at all
        assertFalse(resolver.redirectMatches("https://example.com", "https://qa.orcid.org"));
        assertFalse(resolver.redirectMatches("https://qa.orcid.org", "https://example.com"));

        // Different scheme should not match
        assertFalse(resolver.redirectMatches("https://qa.orcid.org", "http://qa.orcid.org"));
        assertFalse(resolver.redirectMatches("http://qa.orcid.org", "https://qa.orcid.org"));
        assertFalse(resolver.redirectMatches("https://example.com", "http://example.com"));
        assertFalse(resolver.redirectMatches("http://example.com", "https://example.com"));

        // Different port should not match
        assertFalse(resolver.redirectMatches("http://qa.orcid.org", "http://qa.orcid.org:8080"));
        assertFalse(resolver.redirectMatches("http://qa.orcid.org:8080", "http://qa.orcid.org"));
        assertFalse(resolver.redirectMatches("http://qa.orcid.org:8080", "http://qa.orcid.org:8081"));
        assertFalse(resolver.redirectMatches("http://127.0.0.1", "http://127.0.0.1:8080"));
        assertFalse(resolver.redirectMatches("http://127.0.0.1:8080", "http://127.0.0.1"));
        assertFalse(resolver.redirectMatches("http://127.0.0.1:8080", "http://127.0.0.1:8081"));

        // Different host should not match
        assertFalse(resolver.redirectMatches("https://orcid.org", "http://example.com"));

        // Root should not match if it is not registered
        assertFalse(resolver.redirectMatches("https://qa.orcid.org", "http://qa.orcid.org/subdirectory"));

        // Subdirectory should not match if it is not registered
        assertFalse(resolver.redirectMatches("https://qa.orcid.org/subdirectory/2", "https://qa.orcid.org/subdirectory/1"));
        assertFalse(resolver.redirectMatches("https://qa.orcid.org/s2", "https://qa.orcid.org/s1"));

        // Exact match
        assertTrue(resolver.redirectMatches("https://orcid.org", "https://orcid.org"));
        assertTrue(resolver.redirectMatches("http://example.com:9001", "http://example.com:9001"));
        assertTrue(resolver.redirectMatches("http://127.0.0.1", "http://127.0.0.1"));
        assertTrue(resolver.redirectMatches("http://127.0.0.1:8080", "http://127.0.0.1:8080"));

        // Subdirectory should match
        assertTrue(resolver.redirectMatches("https://orcid.org/subdirectory", "https://orcid.org"));
        assertTrue(resolver.redirectMatches("https://orcid.org/subdirectory/1", "https://orcid.org"));
        assertTrue(resolver.redirectMatches("https://orcid.org/subdirectory/1/2", "https://orcid.org"));
        assertTrue(resolver.redirectMatches("https://orcid.org/subdirectory/1/2", "https://orcid.org/subdirectory"));
        assertTrue(resolver.redirectMatches("https://orcid.org/subdirectory/1/2", "https://orcid.org/subdirectory/1"));
        assertTrue(resolver.redirectMatches("https://orcid.org/subdirectory/1/2", "https://orcid.org/subdirectory/1/2"));
        
        // If a subdomain is registered but not the domain, it should fail
        assertFalse(resolver.redirectMatches("https://orcid.org", "https://qa.orcid.org"));
        // Different subdomains should not match
        assertFalse(resolver.redirectMatches("https://qa.orcid.org", "https://sandbox.orcid.org"));       
        
        // Acceptance criteria checks: subdirectory should be allowed
        assertTrue(resolver.redirectMatches("https://example.com/subdirectory", "https://example.com"));
    }
    
    @Test
    public void redirectMatches_AllowMatchingSubdomainsTest() {
        // Subdomain should not match
        assertFalse(resolver.redirectMatches("https://www.orcid.org", "https://orcid.org"));
        assertFalse(resolver.redirectMatches("https://qa.orcid.org", "https://orcid.org"));    
        
        // Acceptance criteria checks: subdomains should be rejected
        assertFalse(resolver.redirectMatches("https://subdomain.example.com/", "https://example.com"));
        assertFalse(resolver.redirectMatches("https://subdomain.example.com/subdirectory", "https://example.com"));
        assertFalse(resolver.redirectMatches("https://www.example.com", "https://example.com"));
    }
    
}
