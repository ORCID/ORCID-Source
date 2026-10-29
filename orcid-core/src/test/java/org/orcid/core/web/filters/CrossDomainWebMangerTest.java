package org.orcid.core.web.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * CrossDomainWebManger is a regex holder: validatePath matches a compiled Pattern that is a
 * constant on the class, and validateDomain matches URI.getHost() against the comma separated
 * allowed_domains property. The only thing the Spring context supplied was that property, so it
 * is set directly here.
 *
 * <p>
 * "localhost" is the value of org.orcid.security.cors.allowed_domains in
 * orcid-test/src/main/resources/properties/test-core.properties, which is what these assertions
 * were written against -- with the production value (dev.orcid.org) the two allowed domains
 * below would be rejected.
 *
 * @author Angel Montenegro
 * 
 */
public class CrossDomainWebMangerTest {

    private static final String ALLOWED_DOMAINS = "localhost";

    CrossDomainWebManger crossDomainWebManger;

    String [] allowedDomains = {"http://localhost", "https://localhost"};
    String [] forbiddenDomains = {"http://.orcid.org", "http://www.otherorcid.org", "http://www.myorcid.org", "http://www.testorcid.org", "http://qa.testorcid.org", "https://.orcid.org", "https://www.otherorcid.org", "https://www.myorcid.org", "https://www.testorcid.org", "https://qa.testorcid.org"};
    
    String [] allowedPaths = {"/lang.json","/userStatus.json","/oauth/userinfo","/oauth/jwks","/.well-known/openid-configuration"};
    String [] forbiddenPaths = {"/oauth","/whatever/oauth","/whatever/oauth/","/whatever/oauth/other",
            "/whatever/userStatus.json","/userstatus.json","/userStatus.json/","/userStatus.json/whatever",
            "/userStatus.jsonwhatever/test","/userStatus.json/whatever","/userStatus.jsonwhatever","/userStatus.jsonwhatever/test"};

    @Before
    public void before() {
        crossDomainWebManger = new CrossDomainWebManger();
        ReflectionTestUtils.setField(crossDomainWebManger, "allowedDomains", ALLOWED_DOMAINS);
    }

    @Test
    public void testDomains() throws URISyntaxException {
        for(String allowed : allowedDomains) {            
            assertTrue("testing: " +  allowed, crossDomainWebManger.validateDomain(allowed));
        }  
        
        for(String forbidden : forbiddenDomains) {
            assertFalse("Testing: " + forbidden, crossDomainWebManger.validateDomain(forbidden));
        }
    }
    
    @Test
    public void testPaths() throws URISyntaxException {
        for(String allowed : allowedPaths) {            
            assertTrue("testing: " +  allowed, crossDomainWebManger.validatePath(allowed));
        }  
        
        for(String forbidden : forbiddenPaths) {
            assertFalse("Testing: " + forbidden, crossDomainWebManger.validatePath(forbidden));
        }
    }
}
