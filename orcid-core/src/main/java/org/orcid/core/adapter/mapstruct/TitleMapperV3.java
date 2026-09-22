package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.orcid.jaxb.model.v3.release.common.Title;

@Mapper(componentModel = "spring")
public interface TitleMapperV3 {

    default String map(Title title) {
        return title == null || StringUtils.isBlank(title.getContent()) ? null : title.getContent().trim();
    }

    default Title map(String title) {
        if (StringUtils.isBlank(title)) {
            return null;
        }
        Title result = new Title();
        result.setContent(title.trim());
        return result;
    }
}
