package org.orcid.core.adapter.mapstruct;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.core.utils.JsonUtils;
import org.orcid.pojo.ajaxForm.PojoUtil;

@Mapper
public interface AdditionalInfoJsonMapper {

    AdditionalInfoJsonMapper INSTANCE = Mappers.getMapper(AdditionalInfoJsonMapper.class);

    @SuppressWarnings("unchecked")
    default Map<String, Object> fromJson(String additionalInfo) {
        if (PojoUtil.isEmpty(additionalInfo)) {
            return null;
        }
        return (Map<String, Object>) JsonUtils.readObjectFromJsonString(additionalInfo, HashMap.class);
    }

    default String toJson(Map<String, Object> additionalInfo) {
        if (additionalInfo == null) {
            return null;
        }
        return JsonUtils.convertToJsonString(additionalInfo);
    }
}
