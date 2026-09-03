package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbDistinctionAdapterImpl;
import org.orcid.core.adapter.MockedMapStructAdapters;

/**
 *
 * @author Angel Montenegro
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbDistinctionAdapterTest {

    private static final String CLIENT_SOURCE_ID = MockedMapStructAdapters.CLIENT_SOURCE_ID;

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbDistinctionAdapter adapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        adapter = adapters.get(JpaJaxbDistinctionAdapterImpl.class);
    }

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Distinction e = getDistinction();
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
        Distinction e = getDistinction();
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
    public void fromOrgAffiliationRelationEntityToDistinction() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Distinction distinction = adapter.toDistinction(entity);
        assertNotNull(distinction);
        assertNotNull(distinction.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(distinction.getCreatedDate().getValue()));
        assertNotNull(distinction.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(distinction.getLastModifiedDate().getValue()));
        assertEquals("distinction:department", distinction.getDepartmentName());
        assertEquals(Long.valueOf(123456), distinction.getPutCode());
        assertEquals("distinction:title", distinction.getRoleTitle());
        assertEquals("private", distinction.getVisibility().value());
        assertNotNull(distinction.getStartDate());
        assertEquals("2000", distinction.getStartDate().getYear().getValue());
        assertEquals("01", distinction.getStartDate().getMonth().getValue());
        assertEquals("01", distinction.getStartDate().getDay().getValue());
        assertEquals("2020", distinction.getEndDate().getYear().getValue());
        assertEquals("02", distinction.getEndDate().getMonth().getValue());
        assertEquals("02", distinction.getEndDate().getDay().getValue());
        assertNotNull(distinction.getOrganization());
        assertEquals("org:name", distinction.getOrganization().getName());
        assertNotNull(distinction.getOrganization().getAddress());
        assertEquals("org:city", distinction.getOrganization().getAddress().getCity());
        assertEquals("org:region", distinction.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, distinction.getOrganization().getAddress().getCountry());
        assertNotNull(distinction.getSource());
        assertNotNull(distinction.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, distinction.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", distinction.getUrl().getValue());

        // not a user obo work
        assertNull(distinction.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBODistinction() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(CLIENT_SOURCE_ID)).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Distinction distinction = adapter.toDistinction(entity);
        assertNotNull(distinction);
        assertNotNull(distinction.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(distinction.getCreatedDate().getValue()));
        assertNotNull(distinction.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(distinction.getLastModifiedDate().getValue()));
        assertEquals("distinction:department", distinction.getDepartmentName());
        assertEquals(Long.valueOf(123456), distinction.getPutCode());
        assertEquals("distinction:title", distinction.getRoleTitle());
        assertEquals("private", distinction.getVisibility().value());
        assertNotNull(distinction.getStartDate());
        assertEquals("2000", distinction.getStartDate().getYear().getValue());
        assertEquals("01", distinction.getStartDate().getMonth().getValue());
        assertEquals("01", distinction.getStartDate().getDay().getValue());
        assertEquals("2020", distinction.getEndDate().getYear().getValue());
        assertEquals("02", distinction.getEndDate().getMonth().getValue());
        assertEquals("02", distinction.getEndDate().getDay().getValue());
        assertNotNull(distinction.getOrganization());
        assertEquals("org:name", distinction.getOrganization().getName());
        assertNotNull(distinction.getOrganization().getAddress());
        assertEquals("org:city", distinction.getOrganization().getAddress().getCity());
        assertEquals("org:region", distinction.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, distinction.getOrganization().getAddress().getCountry());
        assertNotNull(distinction.getSource());
        assertNotNull(distinction.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, distinction.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org", distinction.getUrl().getValue());

        // user obo work
        assertNotNull(distinction.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToDistinctionSummary() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        DistinctionSummary summary = adapter.toDistinctionSummary(entity);
        assertNotNull(summary);
        assertNotNull(summary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getCreatedDate().getValue()));
        assertNotNull(summary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getLastModifiedDate().getValue()));
        assertEquals("distinction:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("distinction:title", summary.getRoleTitle());
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

        // not a user obo work
        assertNull(summary.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBODistinctionSummary() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(CLIENT_SOURCE_ID)).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        DistinctionSummary summary = adapter.toDistinctionSummary(entity);
        assertNotNull(summary);
        assertNotNull(summary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getCreatedDate().getValue()));
        assertNotNull(summary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getLastModifiedDate().getValue()));
        assertEquals("distinction:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("distinction:title", summary.getRoleTitle());
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

        // user obo work
        assertNotNull(summary.getSource().getAssertionOriginOrcid());
    }

    private Distinction getDistinction() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Distinction.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0/samples/read_samples/distinction-3.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Distinction) unmarshaller.unmarshal(inputStream);
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
        result.setAffiliationType(AffiliationType.DISTINCTION.name());
        result.setDepartment("distinction:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setOrcid("0000-0001-0002-0003");
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("distinction:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setUrl("http://tempuri.org");

        return result;
    }
}
