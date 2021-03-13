package org.orcid.core.adapter.converter;

import java.util.Iterator;

import org.orcid.core.contributors.roles.fundings.FundingContributorRoleConverter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_v2.FundingContributors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class FundingContributorsConverter extends BidirectionalConverter<FundingContributors, String> {

    private FundingContributorRoleConverter roleConverter;
    
    public FundingContributorsConverter(FundingContributorRoleConverter fundingContributorsRoleConverter) {
        this.roleConverter = fundingContributorsRoleConverter;
    }

    @Override
    public String convertTo(FundingContributors source, Type<String> destinationType) {
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public FundingContributors convertFrom(String source, Type<FundingContributors> destinationType) {
        // examine json tree before converting to funding contributors
        JsonNode tree = JsonUtils.readTree(source);
        Iterator<JsonNode> contributors = tree.get("contributor").elements();
        while (contributors.hasNext()) {
            JsonNode contributor = contributors.next();
            JsonNode attributes = contributor.get("contributorAttributes");
            JsonNode contributorRole = attributes.get("contributorRole");

            if (contributorRole != null) {
                String contributorRoleValue = contributorRole.textValue();

                // ensure only V2 compatible roles
                String legacyRole = roleConverter.toLegacyRoleName(contributorRoleValue);

                if (legacyRole != null) {
                    ((ObjectNode) attributes).put("contributorRole", legacyRole);
                } else {
                    ((ObjectNode) attributes).remove("contributorRole");
                }
            }
        }
        
        FundingContributors fundingContributors = JsonUtils.convertTreeToValue(tree, FundingContributors.class);
        fundingContributors.getContributor().forEach(c -> c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName()));
        return fundingContributors;
    }
}
