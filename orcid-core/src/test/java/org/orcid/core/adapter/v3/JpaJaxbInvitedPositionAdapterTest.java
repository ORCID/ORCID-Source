package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbInvitedPositionAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbInvitedPositionAdapterV3")
    private JpaJaxbInvitedPositionAdapter adapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        InvitedPosition e = getInvitedPosition();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = adapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
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
        InvitedPosition e = getInvitedPosition();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = adapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        
        e.setUrl(null);
        adapter.toOrgAffiliationRelationEntity(e, oar);
        
        assertNotNull(oar);
        assertNull(oar.getUrl());
        
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
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
    public void fromOrgAffiliationRelationEntityToInvitedPosition() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        InvitedPosition invitedPosition = adapter.toInvitedPosition(entity);
        assertNotNull(invitedPosition);
        assertEquals("invited-position:department", invitedPosition.getDepartmentName());
        assertEquals(Long.valueOf(123456), invitedPosition.getPutCode());
        assertEquals("invited-position:title", invitedPosition.getRoleTitle());
        assertEquals("private", invitedPosition.getVisibility().value());
        assertNotNull(invitedPosition.getStartDate());
        assertEquals("2000", invitedPosition.getStartDate().getYear().getValue());
        assertEquals("01", invitedPosition.getStartDate().getMonth().getValue());
        assertEquals("01", invitedPosition.getStartDate().getDay().getValue());
        assertEquals("2020", invitedPosition.getEndDate().getYear().getValue());
        assertEquals("02", invitedPosition.getEndDate().getMonth().getValue());
        assertEquals("02", invitedPosition.getEndDate().getDay().getValue());
        assertNotNull(invitedPosition.getOrganization());
        assertEquals("org:name", invitedPosition.getOrganization().getName());
        assertNotNull(invitedPosition.getOrganization().getAddress());
        assertEquals("org:city", invitedPosition.getOrganization().getAddress().getCity());
        assertEquals("org:region", invitedPosition.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, invitedPosition.getOrganization().getAddress().getCountry());
        assertNotNull(invitedPosition.getSource());        
        assertNotNull(invitedPosition.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, invitedPosition.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",invitedPosition.getUrl().getValue());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToInvitedPositionSummary() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        InvitedPositionSummary summary = adapter.toInvitedPositionSummary(entity);
        assertNotNull(summary);
        assertEquals("invited-position:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("invited-position:title", summary.getRoleTitle());
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
    }

    private InvitedPosition getInvitedPosition() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { InvitedPosition.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/invited-position-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (InvitedPosition) unmarshaller.unmarshal(inputStream);
    }
    
    private OrgAffiliationRelationEntity getEntity() {
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
        
        OrgAffiliationRelationEntity result = new OrgAffiliationRelationEntity();
        result.setAffiliationType(AffiliationType.INVITED_POSITION.name());
        result.setDepartment("invited-position:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("invited-position:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());   
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setUrl("http://tempuri.org");
        return result;
    }
}
