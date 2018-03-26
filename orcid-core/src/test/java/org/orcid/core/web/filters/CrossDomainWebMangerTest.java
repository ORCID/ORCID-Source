package org.orcid.core.web.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class CrossDomainWebMangerTest {
    
    @Resource
    CrossDomainWebManger crossDomainWebManger;
    
    String [] allowedDomains = {"https://orcid.org", "http://orcid.org", "http://qa.orcid.org", "https://qa.orcid.org", "https://sandbox.orcid.org", "http://sandbox.orcid.org"};
    String [] forbiddenDomains = {"http://.orcid.org", "http://www.otherorcid.org", "http://www.myorcid.org", "http://www.ihateorcid.org", "http://qa.ihateorcid.org", "https://.orcid.org", "https://www.otherorcid.org", "https://www.myorcid.org", "https://www.ihateorcid.org", "https://qa.ihateorcid.org"};
    
    String [] allowedPaths = {"/public/other","/public/","/userStatus.json"};
    String [] forbiddenPaths = {"/public","/whatever/public","/whatever/public/","/whatever/public/other",
            "/whatever/userStatus.json","/userstatus.json","/userStatus.json/","/userStatus.json/whatever",
            "/userStatus.jsonwhatever/test","/userStatus.json/whatever","/userStatus.jsonwhatever","/userStatus.jsonwhatever/test"};
    
    @Test
    public void testDomains() throws MalformedURLException {
        for(String allowed : allowedDomains) {            
            assertTrue("testing: " +  allowed, crossDomainWebManger.validateDomain(allowed));
        }  
        
        for(String forbidden : forbiddenDomains) {
            assertFalse("Testing: " + forbidden, crossDomainWebManger.validateDomain(forbidden));
        }
    }
    
    @Test
    public void testPaths() throws MalformedURLException {
        for(String allowed : allowedPaths) {            
            assertTrue("testing: " +  allowed, crossDomainWebManger.validatePath(allowed));
        }  
        
        for(String forbidden : forbiddenPaths) {
            assertFalse("Testing: " + forbidden, crossDomainWebManger.validatePath(forbidden));
        }
    }
}
