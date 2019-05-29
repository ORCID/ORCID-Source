package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.v3.JpaJaxbFundingAdapter;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
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
public class JpaJaxbFundingAdapterTest {

    @Resource(name = "jpaJaxbFundingAdapterV3")
    private JpaJaxbFundingAdapter jpaJaxbFundingAdapter;

    @Test
    public void toFundingEntityTest() throws JAXBException {
        Funding f = getFunding(true);
        assertNotNull(f);
        ProfileFundingEntity pfe = jpaJaxbFundingAdapter.toProfileFundingEntity(f);
        assertNotNull(pfe);
        // Enums
        assertEquals(Visibility.PRIVATE.name(), pfe.getVisibility());
        assertEquals(FundingType.GRANT.name(), pfe.getType());

        // General info
        assertEquals(Long.valueOf(0), pfe.getId());
        assertEquals("common:title", pfe.getTitle());
        assertEquals("common:translated-title", pfe.getTranslatedTitle());
        assertEquals("en", pfe.getTranslatedTitleLanguageCode());
        assertEquals("common:organization-defined-type", pfe.getOrganizationDefinedType());
        assertEquals("funding:short-description", pfe.getDescription());
        assertEquals("1234", pfe.getNumericAmount().toString());
        assertEquals("ADP", pfe.getCurrencyCode());
        assertEquals("http://tempuri.org", pfe.getUrl());

        // Dates
        assertEquals(Integer.valueOf(2), pfe.getStartDate().getDay());
        assertEquals(Integer.valueOf(2), pfe.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1948), pfe.getStartDate().getYear());
        assertEquals(Integer.valueOf(2), pfe.getEndDate().getDay());
        assertEquals(Integer.valueOf(2), pfe.getEndDate().getMonth());
        assertEquals(Integer.valueOf(1948), pfe.getEndDate().getYear());

