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
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.summary.DistinctionSummary;
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
public class JpaJaxbDistinctionAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbDistinctionAdapterV3")
    private JpaJaxbDistinctionAdapter adapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Distinction e = getDistinction();
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
    public void fromOrgAffiliationRelationEntityToDistinction() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Distinction distinction = adapter.toDistinction(entity);
        assertNotNull(distinction);
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
        assertEquals("APP-000000001", distinction.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",distinction.getUrl().getValue());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToDistinctionSummary() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        DistinctionSummary summary = adapter.toDistinctionSummary(entity);
        assertNotNull(summary);
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
        assertEquals("APP-000000001", summary.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",summary.getUrl().getValue());
    }

    private Distinction getDistinction() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Distinction.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/distinction-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Distinction) unmarshaller.unmarshal(inputStream);
    }
    
    private OrgAffiliationRelationEntity getEntity() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US.name());
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId("APP-000000001");

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(clientDetailsEntity);
        orgEntity.setSource(sourceEntity);
        
        OrgAffiliationRelationEntity result = new OrgAffiliationRelationEntity();
        result.setAffiliationType(AffiliationType.DISTINCTION.name());
        result.setDepartment("distinction:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("distinction:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());   
        result.setClientSourceId("APP-000000001");
        result.setUrl("http://tempuri.org");
        
        return result;
    }
}
