package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbFundingAdapter;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingType;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
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
public class JpaJaxbFundingAdapterTest {

    @Resource
    private JpaJaxbFundingAdapter jpaJaxbFundingAdapter;

    @Test
    public void toFundingEntityTest() throws JAXBException {
        Funding f = getFunding(true);
        assertNotNull(f);
        assertNotNull(f.getCreatedDate().getValue());
        assertNotNull(f.getLastModifiedDate().getValue());
        ProfileFundingEntity pfe = jpaJaxbFundingAdapter.toProfileFundingEntity(f);
        assertNotNull(pfe);
        // Enums
        assertEquals(Visibility.PRIVATE.name(), pfe.getVisibility());
        assertEquals(FundingType.GRANT.name(), pfe.getType());

        // General info
        assertEquals(Long.valueOf(0), pfe.getId());
        assertNull(pfe.getDateCreated());
        assertNull(pfe.getLastModified());
        assertEquals("common:title", pfe.getTitle());
        assertEquals("common:translated-title", pfe.getTranslatedTitle());
        assertEquals("en", pfe.getTranslatedTitleLanguageCode());
        assertEquals("funding:organization-defined-type", pfe.getOrganizationDefinedType());
        assertEquals("funding:short-description", pfe.getDescription());
        assertEquals("1234.0", pfe.getNumericAmount().toString());
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
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"funding:credit-name\"},\"contributorEmail\":{\"value\":\"funding@contributor.email\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}",
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
    public void fromFundingEntityTest() throws JAXBException, IllegalAccessException {
        ProfileFundingEntity entity = getProfileFundingEntity();
        assertNotNull(entity);
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());
        assertEquals("123456", entity.getNumericAmount().toString());

        Funding funding = jpaJaxbFundingAdapter.toFunding(entity);
        assertNotNull(funding);
        assertNotNull(funding.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(funding.getCreatedDate().getValue()));
        assertNotNull(funding.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(funding.getLastModifiedDate().getValue()));
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
        assertEquals("ES", funding.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(FundingType.SALARY_AWARD, funding.getType());
        assertEquals(Visibility.PRIVATE, funding.getVisibility());
    }

    @Test
    public void fromFundingEntityToSummaryTest() throws JAXBException, IllegalAccessException {
        ProfileFundingEntity entity = getProfileFundingEntity();
        assertNotNull(entity);
        assertEquals("123456", entity.getNumericAmount().toString());
        FundingSummary summary = jpaJaxbFundingAdapter.toFundingSummary(entity);
        assertNotNull(summary);
        assertNotNull(summary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getCreatedDate().getValue()));
        assertNotNull(summary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(summary.getLastModifiedDate().getValue()));
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
        assertEquals("funding:organization-defined-type", pfe.getOrganizationDefinedType());
        assertEquals("1234.0", pfe.getNumericAmount().toString());
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
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"funding:credit-name\"},\"contributorEmail\":{\"value\":\"funding@contributor.email\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}",
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
    public void clearMonthFieldsForFundingDateTest() throws JAXBException {
        Funding f = getFunding(true);
        assertNotNull(f);
        ProfileFundingEntity pfe = jpaJaxbFundingAdapter.toProfileFundingEntity(f);
        
        FuzzyDate startDate = FuzzyDate.valueOf(2021, null, null);
        FuzzyDate endDate = FuzzyDate.valueOf(2022, null, null);
        f.setStartDate(startDate);
        f.setEndDate(endDate);
        jpaJaxbFundingAdapter.toProfileFundingEntity(f, pfe);
        assertNull(pfe.getStartDate().getMonth());
        assertEquals(Integer.valueOf(2021), pfe.getStartDate().getYear());
        assertNull(pfe.getEndDate().getMonth());
        assertEquals(Integer.valueOf(2022),pfe.getEndDate().getYear());
    }
    
    
    
    private Funding getFunding(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Funding.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/funding-2.0.xml";
        if(full) {
            name = "/record_2.0/samples/read_samples/funding-full-2.0.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Funding) unmarshaller.unmarshal(inputStream);
    }

    private ProfileFundingEntity getProfileFundingEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ProfileFundingEntity result = new ProfileFundingEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(result, date);
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
        result.setTranslatedTitleLanguageCode("ES");
        result.setType(FundingType.SALARY_AWARD.name());
        result.setVisibility(Visibility.PRIVATE.name());
        return result;
    }

}
