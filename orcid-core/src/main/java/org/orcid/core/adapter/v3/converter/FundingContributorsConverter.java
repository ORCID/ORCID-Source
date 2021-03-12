package org.orcid.core.adapter.v3.converter;

import org.orcid.core.contributors.roles.fundings.FundingContributorRoleConverter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.record.FundingContributors;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class FundingContributorsConverter extends BidirectionalConverter<FundingContributors, String> {

    private FundingContributorRoleConverter roleConverter;
    
    public FundingContributorsConverter(FundingContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    @Override
    public String convertTo(FundingContributors source, Type<String> destinationType) {
        // convert role to db format
        source.getContributor().forEach(c -> {
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                c.getContributorAttributes().setContributorRole(roleConverter.toDBRole(c.getContributorAttributes().getContributorRole()));
            }
        });
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public FundingContributors convertFrom(String source, Type<FundingContributors> destinationType) {
        FundingContributors fundingContributors = JsonUtils.readObjectFromJsonString(source, FundingContributors.class);
        fundingContributors.getContributor().forEach(c -> c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName()));
        
        // convert role to API format
        fundingContributors.getContributor().forEach(c -> {
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                c.getContributorAttributes().setContributorRole(roleConverter.toRoleValue(c.getContributorAttributes().getContributorRole()));
            }
        });
        return fundingContributors;
    }
}
