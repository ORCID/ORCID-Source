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
import org.orcid.core.contributors.roles.fundings.LegacyFundingContributorRole;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.jaxb.model.v3.release.record.FundingContributors;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Replaces the old Orika-only {@code org.orcid.core.adapter.v3.converter.FundingContributorsConverter}.
 * Plain, framework-free conversion logic - no Orika dependency.
 */
public class FundingContributorsMapperV3 {

    @Resource(name = "fundingContributorRoleConverter")
    private ContributorRoleConverter roleConverter;

    @Autowired(required = false)
    @Resource(name = "orcidUrlManager")
    private OrcidUrlManager orcidUrlManager;

    public FundingContributorsMapperV3() {
    }

    public FundingContributorsMapperV3(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public FundingContributorsMapperV3(ContributorRoleConverter roleConverter, OrcidUrlManager orcidUrlManager) {
        this.roleConverter = roleConverter;
        this.orcidUrlManager = orcidUrlManager;
    }

    public void setRoleConverter(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    public void setOrcidUrlManager(OrcidUrlManager orcidUrlManager) {
        this.orcidUrlManager = orcidUrlManager;
    }

    public String convertTo(FundingContributors source) {
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
                        for (LegacyFundingContributorRole role : LegacyFundingContributorRole.values()) {
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

    public FundingContributors convertFrom(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        FundingContributors fundingContributors = JsonUtils.readObjectFromJsonString(source, FundingContributors.class);
        if (fundingContributors == null || fundingContributors.getContributor() == null) {
            return fundingContributors;
        }

        // convert role to API format and normalize/clean contributor fields
        fundingContributors.getContributor().forEach(c -> {
            cleanAndPopulateContributor(c);
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                String apiRole = roleConverter.toRoleValue(c.getContributorAttributes().getContributorRole());
                c.getContributorAttributes().setContributorRole(apiRole);
                if (c.getContributorAttributes().getContributorRole() == null) {
                    c.setContributorAttributes(null);
                }
            }
        });
        return fundingContributors;
    }

    public void cleanAndPopulateContributor(FundingContributor c) {
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
                    String baseUrl = (orcidUrlManager != null && StringUtils.isNotBlank(orcidUrlManager.getBaseUrl()))
                            ? orcidUrlManager.getBaseUrl() : "https://orcid.org";
                    orcid.setUri(baseUrl + "/" + path);
                }
            }
        }

        if (c.getContributorAttributes() != null) {
            if (c.getContributorAttributes().getContributorRole() == null) {
                c.setContributorAttributes(null);
            }
        }
    }
}
