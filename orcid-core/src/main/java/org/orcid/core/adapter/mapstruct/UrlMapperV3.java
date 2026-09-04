package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.orcid.jaxb.model.v3.release.common.Url;

@Mapper(componentModel = "spring")
public interface UrlMapperV3 {

    default String map(Url url) {
        return url == null || StringUtils.isBlank(url.getValue()) ? null : url.getValue().trim();
    }

    default Url map(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        Url result = new Url();
        result.setValue(url.trim());
        return result;
    }
}