package org.orcid.core.adapter.converter;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_v2.FundingContributors;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class FundingContributorsConverter extends BidirectionalConverter<FundingContributors, String> {

    @Override
    public String convertTo(FundingContributors source, Type<String> destinationType) {
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public FundingContributors convertFrom(String source, Type<FundingContributors> destinationType) {
        FundingContributors fundingContributors = JsonUtils.readObjectFromJsonString(source, destinationType.getRawType());
        fundingContributors.getContributor().forEach(c -> c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName()));
        return fundingContributors;
    }
}
