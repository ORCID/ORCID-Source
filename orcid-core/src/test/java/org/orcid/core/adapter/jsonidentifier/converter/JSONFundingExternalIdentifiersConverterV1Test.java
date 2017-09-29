package org.orcid.core.adapter.jsonidentifier.converter;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.jsonidentifier.JSONExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONFundingExternalIdentifiers;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.OrcidJUnit4ClassRunner;

@RunWith(OrcidJUnit4ClassRunner.class)
@Ignore
public class JSONFundingExternalIdentifiersConverterV1Test {

    @Test
    public void testConvertTo(FundingExternalIdentifiers messagePojo) {
//        JSONFundingExternalIdentifiers fundingExternalIdentifiers = new JSONFundingExternalIdentifiers();
//        for (FundingExternalIdentifier fundingExternalIdentifier : messagePojo.getFundingExternalIdentifier()) {
//            JSONExternalIdentifier jsonExternalIdentifier = new JSONExternalIdentifier();
//            if (fundingExternalIdentifier.getType() != null) {
//                jsonExternalIdentifier.setType(fundingExternalIdentifier.getType().value());
//            }
//            if (fundingExternalIdentifier.getUrl() != null) {
//                jsonExternalIdentifier.setUrl(new JSONUrl(fundingExternalIdentifier.getUrl().getValue()));
//            }
//            if (!PojoUtil.isEmpty(fundingExternalIdentifier.getValue())) {
//                jsonExternalIdentifier.setValue(fundingExternalIdentifier.getValue());
//            }
//            jsonExternalIdentifier.setRelationship(Relationship.SELF.value());
//            fundingExternalIdentifiers.getFundingExternalIdentifier().add(jsonExternalIdentifier);
//        }
//        return JsonUtils.convertToJsonString(fundingExternalIdentifiers);
    }

    @Test@Ignore
    public void testConvertFrom(String source) {
//        JSONFundingExternalIdentifiers jsonFundingExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONFundingExternalIdentifiers.class);
//        FundingExternalIdentifiers fundingExternalIdentifiers = new FundingExternalIdentifiers();
//        for (JSONExternalIdentifier jsonFundingExternalIdentifier : jsonFundingExternalIdentifiers.getFundingExternalIdentifier()) {
//            FundingExternalIdentifier fundingExternalIdentifier = new FundingExternalIdentifier();
//            try {
//                if (jsonFundingExternalIdentifier.getType() != null) {
//                    fundingExternalIdentifier.setType(FundingExternalIdentifierType.fromValue(jsonFundingExternalIdentifier.getType().toLowerCase()));
//                } else {
//                    fundingExternalIdentifier.setType(FundingExternalIdentifierType.GRANT_NUMBER);
//                }
//            } catch (IllegalArgumentException e) {
//                fundingExternalIdentifier.setType(FundingExternalIdentifierType.GRANT_NUMBER);
//            }
//            if (jsonFundingExternalIdentifier.getUrl() != null) {
//                org.orcid.jaxb.model.message.Url messageUrl = new org.orcid.jaxb.model.message.Url();
//                messageUrl.setValue(jsonFundingExternalIdentifier.getUrl().getValue());
//                fundingExternalIdentifier.setUrl(messageUrl);
//            }
//            fundingExternalIdentifier.setValue(jsonFundingExternalIdentifier.getValue());
//            fundingExternalIdentifiers.getFundingExternalIdentifier().add(fundingExternalIdentifier);
//        }
//        return fundingExternalIdentifiers;
    }
    
}
