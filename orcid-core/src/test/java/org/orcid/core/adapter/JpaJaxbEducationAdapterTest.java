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
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Iso3166Country;
import org.orcid.jaxb.model.record.Visibility;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbEducationAdapterTest {

    @Resource
    private JpaJaxbEducationAdapter jpaJaxbEducationAdapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        Education e = getEducation();
        assertNotNull(e);
        OrgAffiliationRelationEntity oar = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(e);
        assertNotNull(oar);
        //General info
        assertEquals(Long.valueOf(123), oar.getId());
        assertEquals(Visibility.PRIVATE.value(), oar.getVisibility().value());        
        assertEquals("education:departmentName", oar.getDepartment());
        assertEquals("education:roleTitle", oar.getTitle());
        
        //Dates
        assertEquals(Integer.valueOf(25), oar.getStartDate().getDay());        
        assertEquals(Integer.valueOf(1), oar.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1920), oar.getStartDate().getYear());
        assertEquals(Integer.valueOf(25), oar.getEndDate().getDay());
        assertEquals(Integer.valueOf(1), oar.getEndDate().getMonth());
        assertEquals(Integer.valueOf(1950), oar.getEndDate().getYear());
        
        //Source
        assertEquals("8888-8888-8888-8880", oar.getSource().getSourceId());
        
        //Check org values
        assertEquals("common:name", oar.getOrg().getName());
        assertEquals("common:city", oar.getOrg().getCity());
        assertEquals("common:region", oar.getOrg().getRegion());        
        assertEquals(Iso3166Country.AF.value(), oar.getOrg().getCountry().value());
        assertEquals("common:disambiguatedOrganizationIdentifier", oar.getOrg().getOrgDisambiguated().getSourceId());
        assertEquals("common:disambiguationSource", oar.getOrg().getOrgDisambiguated().getSourceType());
        
    }

    private Education getEducation() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Education.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream("/record_2.0_rc1/samples/education-2.0_rc1.xml");
        return (Education) unmarshaller.unmarshal(inputStream);
    }
}
