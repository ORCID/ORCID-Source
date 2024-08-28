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
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
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
public class JpaJaxbServiceAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbServiceAdapterV3")
    private JpaJaxbServiceAdapter adapter;

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
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Service e = getService();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = adapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        
        // General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());
        assertEquals("department-name", oar.getDepartment());
        assertEquals("role-title", oar.getTitle());

        // Dates
        assertEquals(Integer.valueOf(2), oar.getStartDate().getDay());
        assertEquals(Integer.valueOf(2), oar.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1948), oar.getStartDate().getYear());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getDay());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getMonth());
        assertEquals(Integer.valueOf(1948), oar.getEndDate().getYear());

        // Source
        assertNull(oar.getSourceId());
        assertNull(oar.getClientSourceId());
        assertNull(oar.getElementSourceId());
        assertEquals("http://tempuri.org", oar.getUrl());
    }

    @Test
    public void clearOrgAffiliationRelationEntityFieldsTest() throws JAXBException {
        Service e = getService();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = adapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);

        e.setUrl(null);
        adapter.toOrgAffiliationRelationEntity(e, oar);

        assertNotNull(oar);
        assertNull(oar.getUrl());

        // General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());
        assertEquals("department-name", oar.getDepartment());
        assertEquals("role-title", oar.getTitle());

        // Dates
        assertEquals(Integer.valueOf(2), oar.getStartDate().getDay());
        assertEquals(Integer.valueOf(2), oar.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1948), oar.getStartDate().getYear());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getDay());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getMonth());
        assertEquals(Integer.valueOf(1948), oar.getEndDate().getYear());

        // Source
        assertNull(oar.getSourceId());
        assertNull(oar.getClientSourceId());
        assertNull(oar.getElementSourceId());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToService() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Service service = adapter.toService(entity);
        assertNotNull(service);
        assertNotNull(service.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(service.getCreatedDate().getValue()));
        assertNotNull(service.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(service.getLastModifiedDate().getValue()));
        assertEquals("service:department", service.getDepartmentName());
        assertEquals(Long.valueOf(123456), service.getPutCode());
        assertEquals("service:title", service.getRoleTitle());
        assertEquals("private", service.getVisibility().value());
        assertNotNull(service.getStartDate());
        assertEquals("2000", service.getStartDate().getYear().getValue());
        assertEquals("01", service.getStartDate().getMonth().getValue());
        assertEquals("01", service.getStartDate().getDay().getValue());
        assertEquals("2020", service.getEndDate().getYear().getValue());
        assertEquals("02", service.getEndDate().getMonth().getValue());
        assertEquals("02", service.getEndDate().getDay().getValue());
        assertNotNull(service.getOrganization());
        assertEquals("org:name", service.getOrganization().getName());
        assertNotNull(service.getOrganization().getAddress());
        assertEquals("org:city", service.getOrganization().getAddress().getCity());
        assertEquals("org:region", service.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, service.getOrganization().getAddress().getCountry());
        assertNotNull(service.getSource());
        assertNotNull(service.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, service.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", service.getUrl().getValue());

        // no user obo
        assertNull(service.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToServiceSummary() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        ServiceSummary summary = adapter.toServiceSummary(entity);
        assertNotNull(summary);
        assertNotNull(summary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getCreatedDate().getValue()));
        assertNotNull(summary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getLastModifiedDate().getValue()));
        assertEquals("service:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("service:title", summary.getRoleTitle());
        assertEquals("private", summary.getVisibility().value());
        assertNotNull(summary.getStartDate());
        assertEquals("2000", summary.getStartDate().getYear().getValue());
        assertEquals("01", summary.getStartDate().getMonth().getValue());
        assertEquals("01", summary.getStartDate().getDay().getValue());
        assertEquals("2020", summary.getEndDate().getYear().getValue());
        assertEquals("02", summary.getEndDate().getMonth().getValue());
        assertEquals("02", summary.getEndDate().getDay().getValue());
        assertNotNull(summary.getSource());
        assertNotNull(summary.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, summary.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", summary.getUrl().getValue());

        // no user obo
        assertNull(summary.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBOService() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Service service = adapter.toService(entity);
        assertNotNull(service);
        assertNotNull(service.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(service.getCreatedDate().getValue()));
        assertNotNull(service.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(service.getLastModifiedDate().getValue()));
        assertEquals("service:department", service.getDepartmentName());
        assertEquals(Long.valueOf(123456), service.getPutCode());
        assertEquals("service:title", service.getRoleTitle());
        assertEquals("private", service.getVisibility().value());
        assertNotNull(service.getStartDate());
        assertEquals("2000", service.getStartDate().getYear().getValue());
        assertEquals("01", service.getStartDate().getMonth().getValue());
        assertEquals("01", service.getStartDate().getDay().getValue());
        assertEquals("2020", service.getEndDate().getYear().getValue());
        assertEquals("02", service.getEndDate().getMonth().getValue());
        assertEquals("02", service.getEndDate().getDay().getValue());
        assertNotNull(service.getOrganization());
        assertEquals("org:name", service.getOrganization().getName());
        assertNotNull(service.getOrganization().getAddress());
        assertEquals("org:city", service.getOrganization().getAddress().getCity());
        assertEquals("org:region", service.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, service.getOrganization().getAddress().getCountry());
        assertNotNull(service.getSource());
        assertNotNull(service.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, service.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", service.getUrl().getValue());

        // user obo
        assertNotNull(service.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBOServiceSummary() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        ServiceSummary summary = adapter.toServiceSummary(entity);
        assertNotNull(summary);
        assertNotNull(summary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getCreatedDate().getValue()));
        assertNotNull(summary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getLastModifiedDate().getValue()));
        assertEquals("service:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("service:title", summary.getRoleTitle());
        assertEquals("private", summary.getVisibility().value());
        assertNotNull(summary.getStartDate());
        assertEquals("2000", summary.getStartDate().getYear().getValue());
        assertEquals("01", summary.getStartDate().getMonth().getValue());
        assertEquals("01", summary.getStartDate().getDay().getValue());
        assertEquals("2020", summary.getEndDate().getYear().getValue());
        assertEquals("02", summary.getEndDate().getMonth().getValue());
        assertEquals("02", summary.getEndDate().getDay().getValue());
        assertNotNull(summary.getSource());
        assertNotNull(summary.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, summary.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", summary.getUrl().getValue());

        // user obo
        assertNotNull(summary.getSource().getAssertionOriginOrcid());
    }

    private Service getService() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Service.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0/samples/read_samples/service-3.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Service) unmarshaller.unmarshal(inputStream);
    }

    private OrgAffiliationRelationEntity getEntity() throws IllegalAccessException {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US.name());
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");

        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId(CLIENT_SOURCE_ID);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(clientDetailsEntity);
        orgEntity.setSource(sourceEntity);

        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        OrgAffiliationRelationEntity result = new OrgAffiliationRelationEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(result, date);
        result.setAffiliationType(AffiliationType.SERVICE.name());
        result.setDepartment("service:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setOrcid("0000-0001-0002-0003");
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("service:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setUrl("http://tempuri.org");
        return result;
    }
}
