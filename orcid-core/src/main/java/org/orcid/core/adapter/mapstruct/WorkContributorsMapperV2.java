package org.orcid.core.adapter.mapstruct;

import java.net.URI;
import java.util.Iterator;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.ContributorOrcid;
import org.orcid.jaxb.model.record_v2.WorkContributors;
import org.orcid.utils.OrcidStringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Replaces the Orika-based {@code WorkContributorsConverter}.
 * Managed by Spring so MapStruct can auto-inject it into JpaJaxbWorkAdapterImpl.
 */
@Component
public class WorkContributorsMapperV2 {

    private final WorkContributorRoleConverter roleConverter;

    @Autowired(required = false)
    @Resource(name = "orcidUrlManager")
    private OrcidUrlManager orcidUrlManager;

    public WorkContributorsMapperV2(WorkContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public void setOrcidUrlManager(OrcidUrlManager orcidUrlManager) {
        this.orcidUrlManager = orcidUrlManager;
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
            workContributors.getContributor().forEach(this::cleanAndPopulateContributor);
        }

        return workContributors;
    }

    public void cleanAndPopulateContributor(Contributor c) {
        if (c == null) {
            return;
        }
        if (c.getCreditName() != null && (c.getCreditName().getContent() == null || StringUtils.isBlank(c.getCreditName().getContent()))) {
            c.setCreditName(null);
        }

        if (c.getContributorEmail() != null && (c.getContributorEmail().getValue() == null || StringUtils.isBlank(c.getContributorEmail().getValue()))) {
            c.setContributorEmail(null);
        }

        if (c.getContributorOrcid() != null) {
            ContributorOrcid orcid = c.getContributorOrcid();
            String path = orcid.getPath();
            String uriStr = orcid.getUri();
            String host = orcid.getHost();

            if (StringUtils.isBlank(path) && StringUtils.isNotBlank(uriStr)) {
                path = OrcidStringUtils.getOrcidNumber(uriStr);
                orcid.setPath(path);
            }

            if (StringUtils.isBlank(path)) {
                c.setContributorOrcid(null);
            } else {
                if (StringUtils.isBlank(host) && StringUtils.isNotBlank(uriStr)) {
                    try {
                        URI u = new URI(uriStr);
                        host = u.getHost();
                    } catch (Exception e) {
                        // ignore
                    }
                }
                if (StringUtils.isBlank(host)) {
                    host = (orcidUrlManager != null && StringUtils.isNotBlank(orcidUrlManager.getBaseHost()))
                            ? orcidUrlManager.getBaseHost() : "orcid.org";
                }
                orcid.setHost(host);

                if (StringUtils.isBlank(uriStr)) {
                    String baseUrl = (orcidUrlManager != null && StringUtils.isNotBlank(orcidUrlManager.getBaseUriHttp()))
                            ? orcidUrlManager.getBaseUriHttp() : "http://orcid.org";
                    orcid.setUri(baseUrl + "/" + path);
                }
            }
        }

        if (c.getContributorAttributes() != null) {
            if (c.getContributorAttributes().getContributorRole() == null && c.getContributorAttributes().getContributorSequence() == null) {
                c.setContributorAttributes(null);
            }
        }
    }
}