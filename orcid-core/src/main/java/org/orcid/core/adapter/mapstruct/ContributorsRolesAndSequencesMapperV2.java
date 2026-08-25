package org.orcid.core.adapter.mapstruct;

import org.orcid.core.utils.JsonUtils;
import org.orcid.pojo.ContributorsRolesAndSequencesV2;

import java.util.List;

/**
 * Plain, Orika-free converter for serializing ContributorsRolesAndSequencesV2 objects to JSON.
 */
public class ContributorsRolesAndSequencesMapperV2 {

    public String convertTo(List<ContributorsRolesAndSequencesV2> source) {
        return JsonUtils.convertToJsonString(source);
    }

    public List<ContributorsRolesAndSequencesV2> convertFrom(String source) {
        return null;
    }

}

