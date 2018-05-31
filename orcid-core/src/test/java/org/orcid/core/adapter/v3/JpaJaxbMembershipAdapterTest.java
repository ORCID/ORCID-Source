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
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
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
public class JpaJaxbMembershipAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbMembershipAdapterV3")
    private JpaJaxbMembershipAdapter adapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Membership e = getMembership();
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
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToMembership() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Membership membership = adapter.toMembership(entity);
        assertNotNull(membership);
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
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, membership.getOrganization().getAddress().getCountry());
        assertNotNull(membership.getSource());        
        assertNotNull(membership.getSource().retrieveSourcePath());
        assertEquals("APP-000000001", membership.getSource().retrieveSourcePath());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToMembershipSummary() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        MembershipSummary summary = adapter.toMembershipSummary(entity);
        assertNotNull(summary);
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
        assertEquals("APP-000000001", summary.getSource().retrieveSourcePath());
    }

    private Membership getMembership() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Membership.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/membership-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Membership) unmarshaller.unmarshal(inputStream);
    }
    
    private OrgAffiliationRelationEntity getEntity() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US.name());
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        orgEntity.setSource(new SourceEntity("APP-000000001"));
        
        OrgAffiliationRelationEntity result = new OrgAffiliationRelationEntity();
        result.setAffiliationType(AffiliationType.MEMBERSHIP.name());
        result.setDepartment("membership:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("membership:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());   
        result.setClientSourceId("APP-000000001");
        
        return result;
    }
}
