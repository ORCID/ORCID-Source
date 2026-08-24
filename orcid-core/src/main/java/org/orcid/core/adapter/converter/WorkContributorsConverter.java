package org.orcid.core.adapter.converter;

import java.util.Iterator;

import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_v2.WorkContributors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Plain, Orika-free converter for v2 WorkContributors. Converts WorkContributors <-> JSON string for DB storage.
 */
public class WorkContributorsConverter {

    private ContributorRoleConverter roleConverter;

    public WorkContributorsConverter(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public String convertTo(WorkContributors source) {
        return JsonUtils.convertToJsonString(source);
    }

    public WorkContributors convertFrom(String source) {
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
