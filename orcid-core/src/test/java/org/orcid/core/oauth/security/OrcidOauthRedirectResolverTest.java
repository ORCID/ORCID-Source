package org.orcid.core.oauth.security;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class OrcidOauthRedirectResolverTest {
    
    private Collection<String> allRedirectGrantTypes = Arrays.asList("implicit", "refresh_token", "client_credentials", "authorization_code", "urn:ietf:params:oauth:grant-type:token-exchange");
    
    private OrcidOauthRedirectResolver resolver = new OrcidOauthRedirectResolver();
    
    {
        resolver.setRedirectGrantTypes(allRedirectGrantTypes);
    }
    
    @Test
    public void resolveRedirectTest() {
        fail();
    }
    
    @Test
    public void redirectMatchesTest() {
        
    }
}

