package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mapper(componentModel = "spring")
public abstract class ExternalIdentifierTypeMapper {

    public static final ExternalIdentifierTypeMapper INSTANCE = Mappers.getMapper(ExternalIdentifierTypeMapper.class);

    private final Map<String, String> fromMap = new ConcurrentHashMap<>();

    @Named("apiToDb")
    public String convertTo(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return source.trim().toUpperCase().replace("-", "_");
    }

    @Named("dbToApi")
    public String convertFrom(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        
        String trimmed = source.trim();
        
        if (fromMap.containsKey(trimmed)) {
            return fromMap.get(trimmed);
        }
        
        if ("GRANT_NUMBER".equalsIgnoreCase(trimmed)) {
            return "grant_number";
        }
        
        String result = trimmed.toLowerCase().replace("_", "-");
        fromMap.put(trimmed, result);
        
        return result;
    }
}