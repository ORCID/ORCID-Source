package org.orcid.core.adapter.converter;

import java.util.Iterator;

import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_v2.WorkContributors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WorkContributorsConverter extends BidirectionalConverter<WorkContributors, String> {

    private ContributorRoleConverter roleConverter;

    public WorkContributorsConverter(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    @Override
    public String convertTo(WorkContributors source, Type<String> destinationType) {
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public WorkContributors convertFrom(String source, Type<WorkContributors> destinationType) {
        // examine json tree before converting to work contributors
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

        WorkContributors workContributors = JsonUtils.convertTreeToValue(tree, WorkContributors.class);
        workContributors.getContributor().forEach(c -> c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName()));

        return workContributors;
    }
}
