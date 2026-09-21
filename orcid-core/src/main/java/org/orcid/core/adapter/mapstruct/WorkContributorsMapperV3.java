package org.orcid.core.adapter.mapstruct;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.InvalidContributorRoleException;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.contributors.roles.works.LegacyWorkContributorRole;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.pojo.WorkContributorsList;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Plain, framework-free converter replacing the Orika WorkContributorsConverter.
 */
@Component("workContributorsMapperV3")
public class WorkContributorsMapperV3 {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkContributorsMapperV3.class);

    @Resource(name = "workContributorRoleConverter")
    private ContributorRoleConverter roleConverter;

    @Autowired(required = false)
    @Resource(name = "orcidUrlManager")
    private OrcidUrlManager orcidUrlManager;

    public WorkContributorsMapperV3() {
    }

    public WorkContributorsMapperV3(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public WorkContributorsMapperV3(ContributorRoleConverter roleConverter, OrcidUrlManager orcidUrlManager) {
        this.roleConverter = roleConverter;
        this.orcidUrlManager = orcidUrlManager;
    }

    public void setRoleConverter(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public void setOrcidUrlManager(OrcidUrlManager orcidUrlManager) {
        this.orcidUrlManager = orcidUrlManager;
    }

    public String convertTo(WorkContributors source) {
        if (source == null) {
            return null;
        }
        if (source.getContributor() != null) {
            // convert role to db format
            source.getContributor().forEach(c -> {
                if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                    String providedRoleValue = c.getContributorAttributes().getContributorRole();
                    String resolvedRoleValue = roleConverter.toDBRole(providedRoleValue);
                    if (resolvedRoleValue == null) {
                        Map<String, String> exceptionParams = new HashMap<>();
                        exceptionParams.put("role", providedRoleValue);

                        List<String> legalValues = new ArrayList<>();
                        for (LegacyWorkContributorRole role : LegacyWorkContributorRole.values()) {
                            legalValues.add(role.value());
                        }
                        for (CreditRole role : CreditRole.values()) {
                            legalValues.add(role.value());
                        }
                        exceptionParams.put("validRoles", legalValues.toString());

                        throw new InvalidContributorRoleException(exceptionParams);
                    }
                    c.getContributorAttributes().setContributorRole(resolvedRoleValue);
                }
            });
        }
        return JsonUtils.convertToJsonString(source);
    }

    public WorkContributors convertFrom(String source) {
        if (PojoUtil.isEmpty(source)) {
            return null;
        }
        WorkContributors workContributors = JsonUtils.readObjectFromJsonString(source, WorkContributors.class);
        if (workContributors == null || workContributors.getContributor() == null) {
            return workContributors;
        }

        // convert role to API format and normalize/clean contributor fields
        workContributors.getContributor().forEach(c -> {
            cleanAndPopulateContributor(c);
            // Set the contributor attributes
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                String apiRole = roleConverter.toRoleValue(c.getContributorAttributes().getContributorRole());
                c.getContributorAttributes().setContributorRole(apiRole);
                if (c.getContributorAttributes().getContributorRole() == null && c.getContributorAttributes().getContributorSequence() == null) {
                    c.setContributorAttributes(null);
                }
            }
        });
        return workContributors;
    }

    public void cleanAndPopulateContributor(Contributor c) {
        if (c == null) {
            return;
        }
        // Set the credit name
        if (c.getCreditName() != null && (c.getCreditName().getContent() == null || StringUtils.isBlank(c.getCreditName().getContent()))) {
            c.setCreditName(null);
        }

        // Set the contributor email
        if (c.getContributorEmail() != null && (c.getContributorEmail().getValue() == null || StringUtils.isBlank(c.getContributorEmail().getValue()))) {
            c.setContributorEmail(null);
        }

        // Set the contributor orcid
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
                    String baseUrl = (orcidUrlManager != null && StringUtils.isNotBlank(orcidUrlManager.getBaseUrl()))
                            ? orcidUrlManager.getBaseUrl() : "https://orcid.org";
                    orcid.setUri(baseUrl + "/" + path);
                }
            }
        }

        // Set contributor attributes
        if (c.getContributorAttributes() != null) {
            if (c.getContributorAttributes().getContributorRole() == null && c.getContributorAttributes().getContributorSequence() == null) {
                c.setContributorAttributes(null);
            }
        }
    }

    public List<WorkContributorsList> getContributorsList(String source) {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<WorkContributorsList> langList = new ArrayList<>();
        if (PojoUtil.isEmpty(source)) {
            return langList;
        }
        try {
            langList = objectMapper.readValue(source, new TypeReference<List<WorkContributorsList>>(){});
            for (WorkContributorsList workContributorsList : langList) {
                if (workContributorsList.getContributor() != null) {
                    cleanAndPopulateContributor(workContributorsList.getContributor());
                    if (workContributorsList.getContributor().getContributorAttributes() != null) {
                        ContributorAttributes ca = workContributorsList.getContributor().getContributorAttributes();
                        String providedRoleValue = ca.getContributorRole();
                        if (!PojoUtil.isEmpty(providedRoleValue)) {
                            ca.setContributorRole(roleConverter.toRoleValue(providedRoleValue));
                        }
                    }
                }
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return langList;
        } catch (Exception ioe) {
            LOGGER.error("Unable to process contributors", ioe);
            throw ioe;
        }
        return langList;
    }
}