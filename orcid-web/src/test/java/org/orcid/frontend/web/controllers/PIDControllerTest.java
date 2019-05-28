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
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml", "classpath:statistics-core-context.xml" })
public class PIDControllerTest extends BaseControllerTest{
    
    @Resource
    private PIDController controller;
    
    @Test
    public void testNorm(){
        ResponseEntity<PIDPojo> norm = controller.getNormalized("doi", "10.1/123");
        assertEquals("https://doi.org/10.1/123",norm.getBody().getNormUrl());
        assertEquals("10.1/123",norm.getBody().getNormValue());
        
        ResponseEntity<PIDPojo> norm2 = controller.getNormalized("issn", "ISSN: 1234-1234");
        assertEquals("https://portal.issn.org/resource/ISSN/1234-1234",norm2.getBody().getNormUrl());
        assertEquals("1234-1234",norm2.getBody().getNormValue());
    }

}
