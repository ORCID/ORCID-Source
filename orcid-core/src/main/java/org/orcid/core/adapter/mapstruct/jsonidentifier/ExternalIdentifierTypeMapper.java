package org.orcid.core.adapter.mapstruct.jsonidentifier;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Translates the API representation of identifier types (including
 * relationships) into the DB representation. e.g. PART-OF becomes part_of,
 * OTHER-ID becomes other_id
 * 
 * @author Camelia Dumitru
 */
@Mapper(componentModel = "spring")
public abstract class ExternalIdentifierTypeMapper {

    // Retained for non-Spring instantiation if needed
    public static final ExternalIdentifierTypeMapper INSTANCE = Mappers.getMapper(ExternalIdentifierTypeMapper.class);

    // Upgraded to ConcurrentHashMap to ensure thread safety in a singleton context
    private final Map<String, String> fromMap = new ConcurrentHashMap<>();

    /**
     * Replaces Orika's convertTo() logic (API to Database)
     */
    @Named("apiToDb")
    public String convertTo(String source) {
        if (source == null) {
            return null;
        }
        return source.toUpperCase().replace("-", "_");
    }

    /**
     * Replaces Orika's convertFrom() logic (Database to API)
     */
    @Named("dbToApi")
    public String convertFrom(String source) {
        if (source == null) {
            return null;
        }
        
        if (fromMap.containsKey(source)) {
            return fromMap.get(source);
        }
        
        // annoying hack because grant_number does it differently.
        if (source.equals("GRANT_NUMBER")) {
            return "grant_number";
        }
        
        String result = source.toLowerCase().replace("_", "-");
        fromMap.put(source, result);
        
        return result;
    }
}