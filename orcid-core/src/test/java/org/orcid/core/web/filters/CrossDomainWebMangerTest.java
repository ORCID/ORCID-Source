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
    
    String [] allowedDomains = {"https://orcid.org", "https://www.orcid.org", "https://other.orcid.org", "https://qa.orcid.org", "https://qa-1.orcid.org", "https://sandbox.orcid.org", "https://sandbox-1.orcid.org", "http://orcid.org", "http://other.orcid.org", "http://qa.orcid.org", "http://qa-1.orcid.org", "http://sandbox.orcid.org", "http://sandbox-1.orcid.org"};
    String [] forbiddenDomains = {"http://.orcid.org", "http://www.otherorcid.org", "http://www.myorcid.org", "http://www.ihateorcid.org", "http://qa.ihateorcid.org", "https://.orcid.org", "https://www.otherorcid.org", "https://www.myorcid.org", "https://www.ihateorcid.org", "https://qa.ihateorcid.org"};
    
    
    @Test
    public void testDomains() throws MalformedURLException {
        for(String allowed : allowedDomains) {            
            assertTrue("testing: " +  allowed, crossDomainWebManger.validateDomain(allowed));
        }  
        
        for(String forbidden : forbiddenDomains) {
            assertFalse("Testing: " + forbidden, crossDomainWebManger.validateDomain(forbidden));
        }
    }
}
