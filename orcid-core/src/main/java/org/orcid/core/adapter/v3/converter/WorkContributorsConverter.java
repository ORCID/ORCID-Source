package org.orcid.core.adapter.v3.converter;

import org.orcid.core.contributors.ContributorRoleConverter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WorkContributorsConverter extends BidirectionalConverter<WorkContributors, String> {

    private ContributorRoleConverter roleConverter;

    public WorkContributorsConverter(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    @Override
    public String convertTo(WorkContributors source, Type<String> destinationType) {
        // convert role to db format
        source.getContributor().forEach(c -> {
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                c.getContributorAttributes().setContributorRole(roleConverter.toDBRole(c.getContributorAttributes().getContributorRole()));
            }
        });
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public WorkContributors convertFrom(String source, Type<WorkContributors> destinationType) {
        WorkContributors workContributors = JsonUtils.readObjectFromJsonString(source, WorkContributors.class);
        workContributors.getContributor().forEach(c -> c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName()));

        // convert role to API format
        workContributors.getContributor().forEach(c -> {
            if (c.getContributorAttributes() != null && c.getContributorAttributes().getContributorRole() != null) {
                c.getContributorAttributes().setContributorRole(roleConverter.toRoleValue(c.getContributorAttributes().getContributorRole()));
            }
        });
        return workContributors;
    }
}
