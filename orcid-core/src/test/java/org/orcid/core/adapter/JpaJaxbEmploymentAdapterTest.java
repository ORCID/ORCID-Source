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
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbEmploymentAdapterTest {

    @Resource
    private JpaJaxbEmploymentAdapter jpaJaxbEmploymentAdapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Employment e = getEmployment();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertEquals(Visibility.PRIVATE.value(), oar.getVisibility().value());        
        assertEquals("affiliation:department-name", oar.getDepartment());
        assertEquals("affiliation:role-title", oar.getTitle());
        
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
    public void fromOrgAffiliationRelationEntityToEmployment() {
        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        Employment employment = jpaJaxbEmploymentAdapter.toEmployment(entity);
        assertNotNull(employment);
        assertEquals("employment:department", employment.getDepartmentName());
        assertEquals("123456", employment.getPutCode());
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
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, employment.getOrganization().getAddress().getCountry());
        assertNotNull(employment.getSource());        
        assertNotNull(employment.getSource().getSourceOrcid());
        assertEquals("APP-000000001", employment.getSource().getSourceOrcid().getPath());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToEmploymentSummary() {
        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        EmploymentSummary employmentSummary = jpaJaxbEmploymentAdapter.toEmploymentSummary(entity);
        assertNotNull(employmentSummary);
        assertEquals("employment:department", employmentSummary.getDepartmentName());
        assertEquals("123456", employmentSummary.getPutCode());
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
        assertNotNull(employmentSummary.getSource().getSourceOrcid());
        assertEquals("APP-000000001", employmentSummary.getSource().getSourceOrcid().getPath());
    }
    
    private Employment getEmployment() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Employment.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream("/record_2.0_rc1/samples/employment-2.0_rc1.xml");
        return (Employment) unmarshaller.unmarshal(inputStream);
    }
    
    private OrgAffiliationRelationEntity getEmploymentEntity() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US);
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        orgEntity.setSource(new SourceEntity("APP-000000001"));
        
        OrgAffiliationRelationEntity result = new OrgAffiliationRelationEntity();
        result.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.EMPLOYMENT);
        result.setDepartment("employment:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("employment:title");
        result.setVisibility(org.orcid.jaxb.model.message.Visibility.PRIVATE);   
        result.setSource(new SourceEntity("APP-000000001"));
        
        return result;
    }
}
