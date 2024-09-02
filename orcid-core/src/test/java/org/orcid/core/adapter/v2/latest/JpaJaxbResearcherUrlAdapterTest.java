package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbResearcherUrlAdapterTest extends MockSourceNameCache {

    @Resource
    private JpaJaxbResearcherUrlAdapter jpaJaxbResearcherUrlAdapter;

    @Test
    public void testToResearcherUrlEntity() throws JAXBException {
        ResearcherUrls rUrls = getResearcherUrls();
        assertNotNull(rUrls);
        assertNotNull(rUrls.getResearcherUrls());
        assertEquals(1, rUrls.getResearcherUrls().size());
        assertNotNull(rUrls.getResearcherUrls().get(0).getCreatedDate());
        assertNotNull(rUrls.getResearcherUrls().get(0).getLastModifiedDate());
        ResearcherUrlEntity entity = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(rUrls.getResearcherUrls().get(0));
        assertNotNull(entity);
        //General info
        assertEquals(Long.valueOf(1248), entity.getId());
        assertEquals(Visibility.PUBLIC.name(), entity.getVisibility());        
        assertEquals("http://site1.com/", entity.getUrl());
        assertEquals("Site # 1", entity.getUrlName());                
        // Source
        assertNull(entity.getSourceId());        
        assertNull(entity.getClientSourceId());        
        assertNull(entity.getElementSourceId());
        // Dates are null
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
    }

    @Test
    public void fromResearcherUrlEntityToResearcherUrl() throws IllegalAccessException {
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        ResearcherUrl r = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);
        //General info
        assertNotNull(r);
        assertEquals(Long.valueOf(13579), r.getPutCode());
        assertEquals("http://orcid.org", r.getUrl().getValue());
        assertEquals("Orcid URL", r.getUrlName());
        assertEquals(Visibility.LIMITED, r.getVisibility());
        assertNotNull(r.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getCreatedDate().getValue()));
        assertNotNull(r.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getLastModifiedDate().getValue()));
        
        //Source
        assertEquals(CLIENT_SOURCE_ID, r.getSource().retrieveSourcePath());
    }      
    
    private ResearcherUrls getResearcherUrls() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { ResearcherUrls.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/researcher-urls-2.0.xml";             
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (ResearcherUrls) unmarshaller.unmarshal(inputStream);
    }
    
    private ResearcherUrlEntity getResearcherUrlEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ResearcherUrlEntity entity = new ResearcherUrlEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setId(13579L);
        entity.setClientSourceId(CLIENT_SOURCE_ID);
        entity.setUrl("http://orcid.org");
        entity.setUrlName("Orcid URL");
        entity.setVisibility(Visibility.LIMITED.name());
        return entity;
    }
}
