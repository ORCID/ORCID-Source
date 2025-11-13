package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
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
public class JpaJaxbEmploymentAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbEmploymentAdapterV3")
    private JpaJaxbEmploymentAdapter jpaJaxbEmploymentAdapter;

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
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Employment e = getEmployment(true);
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        // General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), oar.getVisibility());
        assertEquals("employment:department-name", oar.getDepartment());
        assertEquals("employment:role-title", oar.getTitle());

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
        Employment e = getEmployment(true);
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());
        
        e.setUrl(null);
        jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(e, oar);

        assertNotNull(oar);
        assertNull(oar.getUrl());

        // General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());
        assertEquals("employment:department-name", oar.getDepartment());
        assertEquals("employment:role-title", oar.getTitle());

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
    public void fromOrgAffiliationRelationEntityToEmployment() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        Employment employment = jpaJaxbEmploymentAdapter.toEmployment(entity);
        assertNotNull(employment);
        assertNotNull(employment.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employment.getCreatedDate().getValue()));
        assertNotNull(employment.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employment.getLastModifiedDate().getValue()));
        assertEquals("employment:department", employment.getDepartmentName());
        assertEquals(Long.valueOf(123456), employment.getPutCode());
        assertEquals("employment:title", employment.getRoleTitle());
        assertEquals("private", employment.getVisibility().value());
        assertNotNull(employment.getStartDate());
        assertEquals("2000", employment.getStartDate().getYear().getValue());
        assertEquals("01", employment.getStartDate().getMonth().getValue());
        assertEquals("01", employment.getStartDate().getDay().getValue());
        assertEquals("2020", employment.getEndDate().getYear().getValue());
        assertEquals("02", employment.getEndDate().getMonth().getValue());
        assertEquals("02", employment.getEndDate().getDay().getValue());
        assertNotNull(employment.getOrganization());
        assertEquals("org:name", employment.getOrganization().getName());
        assertNotNull(employment.getOrganization().getAddress());
        assertEquals("org:city", employment.getOrganization().getAddress().getCity());
        assertEquals("org:region", employment.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.US, employment.getOrganization().getAddress().getCountry());
        assertNotNull(employment.getSource());
        assertNotNull(employment.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, employment.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", employment.getUrl().getValue());

        // no user obo
        assertNull(employment.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToEmploymentSummary() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        EmploymentSummary employmentSummary = jpaJaxbEmploymentAdapter.toEmploymentSummary(entity);
        assertNotNull(employmentSummary);
        assertNotNull(employmentSummary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employmentSummary.getCreatedDate().getValue()));
        assertNotNull(employmentSummary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employmentSummary.getLastModifiedDate().getValue()));
        assertEquals("employment:department", employmentSummary.getDepartmentName());
        assertEquals(Long.valueOf(123456), employmentSummary.getPutCode());
        assertEquals("employment:title", employmentSummary.getRoleTitle());
        assertEquals("private", employmentSummary.getVisibility().value());
        assertNotNull(employmentSummary.getStartDate());
        assertEquals("2000", employmentSummary.getStartDate().getYear().getValue());
        assertEquals("01", employmentSummary.getStartDate().getMonth().getValue());
        assertEquals("01", employmentSummary.getStartDate().getDay().getValue());
        assertEquals("2020", employmentSummary.getEndDate().getYear().getValue());
        assertEquals("02", employmentSummary.getEndDate().getMonth().getValue());
        assertEquals("02", employmentSummary.getEndDate().getDay().getValue());
        assertNotNull(employmentSummary.getSource());
        assertNotNull(employmentSummary.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, employmentSummary.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", employmentSummary.getUrl().getValue());

        // no user obo
        assertNull(employmentSummary.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBOEmployment() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.eq(CLIENT_SOURCE_ID))).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        Employment employment = jpaJaxbEmploymentAdapter.toEmployment(entity);
        assertNotNull(employment);
        assertNotNull(employment.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employment.getCreatedDate().getValue()));
        assertNotNull(employment.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employment.getLastModifiedDate().getValue()));
        assertEquals("employment:department", employment.getDepartmentName());
        assertEquals(Long.valueOf(123456), employment.getPutCode());
        assertEquals("employment:title", employment.getRoleTitle());
        assertEquals("private", employment.getVisibility().value());
        assertNotNull(employment.getStartDate());
        assertEquals("2000", employment.getStartDate().getYear().getValue());
        assertEquals("01", employment.getStartDate().getMonth().getValue());
        assertEquals("01", employment.getStartDate().getDay().getValue());
        assertEquals("2020", employment.getEndDate().getYear().getValue());
        assertEquals("02", employment.getEndDate().getMonth().getValue());
        assertEquals("02", employment.getEndDate().getDay().getValue());
        assertNotNull(employment.getOrganization());
        assertEquals("org:name", employment.getOrganization().getName());
        assertNotNull(employment.getOrganization().getAddress());
        assertEquals("org:city", employment.getOrganization().getAddress().getCity());
        assertEquals("org:region", employment.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.US, employment.getOrganization().getAddress().getCountry());
        assertNotNull(employment.getSource());
        assertNotNull(employment.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, employment.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", employment.getUrl().getValue());

        // user obo
        assertNotNull(employment.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBOEmploymentSummary() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.eq(CLIENT_SOURCE_ID))).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        EmploymentSummary employmentSummary = jpaJaxbEmploymentAdapter.toEmploymentSummary(entity);
        assertNotNull(employmentSummary);
        assertNotNull(employmentSummary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employmentSummary.getCreatedDate().getValue()));
        assertNotNull(employmentSummary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employmentSummary.getLastModifiedDate().getValue()));
        assertEquals("employment:department", employmentSummary.getDepartmentName());
        assertEquals(Long.valueOf(123456), employmentSummary.getPutCode());
        assertEquals("employment:title", employmentSummary.getRoleTitle());
        assertEquals("private", employmentSummary.getVisibility().value());
        assertNotNull(employmentSummary.getStartDate());
        assertEquals("2000", employmentSummary.getStartDate().getYear().getValue());
        assertEquals("01", employmentSummary.getStartDate().getMonth().getValue());
        assertEquals("01", employmentSummary.getStartDate().getDay().getValue());
        assertEquals("2020", employmentSummary.getEndDate().getYear().getValue());
        assertEquals("02", employmentSummary.getEndDate().getMonth().getValue());
        assertEquals("02", employmentSummary.getEndDate().getDay().getValue());
        assertNotNull(employmentSummary.getSource());
        assertNotNull(employmentSummary.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, employmentSummary.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", employmentSummary.getUrl().getValue());

        // user obo
        assertNotNull(employmentSummary.getSource().getAssertionOriginOrcid());
    }

    private Employment getEmployment(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Employment.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Marshaller m = context.createMarshaller();
        String name = "/record_3.0/samples/read_samples/employment-3.0.xml";
        if (full) {
            name = "/record_3.0/samples/read_samples/employment-full-3.0.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        Employment e = (Employment) unmarshaller.unmarshal(inputStream);

        StringWriter stringWriter = new StringWriter();
        m.marshal(e, stringWriter);
        System.out.println(stringWriter.toString());

        return e;
    }

    private OrgAffiliationRelationEntity getEmploymentEntity() throws IllegalAccessException {
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
        result.setAffiliationType(AffiliationType.EMPLOYMENT.name());
        result.setDepartment("employment:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setOrcid("0000-0001-0002-0003");
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("employment:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setUrl("http://tempuri.org");
        return result;
    }
}
