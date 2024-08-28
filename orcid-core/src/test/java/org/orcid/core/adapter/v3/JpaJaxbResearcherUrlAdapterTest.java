package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbResearcherUrlAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbResearcherUrlAdapterV3")
    private JpaJaxbResearcherUrlAdapter jpaJaxbResearcherUrlAdapter;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManager;
    
    @Mock
    private ClientDetailsManager mockClientDetailsManager;
    
    @Mock
    private RecordNameDao mockRecordNameDao;
    
    @Mock
    private RecordNameManagerReadOnly mockRecordNameManager;
    
    @Before
    public void setUp() {
        // by default return client details entity with user obo disabled
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(new ClientDetailsEntity());
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", mockClientDetailsManager);
        
        Mockito.when(mockRecordNameDao.exists(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockRecordNameManager.fetchDisplayablePublicName(Mockito.anyString())).thenReturn("test");
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", mockRecordNameDao);
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", mockRecordNameManager);
    }

    @After
    public void tearDown() {
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", clientDetailsManager);        
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", recordNameDao);        
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", recordNameManager);   
    }
    
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
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals(Visibility.PUBLIC.name(), entity.getVisibility());        
        assertEquals("http://site1.com/", entity.getUrl());
        assertEquals("Site # 1", entity.getUrlName());                
        // Source
        assertNull(entity.getSourceId());        
        assertNull(entity.getClientSourceId());        
        assertNull(entity.getElementSourceId());     
    }

    @Test
    public void fromResearcherUrlEntityToResearcherUrl() throws IllegalAccessException {
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        ResearcherUrl r = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);
        //General info
        assertNotNull(r);
        assertNotNull(r.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getCreatedDate().getValue()));
        assertNotNull(r.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getLastModifiedDate().getValue()));
        assertEquals(Long.valueOf(13579), r.getPutCode());
        assertEquals("http://orcid.org", r.getUrl().getValue());
        assertEquals("Orcid URL", r.getUrlName());
        assertEquals(Visibility.LIMITED, r.getVisibility());
        //Source
        assertEquals(CLIENT_SOURCE_ID, r.getSource().retrieveSourcePath());
        
        // no user obo
        assertNull(r.getSource().getAssertionOriginOrcid());
    }      
    
    @Test
    public void fromResearcherUrlEntityToUserOBOResearcherUrl() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);
        
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        entity.setOrcid("orcid");
        
        ResearcherUrl r = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);
        //General info
        assertNotNull(r);
        assertNotNull(r.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getCreatedDate().getValue()));
        assertNotNull(r.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getLastModifiedDate().getValue()));
        assertEquals(Long.valueOf(13579), r.getPutCode());
        assertEquals("http://orcid.org", r.getUrl().getValue());
        assertEquals("Orcid URL", r.getUrlName());
        assertEquals(Visibility.LIMITED, r.getVisibility());
        //Source
        assertEquals(CLIENT_SOURCE_ID, r.getSource().retrieveSourcePath());
        
        // user obo
        assertNotNull(r.getSource().getAssertionOriginOrcid());
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
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        return entity;
    }
}
