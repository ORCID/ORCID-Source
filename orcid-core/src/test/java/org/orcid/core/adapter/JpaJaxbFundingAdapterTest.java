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
import org.orcid.jaxb.model.record.Visibility;
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
    public void testToFundingEntity() throws JAXBException {
        Funding f = getFunding();
        assertNotNull(f);
        ProfileFundingEntity pfe = jpaJaxbFundingAdapter.toProfileFundingEntity(f);
        assertNotNull(pfe);
        //Enums
        assertEquals(Visibility.PRIVATE.value(), pfe.getVisibility().value());
        assertEquals(FundingType.GRANT.value(), pfe.getType().value());
        assertEquals(Visibility.PRIVATE.value(), pfe.getVisibility().value());

        //General info
        assertEquals("funding:title", pfe.getTitle());
        assertEquals("funding:translatedTitle", pfe.getTranslatedTitle());
        assertEquals("en", pfe.getTranslatedTitleLanguageCode());
        assertEquals("funding:organizationDefinedType", pfe.getOrganizationDefinedType());
        assertEquals("funding:shortDescription", pfe.getDescription());
        assertEquals("funding:amount", pfe.getAmount());
        assertEquals("ADP", pfe.getCurrencyCode());
        assertEquals("http://tempuri.org", pfe.getUrl());
        
        //Dates
        assertEquals(Integer.valueOf(25), pfe.getStartDate().getDay());        
        assertEquals(Integer.valueOf(1), pfe.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1920), pfe.getStartDate().getYear());
        assertEquals(Integer.valueOf(31), pfe.getEndDate().getDay());
        assertEquals(Integer.valueOf(12), pfe.getEndDate().getMonth());
        assertEquals(Integer.valueOf(2020), pfe.getEndDate().getYear());
        
        //Contributors        
        assertEquals("{\"contributor\":[{\"contributorOrcid\":{\"value\":null,\"valueAsString\":null,\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"funding:creditName\",\"visibility\":\"PRIVATE\"},\"contributorEmail\":{\"value\":\"funding@contributorEmail.com\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}", pfe.getContributorsJson());

        //External identifiers
        assertEquals("{\"externalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"12345\",\"url\":{\"value\":\"http://tempuri.org\"}}]}", pfe.getExternalIdentifiersJson());
        
        //Source
        assertEquals("8888-8888-8888-8880", pfe.getSource().getSourceId());
        
        //Check org values
        assertEquals("common:name", pfe.getOrg().getName());
        assertEquals("common:city", pfe.getOrg().getCity());
        assertEquals("common:region", pfe.getOrg().getRegion());        
        assertEquals(Iso3166Country.AF.value(), pfe.getOrg().getCountry().value());
        assertEquals("common:disambiguatedOrganizationIdentifier", pfe.getOrg().getOrgDisambiguated().getSourceId());
        assertEquals("common:disambiguationSource", pfe.getOrg().getOrgDisambiguated().getSourceType());
        
    }

    private Funding getFunding() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Funding.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream("/record_2.0_rc1/samples/funding-2.0_rc1.xml");
        return (Funding) unmarshaller.unmarshal(inputStream);
    }
}
