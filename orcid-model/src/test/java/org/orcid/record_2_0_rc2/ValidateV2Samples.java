/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.record_2_0_rc2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;

public class ValidateV2Samples {
    @Test
    public void testUnmarshallPersonalDetails() {
        PersonalDetails personalDetails = (PersonalDetails) unmarshallFromPath("/record_2.0_rc2/samples/personal-details-2.0_rc2.xml", PersonalDetails.class);
        assertNotNull(personalDetails);
        //Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals("Biography", personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        //Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());        
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals("Give Names", personalDetails.getName().getGivenNames().getContent());        
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
        //Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertEquals("Other Name #1", personalDetails.getOtherNames().getOtherNames().get(0).getContent());
        assertEquals("Other Name #2", personalDetails.getOtherNames().getOtherNames().get(1).getContent());        
        assertEquals(Visibility.PUBLIC, personalDetails.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(Visibility.LIMITED, personalDetails.getOtherNames().getOtherNames().get(1).getVisibility());
    }
    
    @Test
    public void testUnmarshallResearcherUrl() {
        ResearcherUrls rUrls = (ResearcherUrls) unmarshallFromPath("/record_2.0_rc2/samples/researcher-urls-2.0_rc2.xml", ResearcherUrls.class);
        assertNotNull(rUrls);
        assertNotNull(rUrls.getResearcherUrls());
        assertEquals(1, rUrls.getResearcherUrls().size());
        assertNotNull(rUrls.getResearcherUrls().get(0).getCreatedDate());
        assertNotNull(rUrls.getResearcherUrls().get(0).getLastModifiedDate());        
        assertEquals("Site # 1", rUrls.getResearcherUrls().get(0).getUrlName());
        assertEquals("http://site1.com/", rUrls.getResearcherUrls().get(0).getUrl().getValue());
        assertEquals(Long.valueOf(1248), rUrls.getResearcherUrls().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC.value(), rUrls.getResearcherUrls().get(0).getVisibility().value());
        assertNotNull(rUrls.getResearcherUrls().get(0).getSource());
        assertEquals("http://orcid.org/8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retriveSourceUri());
        assertEquals("8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retrieveSourcePath());
    }
    
    private Object unmarshallFromPath(String path, Class<?> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Object result = null;
            if(ResearcherUrls.class.equals(type)) {
                result = (ResearcherUrls) obj;
            } else if(PersonalDetails.class.equals(type)) {
                result = (PersonalDetails) obj;
            } 
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }
    
    private Object unmarshall(Reader reader, Class<?> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }
}
