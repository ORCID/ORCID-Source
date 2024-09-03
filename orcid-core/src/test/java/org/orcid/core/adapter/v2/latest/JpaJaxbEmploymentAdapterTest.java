package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbEmploymentAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbEmploymentAdapterTest extends MockSourceNameCache {

    @Resource
    private JpaJaxbEmploymentAdapter jpaJaxbEmploymentAdapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Employment e = getEmployment(true);
        assertNotNull(e);
        assertNotNull(e.getCreatedDate().getValue());
        assertNotNull(e.getLastModifiedDate().getValue());
        OrgAffiliationRelationEntity oar = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        //General info
        assertEquals(Long.valueOf(0), oar.getId());
        assertNull(oar.getDateCreated());
        assertNull(oar.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), oar.getVisibility());        
        assertEquals("employment:department-name", oar.getDepartment());
        assertEquals("employment:role-title", oar.getTitle());
        
        //Dates
        assertEquals(Integer.valueOf(2), oar.getStartDate().getDay());        
        assertEquals(Integer.valueOf(2), oar.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1948), oar.getStartDate().getYear());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getDay());
        assertEquals(Integer.valueOf(2), oar.getEndDate().getMonth());
        assertEquals(Integer.valueOf(1948), oar.getEndDate().getYear());
        
        //Source                
        assertNull(oar.getSourceId());        
        assertNull(oar.getClientSourceId());        
        assertNull(oar.getElementSourceId());
    }
    
    @Test
    public void testToOrgAffiliationRelationEntityWithNullMonthAndDay() throws JAXBException {
        Employment e = getEmploymentWithDates();
        
        OrgAffiliationRelationEntity oar = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar.getStartDate().getYear());
        assertNotNull(oar.getStartDate().getMonth());
        assertNotNull(oar.getStartDate().getDay());
        
        e = getEmploymentWithDatesWithNullMonthAndDay();
        oar = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(e, oar);
        assertNotNull(oar.getStartDate().getYear());
        assertNull(oar.getStartDate().getMonth());
        assertNull(oar.getStartDate().getDay());
    }

    @Test
    public void fromOrgAffiliationRelationEntityToEmployment() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        Employment employment = jpaJaxbEmploymentAdapter.toEmployment(entity);
        assertNotNull(employment);
        assertNotNull(employment.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employment.getCreatedDate().getValue()));
        assertNotNull(employment.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employment.getLastModifiedDate().getValue()));
        assertEquals("employment:department", employment.getDepartmentName());
        assertEquals(Long.valueOf(123456), employment.getPutCode());
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
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US, employment.getOrganization().getAddress().getCountry());
        assertNotNull(employment.getSource());        
        assertNotNull(employment.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, employment.getSource().retrieveSourcePath());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToEmploymentSummary() throws IllegalAccessException {
        OrgAffiliationRelationEntity entity = getEmploymentEntity();
        assertNotNull(entity);
        EmploymentSummary employmentSummary = jpaJaxbEmploymentAdapter.toEmploymentSummary(entity);
        assertNotNull(employmentSummary);
        assertNotNull(employmentSummary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employmentSummary.getCreatedDate().getValue()));
        assertNotNull(employmentSummary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(employmentSummary.getLastModifiedDate().getValue()));
        assertEquals("employment:department", employmentSummary.getDepartmentName());
        assertEquals(Long.valueOf(123456), employmentSummary.getPutCode());
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
        assertNotNull(employmentSummary.getSource().retrieveSourcePath());
        assertEquals(CLIENT_SOURCE_ID, employmentSummary.getSource().retrieveSourcePath());
    }
    
    private Employment getEmploymentWithDatesWithNullMonthAndDay() {
        Employment employment = new Employment();
        employment.setRoleTitle("role title");
        
        FuzzyDate startDate = new FuzzyDate(new Year(2017), null, null);
        FuzzyDate endDate = new FuzzyDate(new Year(2017), null, null);
        
        employment.setStartDate(startDate);
        employment.setEndDate(endDate);
        
        return employment;
    }
    
    private Employment getEmploymentWithDates() {
        Employment employment = new Employment();
        employment.setRoleTitle("role title");
        
        FuzzyDate startDate = new FuzzyDate(new Year(2017), new Month(1), new Day(1));
        FuzzyDate endDate = new FuzzyDate(new Year(2017), new Month(3), new Day(1));
        
        employment.setStartDate(startDate);
        employment.setEndDate(endDate);
        return employment;
    }
    
    private Employment getEmployment(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Employment.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/employment-2.0.xml";
        if(full) {
            name = "/record_2.0/samples/read_samples/employment-full-2.0.xml";
        }            
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Employment) unmarshaller.unmarshal(inputStream);
    }
    
    private OrgAffiliationRelationEntity getEmploymentEntity() throws IllegalAccessException {
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
        result.setAffiliationType(AffiliationType.EMPLOYMENT.name());
        result.setDepartment("employment:department");
        result.setEndDate(new EndDateEntity(2020, 2, 2));
        result.setId(123456L);
        result.setOrg(orgEntity);
        result.setOrcid("0000-0001-0002-0003");
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setTitle("employment:title");
        result.setVisibility(Visibility.PRIVATE.name());   
        result.setClientSourceId(CLIENT_SOURCE_ID);
        
        return result;
    }
}
