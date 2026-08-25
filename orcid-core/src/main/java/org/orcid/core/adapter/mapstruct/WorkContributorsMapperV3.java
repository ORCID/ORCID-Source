package org.orcid.core.adapter.mapstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.InvalidContributorRoleException;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.contributors.roles.works.LegacyWorkContributorRole;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.pojo.WorkContributorsList;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public String convertTo(WorkContributors source) {
        if (source == null) {
            return null;
        }
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
        return JsonUtils.convertToJsonString(source);
    }

    public WorkContributors convertFrom(String source) {
        if (PojoUtil.isEmpty(source)) {
            return null;
        }
        WorkContributors workContributors = JsonUtils.readObjectFromJsonString(source, WorkContributors.class);
        if (workContributors == null) {
            return null;
        }

        // convert role to API format
        workContributors.getContributor().forEach(c -> {
            // Set the credit name
            c.setCreditName((c.getCreditName() != null && PojoUtil.isEmpty(c.getCreditName().getContent())) ? null : c.getCreditName());

            // Set the contributor attributes
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                c.getContributorAttributes().setContributorRole(roleConverter.toRoleValue(c.getContributorAttributes().getContributorRole()));
            }
        });
        return workContributors;
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
                if (workContributorsList.getContributor() != null && workContributorsList.getContributor().getContributorAttributes() != null) {
                    ContributorAttributes ca = workContributorsList.getContributor().getContributorAttributes();
                    String providedRoleValue = ca.getContributorRole();
                    if (!PojoUtil.isEmpty(providedRoleValue)) {
                        ca.setContributorRole(roleConverter.toRoleValue(providedRoleValue));
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