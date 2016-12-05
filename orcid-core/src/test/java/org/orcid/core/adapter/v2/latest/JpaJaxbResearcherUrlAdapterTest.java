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
package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbResearcherUrlAdapterTest extends MockSourceNameCache {

    @Resource
    private JpaJaxbResearcherUrlAdapter jpaJaxbResearcherUrlAdapter;

    @Test
    public void testToResearcherUrlEntity() throws JAXBException {
        ResearcherUrls rUrls = getResearcherUrls();
        assertNotNull(rUrls);
        assertNotNull(rUrls.getResearcherUrls());
        assertEquals(1, rUrls.getResearcherUrls().size());
        ResearcherUrlEntity entity = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(rUrls.getResearcherUrls().get(0));
        assertNotNull(entity);
        //General info
        assertEquals(Long.valueOf(1248), entity.getId());
        assertEquals(Visibility.PUBLIC.value(), entity.getVisibility().value());        
        assertEquals("http://site1.com/", entity.getUrl());
        assertEquals("Site # 1", entity.getUrlName());                
        //Source        
        assertEquals("8888-8888-8888-8880", entity.getElementSourceId());  
    }

    @Test
    public void fromResearcherUrlEntityToResearcherUrl() {
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        ResearcherUrl r = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);
        //General info
        assertNotNull(r);
        assertEquals(Long.valueOf(13579), r.getPutCode());
        assertEquals("http://orcid.org", r.getUrl().getValue());
        assertEquals("Orcid URL", r.getUrlName());
        assertEquals(Visibility.LIMITED, r.getVisibility());
        //Source
        assertEquals("APP-0001", r.getSource().retrieveSourcePath());
    }      
    
    private ResearcherUrls getResearcherUrls() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { ResearcherUrls.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc4/samples/researcher-urls-2.0_rc4.xml";             
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (ResearcherUrls) unmarshaller.unmarshal(inputStream);
    }
    
    private ResearcherUrlEntity getResearcherUrlEntity() {
        ResearcherUrlEntity entity = new ResearcherUrlEntity();
        entity.setId(13579L);
        entity.setClientSourceId("APP-0001");
        entity.setUrl("http://orcid.org");
        entity.setUrlName("Orcid URL");
        entity.setVisibility(Visibility.LIMITED);
        return entity;
    }
}
