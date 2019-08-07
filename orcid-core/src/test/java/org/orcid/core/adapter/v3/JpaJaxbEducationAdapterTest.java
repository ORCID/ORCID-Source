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
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
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
public class JpaJaxbEducationAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbEducationAdapterV3")
    private JpaJaxbEducationAdapter jpaJaxbEducationAdapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Education e = getEducation(true);
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());        
        assertEquals("education:department-name", oar.getDepartment());
        assertEquals("education:role-title", oar.getTitle());
        
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
        Education e = getEducation(true);
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        assertEquals("http://tempuri.org",oar.getUrl());
        
        // Clear the url
        e.setUrl(null);
        
        jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(e, oar);
        assertNotNull(oar);
        
        // Check url is null
        assertNull(oar.getUrl());
        
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());        
        assertEquals("education:department-name", oar.getDepartment());
        assertEquals("education:role-title", oar.getTitle());
        
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
    public void fromOrgAffiliationRelationEntityToEducation() {
        OrgAffiliationRelationEntity entity = getEducationEntity();
        assertNotNull(entity);
        Education education = jpaJaxbEducationAdapter.toEducation(entity);
        assertNotNull(education);
        assertEquals("education:department", education.getDepartmentName());
        assertEquals(Long.valueOf(123456), education.getPutCode());
        assertEquals("education:title", education.getRoleTitle());
        assertEquals("private", education.getVisibility().value());
        assertNotNull(education.getStartDate());
        assertEquals("2000", education.getStartDate().getYear().getValue());
        assertEquals("01", education.getStartDate().getMonth().getValue());
        assertEquals("01", education.getStartDate().getDay().getValue());
        assertEquals("2020", education.getEndDate().getYear().getValue());
        assertEquals("02", education.getEndDate().getMonth().getValue());
        assertEquals("02", education.getEndDate().getDay().getValue());
        assertNotNull(education.getOrganization());
        assertEquals("org:name", education.getOrganization().getName());
        assertNotNull(education.getOrganization().getAddress());
        assertEquals("org:city", education.getOrganization().getAddress().getCity());
        assertEquals("org:region", education.getOrganization().getAddress().getRegion());
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, education.getOrganization().getAddress().getCountry());
        assertNotNull(education.getSource());        
        assertNotNull(education.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, education.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",education.getUrl().getValue());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToEducationSummary() {
        OrgAffiliationRelationEntity entity = getEducationEntity();
        assertNotNull(entity);
        EducationSummary educationSummary = jpaJaxbEducationAdapter.toEducationSummary(entity);
        assertNotNull(educationSummary);
        assertEquals("education:department", educationSummary.getDepartmentName());
        assertEquals(Long.valueOf(123456), educationSummary.getPutCode());
        assertEquals("education:title", educationSummary.getRoleTitle());
        assertEquals("private", educationSummary.getVisibility().value());
        assertNotNull(educationSummary.getStartDate());
        assertEquals("2000", educationSummary.getStartDate().getYear().getValue());
        assertEquals("01", educationSummary.getStartDate().getMonth().getValue());
        assertEquals("01", educationSummary.getStartDate().getDay().getValue());
        assertEquals("2020", educationSummary.getEndDate().getYear().getValue());
        assertEquals("02", educationSummary.getEndDate().getMonth().getValue());
        assertEquals("02", educationSummary.getEndDate().getDay().getValue());        
        assertNotNull(educationSummary.getSource());
        assertNotNull(educationSummary.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, educationSummary.getSource().retrieveSourcePath());
        assertEquals("http://tempuri.org",educationSummary.getUrl().getValue());
    }

    private Education getEducation(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Education.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/education-3.0_rc1.xml";
        if(full) {
            name = "/record_3.0_rc1/samples/read_samples/education-full-3.0_rc1.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Education) unmarshaller.unmarshal(inputStream);
    }
    
    private OrgAffiliationRelationEntity getEducationEntity() {
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
        result.setAffiliationType(AffiliationType.EDUCATION.name());
        result.setDepartment("education:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("education:title");
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());   
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setUrl("http://tempuri.org");
        return result;
    }
}
