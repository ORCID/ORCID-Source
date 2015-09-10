package org.orcid.core.web.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.junit.Test;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class CrossDomainWebMangerTest {
    
    @Resource
    CrossDomainWebManger crossDomainWebManger;
    
    String [] allowedDomains = {"orcid.org", "other.orcid.org", "qa.orcid.org", "qa-1.orcid.org", "sandbox.orcid.org", "sandbox-1.orcid.org"};
    String [] forbiddenDomains = {".orcid.org", "otherorcid.org", "myorcid.org", "ihateorcid.org", "qa.ihateorcid.org"};
    
    
    @Test
    public void testDomains() throws MalformedURLException {
        for(String allowed : allowedDomains) {
            assertTrue(crossDomainWebManger.validateDomain(allowed));
        }  
        
        for(String forbidden : forbiddenDomains) {
            assertFalse(crossDomainWebManger.validateDomain(forbidden));
        }
    }
}
