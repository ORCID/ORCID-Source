package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingType;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;

public class JSONFundingExternalIdentifiersConverterV2Test {

    private JSONFundingExternalIdentifiersConverterV2 converter = new JSONFundingExternalIdentifiersConverterV2();

    @Test
    public void testConvertTo() throws JAXBException {
        Funding funding = getFunding();
        assertEquals(
                "{\"fundingExternalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"funding:external-identifier-value\",\"url\":{\"value\":\"http://tempuri.org\"},\"relationship\":\"SELF\"},{\"type\":\"GRANT_NUMBER\",\"value\":\"funding:external-identifier-value2\",\"url\":{\"value\":\"http://tempuri.org/2\"},\"relationship\":\"SELF\"}]}",
                converter.convertTo(funding.getExternalIdentifiers(), null));
    }

    @Test
    public void testConvertFrom() {
        ProfileFundingEntity funding = getProfileFundingEntity();
        ExternalIDs externalIDs = converter.convertFrom(funding.getExternalIdentifiersJson(), null);
        assertNotNull(externalIDs);
        assertEquals(2, externalIDs.getExternalIdentifier().size());
        
        ExternalID externalID = externalIDs.getExternalIdentifier().get(0);
        assertEquals("grant_number", externalID.getType());
        assertEquals("12345", externalID.getValue());
        assertEquals("http://tempuri.org", externalID.getUrl().getValue());
        
        externalID = externalIDs.getExternalIdentifier().get(1);
        assertEquals("grant_number", externalID.getType());
        assertEquals("67890", externalID.getValue());
        assertEquals("http://tempuri.org/2", externalID.getUrl().getValue());
    }

    private ProfileFundingEntity getProfileFundingEntity() {
        ProfileFundingEntity result = new ProfileFundingEntity();
        result.setContributorsJson(
                "{\"contributor\":[{\"contributorOrcid\":{\"value\":null,\"valueAsString\":null,\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"funding:creditName\"},\"contributorEmail\":{\"value\":\"funding@contributorEmail.com\"},\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}}]}");
        result.setDescription("funding:description");
        result.setEndDate(new EndDateEntity(2020, 1, 1));
        result.setStartDate(new StartDateEntity(2000, 1, 1));
        result.setExternalIdentifiersJson(
                "{\"fundingExternalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"12345\",\"url\":{\"value\":\"http://tempuri.org\"}},{\"type\":\"GRANT_NUMBER\",\"value\":\"67890\",\"url\":{\"value\":\"http://tempuri.org/2\"}}]}");
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

    private Funding getFunding() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Funding.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/funding-full-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Funding) unmarshaller.unmarshal(inputStream);
    }

}
