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
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.summary.ServiceSummary;
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
public class JpaJaxbServiceAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbServiceAdapterV3")
    private JpaJaxbServiceAdapter adapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Service e = getService();
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
    public void fromOrgAffiliationRelationEntityToService() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Service service = adapter.toService(entity);
        assertNotNull(service);
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
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Iso3166Country.US, service.getOrganization().getAddress().getCountry());
        assertNotNull(service.getSource());        
        assertNotNull(service.getSource().retrieveSourcePath());
        assertEquals("APP-000000001", service.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",service.getUrl().getValue());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToServiceSummary() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        ServiceSummary summary = adapter.toServiceSummary(entity);
        assertNotNull(summary);
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
        assertEquals("APP-000000001", summary.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",summary.getUrl().getValue());
    }

    private Service getService() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Service.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/service-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Service) unmarshaller.unmarshal(inputStream);
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
        result.setAffiliationType(AffiliationType.SERVICE.name());
        result.setDepartment("service:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("service:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());   
        result.setClientSourceId("APP-000000001");
        result.setUrl("http://tempuri.org");
        return result;
    }
}
