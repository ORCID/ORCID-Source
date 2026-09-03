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
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
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
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbMembershipAdapterImpl;
import org.orcid.core.adapter.MockedMapStructAdapters;

/**
 *
 * @author Angel Montenegro
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbMembershipAdapterTest {

    private static final String CLIENT_SOURCE_ID = MockedMapStructAdapters.CLIENT_SOURCE_ID;

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbMembershipAdapter adapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        adapter = adapters.get(JpaJaxbMembershipAdapterImpl.class);
    }

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Membership e = getMembership();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = adapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());

        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());
        assertEquals("department-name", oar.getDepartment());
        assertEquals("role-title", oar.getTitle());

        //Dates
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
        assertEquals("http://tempuri.org",oar.getUrl());
    }

    @Test
    public void clearOrgAffiliationRelationEntityFieldsTest() throws JAXBException {
        Membership e = getMembership();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = adapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);

        e.setUrl(null);
        adapter.toOrgAffiliationRelationEntity(e, oar);

        assertNotNull(oar);
        assertNull(oar.getUrl());

        //General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());
        assertEquals("department-name", oar.getDepartment());
        assertEquals("role-title", oar.getTitle());

        //Dates
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
    public void fromOrgAffiliationRelationEntityToMembership() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Membership membership = adapter.toMembership(entity);
        assertNotNull(membership);
        assertNotNull(membership.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(membership.getCreatedDate().getValue()));
        assertNotNull(membership.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(membership.getLastModifiedDate().getValue()));
        assertEquals("membership:department", membership.getDepartmentName());
        assertEquals(Long.valueOf(123456), membership.getPutCode());
        assertEquals("membership:title", membership.getRoleTitle());
        assertEquals("private", membership.getVisibility().value());
        assertNotNull(membership.getStartDate());
        assertEquals("2000", membership.getStartDate().getYear().getValue());
        assertEquals("01", membership.getStartDate().getMonth().getValue());
        assertEquals("01", membership.getStartDate().getDay().getValue());
        assertEquals("2020", membership.getEndDate().getYear().getValue());
        assertEquals("02", membership.getEndDate().getMonth().getValue());
        assertEquals("02", membership.getEndDate().getDay().getValue());
        assertNotNull(membership.getOrganization());
        assertEquals("org:name", membership.getOrganization().getName());
        assertNotNull(membership.getOrganization().getAddress());
        assertEquals("org:city", membership.getOrganization().getAddress().getCity());
        assertEquals("org:region", membership.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, membership.getOrganization().getAddress().getCountry());
        assertNotNull(membership.getSource());
        assertNotNull(membership.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, membership.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",membership.getUrl().getValue());

        // no user obo
        assertNull(membership.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToMembershipSummary() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        MembershipSummary summary = adapter.toMembershipSummary(entity);
        assertNotNull(summary);
        assertNotNull(summary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getCreatedDate().getValue()));
        assertNotNull(summary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getLastModifiedDate().getValue()));
        assertEquals("membership:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("membership:title", summary.getRoleTitle());
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
        assertEquals("http://tempuri.org",summary.getUrl().getValue());

        // no user obo
        assertNull(summary.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBOMembership() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(Mockito.anyString())).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Membership membership = adapter.toMembership(entity);
        assertNotNull(membership);
        assertNotNull(membership.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(membership.getCreatedDate().getValue()));
        assertNotNull(membership.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(membership.getLastModifiedDate().getValue()));
        assertEquals("membership:department", membership.getDepartmentName());
        assertEquals(Long.valueOf(123456), membership.getPutCode());
        assertEquals("membership:title", membership.getRoleTitle());
        assertEquals("private", membership.getVisibility().value());
        assertNotNull(membership.getStartDate());
        assertEquals("2000", membership.getStartDate().getYear().getValue());
        assertEquals("01", membership.getStartDate().getMonth().getValue());
        assertEquals("01", membership.getStartDate().getDay().getValue());
        assertEquals("2020", membership.getEndDate().getYear().getValue());
        assertEquals("02", membership.getEndDate().getMonth().getValue());
        assertEquals("02", membership.getEndDate().getDay().getValue());
        assertNotNull(membership.getOrganization());
        assertEquals("org:name", membership.getOrganization().getName());
        assertNotNull(membership.getOrganization().getAddress());
        assertEquals("org:city", membership.getOrganization().getAddress().getCity());
        assertEquals("org:region", membership.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, membership.getOrganization().getAddress().getCountry());
        assertNotNull(membership.getSource());
        assertNotNull(membership.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, membership.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",membership.getUrl().getValue());

        // user obo
        assertNotNull(membership.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToUserOBOMembershipSummary() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(Mockito.anyString())).thenReturn(userOBOClient);

        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        MembershipSummary summary = adapter.toMembershipSummary(entity);
        assertNotNull(summary);
        assertNotNull(summary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getCreatedDate().getValue()));
        assertNotNull(summary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getLastModifiedDate().getValue()));
        assertEquals("membership:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("membership:title", summary.getRoleTitle());
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
        assertEquals("http://tempuri.org",summary.getUrl().getValue());

        // user obo
        assertNotNull(summary.getSource().getAssertionOriginOrcid());
    }

    private Membership getMembership() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Membership.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0/samples/read_samples/membership-3.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Membership) unmarshaller.unmarshal(inputStream);
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

        result.setAffiliationType(AffiliationType.MEMBERSHIP.name());
        result.setDepartment("membership:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setOrcid("0000-0001-0002-0003");
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("membership:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setUrl("http://tempuri.org");
        return result;
    }
}
