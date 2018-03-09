package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.Url;

public class JSONFundingExternalIdentifiersConverterV1Test {

    private JSONFundingExternalIdentifiersConverterV1 converter = new JSONFundingExternalIdentifiersConverterV1();

    @Test
    public void testConvertTo() throws JAXBException {
        FundingExternalIdentifiers fundingExternalIdentifiers = getFundingIdentifiers();
        assertEquals(
                "{\"fundingExternalIdentifier\":[{\"type\":\"grant_number\",\"value\":\"erm....\",\"url\":{\"value\":\"http://orcid.org\"},\"relationship\":\"self\"}]}",
                converter.convertTo(fundingExternalIdentifiers));

    }

    @Test
    public void testConvertFrom() {
        FundingExternalIdentifiers externalIdentifiers = converter.convertFrom(
                "{\"fundingExternalIdentifier\":[{\"type\":\"grant_number\",\"value\":\"erm....\",\"url\":{\"value\":\"http://orcid.org\"},\"relationship\":\"self\"}]}");
        assertNotNull(externalIdentifiers);
        assertEquals(1, externalIdentifiers.getFundingExternalIdentifier().size());
        
        FundingExternalIdentifier fundingExternalIdentifier = externalIdentifiers.getFundingExternalIdentifier().get(0);
        assertEquals(FundingExternalIdentifierType.GRANT_NUMBER, fundingExternalIdentifier.getType());
        assertEquals("http://orcid.org", fundingExternalIdentifier.getUrl().getValue());
        assertEquals("erm....", fundingExternalIdentifier.getValue());
    }

    private FundingExternalIdentifiers getFundingIdentifiers() {
        FundingExternalIdentifiers fundingExternalIdentifiers = new FundingExternalIdentifiers();
        FundingExternalIdentifier fundingExternalIdentifier = new FundingExternalIdentifier();
        fundingExternalIdentifier.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fundingExternalIdentifier.setUrl(new Url("http://orcid.org"));
        fundingExternalIdentifier.setValue("erm....");
        fundingExternalIdentifiers.getFundingExternalIdentifier().add(fundingExternalIdentifier);
        return fundingExternalIdentifiers;
    }

}
