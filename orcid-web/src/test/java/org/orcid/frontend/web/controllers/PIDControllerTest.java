package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.pojo.PIDPojo;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
public class PIDControllerTest extends BaseControllerTest {
    
    @Resource
    private PIDController pidController;
    
    @Test
    public void testNorm(){
        ResponseEntity<PIDPojo> norm = pidController.getNormalized("doi", "10.1/123");
        assertEquals("https://doi.org/10.1/123",norm.getBody().getNormUrl());
        assertEquals("10.1/123",norm.getBody().getNormValue());
        
        ResponseEntity<PIDPojo> norm2 = pidController.getNormalized("issn", "ISSN: 1234-1234");
        assertEquals("",norm2.getBody().getNormUrl());
        assertEquals("1234-1234",norm2.getBody().getNormValue());
    }

}
