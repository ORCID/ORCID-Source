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
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingType;
import org.orcid.jaxb.model.record.Iso3166Country;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbFundingAdapterTest {

    @Resource
    private JpaJaxbFundingAdapter jpaJaxbFundingAdapter;

    @Test
    public void testUnmarshallWork() throws JAXBException {
        Funding funding = getFunding();
        assertEquals(FundingType.GRANT, funding.getType());
        assertEquals("funding:organizationDefinedType", funding.getOrganizationDefinedType().getContent());
        assertEquals("funding:title", funding.getTitle().getTitle().getContent());
        assertEquals("funding:translatedTitle", funding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", funding.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("common:name", funding.getOrganization().getName());
        assertEquals("common:city", funding.getOrganization().getAddress().getCity());
        assertEquals("common:region", funding.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, funding.getOrganization().getAddress().getCountry());
        assertEquals("common:disambiguatedOrganizationIdentifier", funding.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("common:disambiguationSource", funding.getOrganization().getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("funding:shortDescription", funding.getDescription());
        assertEquals("funding:amount", funding.getAmount().getContent());
        assertEquals("ADP", funding.getAmount().getCurrencyCode());
        assertEquals("http://tempuri.org", funding.getUrl().getValue());
        assertEquals("25", funding.getStartDate().getDay().getValue());        
        assertEquals("01", funding.getStartDate().getMonth().getValue());
        assertEquals("1920", funding.getStartDate().getYear().getValue());
        assertEquals("31", funding.getEndDate().getDay().getValue());        
        assertEquals("12", funding.getEndDate().getMonth().getValue());
        assertEquals("2020", funding.getEndDate().getYear().getValue());        
        assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("grant_number", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getType().value());
        assertEquals("http://tempuri.org", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("12345", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, funding.getContributors().getContributor().size()); 
        assertEquals("lead", funding.getContributors().getContributor().get(0).getContributorAttributes().getContributorRole().value());
        assertEquals("funding@contributorEmail.com", funding.getContributors().getContributor().get(0).getContributorEmail().getValue());
        assertEquals("orcid.org", funding.getContributors().getContributor().get(0).getContributorOrcid().getHost());
        assertEquals("http://orcid.org/8888-8888-8888-8880", funding.getContributors().getContributor().get(0).getContributorOrcid().getUri());
        assertEquals("8888-8888-8888-8880", funding.getContributors().getContributor().get(0).getContributorOrcid().getPath());
    }
    
    @Test
    public void testToFundingEntity() throws JAXBException {
        
    }

    private Funding getFunding() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Funding.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream("/record_2.0_rc1/samples/funding-2.0_rc1.xml");
        return (Funding) unmarshaller.unmarshal(inputStream);
    }
}
