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
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.v3.JpaJaxbExternalIdentifierAdapter;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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
public class JpaJaxbExternalIdentifierAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbExternalIdentifierAdapterV3")
    private JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;

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
        MockitoAnnotations.initMocks(this);

        // by default return client details entity with user obo disabled
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.eq(CLIENT_SOURCE_ID))).thenReturn(new ClientDetailsEntity());
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
    public void testToExternalIdentifierEntity() throws JAXBException {
        ExternalIdentifierEntity entity = jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(getExternalIdentifier());
        assertNotNull(entity);
        assertEquals("A-0003", entity.getExternalIdCommonName());
        assertEquals("A-0003", entity.getExternalIdReference());
        assertEquals("http://ext-id/A-0003", entity.getExternalIdUrl());
        assertEquals(Long.valueOf(1), entity.getId());
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());

        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());

        // Check url get removed on entity when it comes null in model object
        PersonExternalIdentifier pei = getExternalIdentifier();
        pei.setUrl(null);

        jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(pei, entity);
        assertNotNull(entity);
        assertNull(entity.getExternalIdUrl());
        assertEquals("A-0003", entity.getExternalIdCommonName());
        assertEquals("A-0003", entity.getExternalIdReference());
        assertEquals(Long.valueOf(1), entity.getId());
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());

        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());
    }

    @Test
    public void fromExternalIdentifierEntityToExternalIdentifier() throws IllegalAccessException {
        ExternalIdentifierEntity entity = getExternalIdentifierEntity();
        PersonExternalIdentifier extId = jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
        assertNotNull(extId);
        assertNotNull(extId.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getCreatedDate().getValue()));
        assertNotNull(extId.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getLastModifiedDate().getValue()));
        assertEquals("common-name", extId.getType());
        assertEquals("id-reference", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://myurl.com", extId.getUrl().getValue());
        assertEquals(Long.valueOf(123), extId.getPutCode());
        assertNotNull(extId.getSource());
        assertEquals(CLIENT_SOURCE_ID, extId.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());

        // no user obo
        assertNull(extId.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromExternalIdentifierEntityToUserOBOExternalIdentifier() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.eq(CLIENT_SOURCE_ID))).thenReturn(userOBOClient);

        ExternalIdentifierEntity entity = getExternalIdentifierEntity();
        PersonExternalIdentifier extId = jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
        assertNotNull(extId);
        assertNotNull(extId.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getCreatedDate().getValue()));
        assertNotNull(extId.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getLastModifiedDate().getValue()));
        assertEquals("common-name", extId.getType());
        assertEquals("id-reference", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://myurl.com", extId.getUrl().getValue());
        assertEquals(Long.valueOf(123), extId.getPutCode());
        assertNotNull(extId.getSource());
        assertEquals(CLIENT_SOURCE_ID, extId.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());

        // user obo
        assertNotNull(extId.getSource().getAssertionOriginOrcid());
    }

    private PersonExternalIdentifier getExternalIdentifier() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { PersonExternalIdentifier.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/external-identifier-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (PersonExternalIdentifier) unmarshaller.unmarshal(inputStream);
    }

    private ExternalIdentifierEntity getExternalIdentifierEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setExternalIdCommonName("common-name");
        entity.setExternalIdReference("id-reference");
        entity.setExternalIdUrl("http://myurl.com");
        entity.setId(123L);
        entity.setClientSourceId(CLIENT_SOURCE_ID);
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        entity.setOrcid("orcid");

        return entity;
    }
}
