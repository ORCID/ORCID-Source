package org.orcid.core.adapter.mapstruct;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_v2.WorkContributors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Replaces the Orika-based {@code WorkContributorsConverter}.
 * Managed by Spring so MapStruct can auto-inject it into JpaJaxbWorkAdapterImpl.
 */
@Component
public class WorkContributorsMapperV2 {

    private final WorkContributorRoleConverter roleConverter;

    public WorkContributorsMapperV2(WorkContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public String convertTo(WorkContributors source) {
        if (source == null) {
            return null;
        }
        return JsonUtils.convertToJsonString(source);
    }

    public WorkContributors convertFrom(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        JsonNode tree = JsonUtils.readTree(source);
        if (tree == null) {
            return null;
        }

        JsonNode contributorNode = tree.get("contributor");

        if (contributorNode != null && contributorNode.isArray()) {
            Iterator<JsonNode> contributors = contributorNode.elements();

            while (contributors.hasNext()) {
                JsonNode contributor = contributors.next();
                JsonNode attributes = contributor.get("contributorAttributes");

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

        WorkContributors workContributors = JsonUtils.convertTreeToValue(tree, WorkContributors.class);

        if (workContributors != null && workContributors.getContributor() != null) {
            workContributors.getContributor().forEach(c -> 
                c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName())
            );
        }

        return workContributors;
    }
}