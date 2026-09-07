package org.orcid.core.adapter.mapstruct;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.orcid.core.contributors.roles.fundings.FundingContributorRoleConverter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_v2.FundingContributors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Replaces the old Orika-only {@code org.orcid.core.adapter.converter.FundingContributorsConverter}.
 * Managed by Spring so MapStruct can auto-inject it into the JpaJaxbFundingAdapterImpl.
 */
@Component
public class FundingContributorsMapperV2 {

    private final FundingContributorRoleConverter roleConverter;

    // Spring will automatically inject the FundingContributorRoleConverter here
    public FundingContributorsMapperV2(FundingContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public String convertTo(FundingContributors source) {
        if (source == null) {
            return null;
        }
        return JsonUtils.convertToJsonString(source);
    }

    public FundingContributors convertFrom(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        // examine json tree before converting to funding contributors
        JsonNode tree = JsonUtils.readTree(source);
        if (tree == null) {
            return null;
        }

        JsonNode contributorNode = tree.get("contributor");
        
        // Null-check the array node before trying to iterate
        if (contributorNode != null && contributorNode.isArray()) {
            Iterator<JsonNode> contributors = contributorNode.elements();
            
            while (contributors.hasNext()) {
                JsonNode contributor = contributors.next();
                JsonNode attributes = contributor.get("contributorAttributes");
                
                // Null-check attributes before modifying
                if (attributes != null && attributes.isObject()) {
                    JsonNode contributorRole = attributes.get("contributorRole");

                    if (contributorRole != null && !contributorRole.isNull()) {
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
            }
        }

        FundingContributors fundingContributors = JsonUtils.convertTreeToValue(tree, FundingContributors.class);
        
        // Null-check the converted object and its internal list before iterating
        if (fundingContributors != null && fundingContributors.getContributor() != null) {
            fundingContributors.getContributor().forEach(c -> 
                c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName())
            );
        }
        
        return fundingContributors;
    }
}