        // Contributors
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"https://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"funding:credit-name\"},\"contributorEmail\":{\"value\":\"funding@contributor.email\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}",
                pfe.getContributorsJson());

        // External identifiers
        assertEquals(
                "{\"fundingExternalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"funding:external-identifier-value\",\"url\":{\"value\":\"http://tempuri.org\"},\"relationship\":\"SELF\"},{\"type\":\"GRANT_NUMBER\",\"value\":\"funding:external-identifier-value2\",\"url\":{\"value\":\"http://tempuri.org/2\"},\"relationship\":\"SELF\"}]}",
                pfe.getExternalIdentifiersJson());

        // Check org is null
        assertNull(pfe.getOrg()); 
        
        // Source
        assertNull(pfe.getSourceId());        
        assertNull(pfe.getClientSourceId());        
        assertNull(pfe.getElementSourceId());
    }

    @Test
    public void fromFundingEntityTest() throws JAXBException {
        ProfileFundingEntity entity = getProfileFundingEntity();
        assertNotNull(entity);
        assertEquals("123456", entity.getNumericAmount().toString());

        Funding funding = jpaJaxbFundingAdapter.toFunding(entity);
        assertNotNull(funding);
        assertEquals(Long.valueOf(12345), funding.getPutCode());
        assertNotNull(funding.getAmount());
        assertEquals("123456", funding.getAmount().getContent());
        assertEquals("CRC", funding.getAmount().getCurrencyCode());
        assertNotNull(funding.getContributors());
        assertNotNull(funding.getContributors().getContributor());
        assertEquals(1, funding.getContributors().getContributor().size());
        assertEquals("8888-8888-8888-8880", funding.getContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertEquals("orcid.org", funding.getContributors().getContributor().get(0).getContributorOrcid().getHost());
        assertEquals("http://orcid.org/8888-8888-8888-8880", funding.getContributors().getContributor().get(0).getContributorOrcid().getUri());
        assertEquals("funding:creditName", funding.getContributors().getContributor().get(0).getCreditName().getContent());
        assertEquals("funding:description", funding.getDescription());
        assertNotNull(funding.getStartDate());
        assertEquals("01", funding.getStartDate().getDay().getValue());
        assertEquals("01", funding.getStartDate().getMonth().getValue());
        assertEquals("2000", funding.getStartDate().getYear().getValue());
        assertNotNull(funding.getEndDate());
        assertEquals("01", funding.getEndDate().getDay().getValue());
        assertEquals("01", funding.getEndDate().getMonth().getValue());
        assertEquals("2020", funding.getEndDate().getYear().getValue());
        assertEquals("funding:title", funding.getTitle().getTitle().getContent());
        assertEquals("funding:translatedTitle", funding.getTitle().getTranslatedTitle().getContent());
        assertEquals("es", funding.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(FundingType.SALARY_AWARD, funding.getType());
        assertEquals(Visibility.PRIVATE, funding.getVisibility());
    }

    @Test
    public void fromFundingEntityToSummaryTest() throws JAXBException {
        ProfileFundingEntity entity = getProfileFundingEntity();
        assertNotNull(entity);
        assertEquals("123456", entity.getNumericAmount().toString());
        FundingSummary summary = jpaJaxbFundingAdapter.toFundingSummary(entity);
        assertNotNull(summary);
        assertEquals(Long.valueOf(12345), summary.getPutCode());
        assertNotNull(summary.getStartDate());
        assertEquals("01", summary.getStartDate().getDay().getValue());
        assertEquals("01", summary.getStartDate().getMonth().getValue());
        assertEquals("2000", summary.getStartDate().getYear().getValue());
        assertNotNull(summary.getEndDate());
        assertEquals("01", summary.getEndDate().getDay().getValue());
        assertEquals("01", summary.getEndDate().getMonth().getValue());
        assertEquals("2020", summary.getEndDate().getYear().getValue());
        assertEquals("funding:title", summary.getTitle().getTitle().getContent());
        assertEquals(FundingType.SALARY_AWARD, summary.getType());
        assertEquals(Visibility.PRIVATE, summary.getVisibility());
    }
    
    @Test
    public void clearFundingEntityFieldsTest() throws JAXBException {
        Funding f = getFunding(true);
        assertNotNull(f);
        
        ProfileFundingEntity pfe = jpaJaxbFundingAdapter.toProfileFundingEntity(f);
        assertNotNull(pfe);
        assertEquals("common:translated-title", pfe.getTranslatedTitle());
        assertEquals("en", pfe.getTranslatedTitleLanguageCode());
        assertEquals("common:organization-defined-type", pfe.getOrganizationDefinedType());
        assertEquals("1234", pfe.getNumericAmount().toString());
        assertEquals("ADP", pfe.getCurrencyCode());
        assertEquals("http://tempuri.org", pfe.getUrl());

        f.getTitle().setTranslatedTitle(null);
        f.setOrganizationDefinedType(null);
        f.setAmount(null);
        f.setUrl(null);
        
        jpaJaxbFundingAdapter.toProfileFundingEntity(f, pfe);
        
        // Verify values where removed
        assertNotNull(pfe);
        assertNull(pfe.getCurrencyCode());
        assertNull(pfe.getNumericAmount());
        assertNull(pfe.getOrganizationDefinedType());
        assertNull(pfe.getTranslatedTitle());
        assertNull(pfe.getTranslatedTitleLanguageCode());
        assertNull(pfe.getUrl());
        // Enums
        assertEquals(Visibility.PRIVATE.name(), pfe.getVisibility());
        assertEquals(FundingType.GRANT.name(), pfe.getType());

        // General info
        assertEquals(Long.valueOf(0), pfe.getId());
        assertEquals("common:title", pfe.getTitle());
        assertEquals("funding:short-description", pfe.getDescription());
        
        // Dates
        assertEquals(Integer.valueOf(2), pfe.getStartDate().getDay());
        assertEquals(Integer.valueOf(2), pfe.getStartDate().getMonth());
        assertEquals(Integer.valueOf(1948), pfe.getStartDate().getYear());
        assertEquals(Integer.valueOf(2), pfe.getEndDate().getDay());
        assertEquals(Integer.valueOf(2), pfe.getEndDate().getMonth());
        assertEquals(Integer.valueOf(1948), pfe.getEndDate().getYear());

        // Contributors
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"https://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"funding:credit-name\"},\"contributorEmail\":{\"value\":\"funding@contributor.email\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}",
                pfe.getContributorsJson());

        // External identifiers
        assertEquals(
                "{\"fundingExternalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"funding:external-identifier-value\",\"url\":{\"value\":\"http://tempuri.org\"},\"relationship\":\"SELF\"},{\"type\":\"GRANT_NUMBER\",\"value\":\"funding:external-identifier-value2\",\"url\":{\"value\":\"http://tempuri.org/2\"},\"relationship\":\"SELF\"}]}",
                pfe.getExternalIdentifiersJson());

        // Check org is null
        assertNull(pfe.getOrg()); 
        
        // Source
        assertNull(pfe.getSourceId());        
        assertNull(pfe.getClientSourceId());        
        assertNull(pfe.getElementSourceId());
        
    }
    
    private Funding getFunding(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Funding.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/funding-3.0_rc1.xml";
        if(full) {
            name = "/record_3.0_rc1/samples/read_samples/funding-full-3.0_rc1.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Funding) unmarshaller.unmarshal(inputStream);
    }

    private ProfileFundingEntity getProfileFundingEntity() {
        ProfileFundingEntity result = new ProfileFundingEntity();
        result.setContributorsJson("{\"contributor\":[{\"contributorOrcid\":{\"value\":null,\"valueAsString\":null,\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"funding:creditName\"},\"contributorEmail\":{\"value\":\"funding@contributorEmail.com\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}");
        result.setDescription("funding:description");
        result.setEndDate(new EndDateEntity(2020, 1, 1));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setExternalIdentifiersJson("{\"fundingExternalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"12345\",\"url\":{\"value\":\"http://tempuri.org\"}},{\"type\":\"GRANT_NUMBER\",\"value\":\"67890\",\"url\":{\"value\":\"http://tempuri.org/2\"}}]}");
        result.setId(12345L);
        result.setNumericAmount(new BigDecimal(123456));
        result.setCurrencyCode("CRC");
        result.setTitle("funding:title");
        result.setTranslatedTitle("funding:translatedTitle");
        result.setTranslatedTitleLanguageCode("es");
        result.setType(org.orcid.jaxb.model.record_v2.FundingType.SALARY_AWARD.name());
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        return result;
    }

}
