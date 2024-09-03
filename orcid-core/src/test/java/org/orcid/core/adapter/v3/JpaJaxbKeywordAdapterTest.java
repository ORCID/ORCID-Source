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
import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbKeywordAdapterTest extends MockSourceNameCache {
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private JpaJaxbKeywordAdapter adapter;
    
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
    public void fromKeywordToProfileKeywordEntityTest() throws JAXBException {
        Keyword keyword = getKeyword();
        ProfileKeywordEntity entity = adapter.toProfileKeywordEntity(keyword);
        assertNotNull(entity);
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals(Long.valueOf(1), entity.getId());
        assertEquals("keyword1", entity.getKeywordName());        
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name(), entity.getVisibility());
        
        // Source
        assertNull(entity.getSourceId());        
        assertNull(entity.getClientSourceId());        
        assertNull(entity.getElementSourceId());
    }
    
    @Test
    public void fromProfileKeywordEntityToKeywordTest() throws IllegalAccessException {
        ProfileKeywordEntity entity = getProfileKeywordEntity();
        Keyword keyword = adapter.toKeyword(entity);
        assertNotNull(keyword);
        assertNotNull(keyword.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getCreatedDate().getValue()));
        assertNotNull(keyword.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getLastModifiedDate().getValue()));
        assertEquals("keyword-1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertNotNull(keyword.getSource());
        assertEquals(CLIENT_SOURCE_ID, keyword.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        
        // no user obo
        assertNull(keyword.getSource().getAssertionOriginOrcid());
    }
    
    @Test
    public void fromProfileKeywordEntityToUserOBOKeywordTest() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);
        
        ProfileKeywordEntity entity = getProfileKeywordEntity();
        Keyword keyword = adapter.toKeyword(entity);
        assertNotNull(keyword);
        assertNotNull(keyword.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getCreatedDate().getValue()));
        assertNotNull(keyword.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getLastModifiedDate().getValue()));
        assertEquals("keyword-1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertNotNull(keyword.getSource());
        assertEquals(CLIENT_SOURCE_ID, keyword.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        
        // user obo
        assertNotNull(keyword.getSource().getAssertionOriginOrcid());
    }
    
    private Keyword getKeyword() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Keyword.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0/samples/read_samples/keyword-3.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Keyword) unmarshaller.unmarshal(inputStream); 
    }
    
    private ProfileKeywordEntity getProfileKeywordEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setId(Long.valueOf(1));
        entity.setKeywordName("keyword-1");
        entity.setOrcid("0000-0000-0000-0000");
        entity.setClientSourceId(CLIENT_SOURCE_ID);
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        return entity;
    }
}
