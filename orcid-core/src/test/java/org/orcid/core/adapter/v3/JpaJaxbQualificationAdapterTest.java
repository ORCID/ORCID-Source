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
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
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
public class JpaJaxbQualificationAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbQualificationAdapterV3")
    private JpaJaxbQualificationAdapter adapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Qualification e = getQualification();
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
        Qualification e = getQualification();
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
    public void fromOrgAffiliationRelationEntityToQualification() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        Qualification qualification = adapter.toQualification(entity);
        assertNotNull(qualification);
        assertEquals("qualification:department", qualification.getDepartmentName());
        assertEquals(Long.valueOf(123456), qualification.getPutCode());
        assertEquals("qualification:title", qualification.getRoleTitle());
        assertEquals("private", qualification.getVisibility().value());
        assertNotNull(qualification.getStartDate());
        assertEquals("2000", qualification.getStartDate().getYear().getValue());
        assertEquals("01", qualification.getStartDate().getMonth().getValue());
        assertEquals("01", qualification.getStartDate().getDay().getValue());
        assertEquals("2020", qualification.getEndDate().getYear().getValue());
        assertEquals("02", qualification.getEndDate().getMonth().getValue());
        assertEquals("02", qualification.getEndDate().getDay().getValue());
        assertNotNull(qualification.getOrganization());
        assertEquals("org:name", qualification.getOrganization().getName());
        assertNotNull(qualification.getOrganization().getAddress());
        assertEquals("org:city", qualification.getOrganization().getAddress().getCity());
        assertEquals("org:region", qualification.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, qualification.getOrganization().getAddress().getCountry());
        assertNotNull(qualification.getSource());        
        assertNotNull(qualification.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, qualification.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",qualification.getUrl().getValue());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToQualificationSummary() {
        OrgAffiliationRelationEntity entity = getEntity();
        assertNotNull(entity);
        QualificationSummary summary = adapter.toQualificationSummary(entity);
        assertNotNull(summary);
        assertEquals("qualification:department", summary.getDepartmentName());
        assertEquals(Long.valueOf(123456), summary.getPutCode());
        assertEquals("qualification:title", summary.getRoleTitle());
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

    private Qualification getQualification() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Qualification.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/qualification-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Qualification) unmarshaller.unmarshal(inputStream);
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
        result.setAffiliationType(AffiliationType.QUALIFICATION.name());
        result.setDepartment("qualification:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("qualification:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());   
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setUrl("http://tempuri.org");
        return result;
    }
}
