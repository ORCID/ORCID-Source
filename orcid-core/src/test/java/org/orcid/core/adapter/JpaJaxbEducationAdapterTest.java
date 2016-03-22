/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record_rc2.Education;
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
public class JpaJaxbEducationAdapterTest {

    @Resource
    private JpaJaxbEducationAdapter jpaJaxbEducationAdapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Education e = getEducation(true);
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertEquals(Visibility.PRIVATE.value(), oar.getVisibility().value());        
        assertEquals("education:department-name", oar.getDepartment());
        assertEquals("education:role-title", oar.getTitle());
        
        //Dates
        assertEquals(Integer.valueOf(2), oar.getStartDate().getDay());        
        assertEquals(Integer.valueOf(2), oar.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1848), oar.getStartDate().getYear());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getDay());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getMonth());
        assertEquals(Integer.valueOf(1848), oar.getEndDate().getYear());
        
        //Source
        assertEquals("8888-8888-8888-8880", oar.getSource().getSourceId());
        
        //Check org values
        assertEquals("common:name", oar.getOrg().getName());
        assertEquals("common:city", oar.getOrg().getCity());
        assertEquals("common:region", oar.getOrg().getRegion());        
        assertEquals(Iso3166Country.AF.value(), oar.getOrg().getCountry().value());
        assertEquals("common:disambiguated-organization-identifier", oar.getOrg().getOrgDisambiguated().getSourceId());
        assertEquals("common:disambiguation-source", oar.getOrg().getOrgDisambiguated().getSourceType());        
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
        assertEquals(org.orcid.jaxb.model.common_rc2.Iso3166Country.US, education.getOrganization().getAddress().getCountry());
        assertNotNull(education.getSource());        
        assertNotNull(education.getSource().retrieveSourcePath());
        assertEquals("APP-000000001", education.getSource().retrieveSourcePath());
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
        assertEquals("APP-000000001", educationSummary.getSource().retrieveSourcePath());
    }

    private Education getEducation(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Education.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc2/samples/education-2.0_rc2.xml";
        if(full) {
            name = "/record_2.0_rc2/samples/education-full-2.0_rc2.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Education) unmarshaller.unmarshal(inputStream);
    }
    
    private OrgAffiliationRelationEntity getEducationEntity() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US);
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        orgEntity.setSource(new SourceEntity("APP-000000001"));
        
        OrgAffiliationRelationEntity result = new OrgAffiliationRelationEntity();
        result.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.EDUCATION);
        result.setDepartment("education:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("education:title");
        result.setVisibility(org.orcid.jaxb.model.message.Visibility.PRIVATE);   
        result.setSource(new SourceEntity("APP-000000001"));
        
        return result;
    }
}
