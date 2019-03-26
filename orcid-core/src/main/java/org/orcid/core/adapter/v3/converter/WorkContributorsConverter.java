package org.orcid.core.adapter.v3.converter;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.rc2.record.WorkContributors;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WorkContributorsConverter extends BidirectionalConverter<WorkContributors, String> {

    @Override
    public String convertTo(WorkContributors source, Type<String> destinationType) {
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public WorkContributors convertFrom(String source, Type<WorkContributors> destinationType) {
        WorkContributors workContributors = JsonUtils.readObjectFromJsonString(source, destinationType.getRawType());
        workContributors.getContributor().forEach(c -> c.setCreditName("".equals(c.getCreditName()) ? null : c.getCreditName()));
        return workContributors;
    }
}
