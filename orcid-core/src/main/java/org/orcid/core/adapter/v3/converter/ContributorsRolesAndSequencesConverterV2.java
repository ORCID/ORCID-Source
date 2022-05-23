package org.orcid.core.adapter.v3.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.orcid.core.utils.JsonUtils;
import org.orcid.pojo.ContributorsRolesAndSequencesV2;

import java.util.List;

public class ContributorsRolesAndSequencesConverterV2 extends BidirectionalConverter<List<ContributorsRolesAndSequencesV2>, String> {

    @Override
    public String convertTo(List<ContributorsRolesAndSequencesV2> source, Type<String> destinationType) {
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public List<ContributorsRolesAndSequencesV2> convertFrom(String source, Type<List<ContributorsRolesAndSequencesV2>> destinationType) {
        return null;
    }

}

