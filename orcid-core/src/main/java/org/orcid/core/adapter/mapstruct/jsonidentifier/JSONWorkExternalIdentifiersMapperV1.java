package org.orcid.core.adapter.mapstruct.jsonidentifier;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifiers;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * Custom MapStruct Mapper for converting WorkExternalIdentifiers to and from JSON.
 */
@Mapper(componentModel = "spring")
public abstract class JSONWorkExternalIdentifiersMapperV1 {

    @Autowired
    protected ExternalIdentifierTypeMapper typeMapper;

    /**
     * Converts the JAXB message POJO into a JSON String for the database.
     * 
     * @param workIdentifiersObj The source object.
     * @param workType    The @Context annotation tells MapStruct this is context 
     *                    for business logic, not a source object to be mapped.
     */
    public String convertTo(WorkExternalIdentifiers workIdentifiersObj, @Context WorkType workType) {
        if (workIdentifiersObj == null || workIdentifiersObj.getWorkExternalIdentifier() == null) {
            return null;
        }

        JSONWorkExternalIdentifiers workExternalIdentifiers = new JSONWorkExternalIdentifiers();
        
        for (WorkExternalIdentifier workExternalIdentifier : workIdentifiersObj.getWorkExternalIdentifier()) {
            JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
            
            if (workExternalIdentifier.getWorkExternalIdentifierType() != null) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierType(workExternalIdentifier.getWorkExternalIdentifierType().value());
            }
            
            if (workExternalIdentifier.getWorkExternalIdentifierId() != null && !PojoUtil.isEmpty(workExternalIdentifier.getWorkExternalIdentifierId().getContent())) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(workExternalIdentifier.getWorkExternalIdentifierId().getContent()));
            }
            
            // Use the injected typeMapper instead of instantiating manually
            String rawType = typeMapper.convertTo(jsonWorkExternalIdentifier.getWorkExternalIdentifierType());
            WorkExternalIdentifierType type = WorkExternalIdentifierType.valueOf(rawType);

            // Conditional relationship logic based on the @Context WorkType
            if (WorkExternalIdentifierType.ISSN.equals(type)) {
                if (!WorkType.BOOK.equals(workType)) {
                    jsonWorkExternalIdentifier.setRelationship(Relationship.PART_OF.value());
                } else {
                    jsonWorkExternalIdentifier.setRelationship(Relationship.SELF.value());
                }
            } else if (WorkExternalIdentifierType.ISBN.equals(type)) {
                if (WorkType.BOOK_CHAPTER.equals(workType) || WorkType.CONFERENCE_PAPER.equals(workType)) {
                    jsonWorkExternalIdentifier.setRelationship(Relationship.PART_OF.value());
                } else {
                    jsonWorkExternalIdentifier.setRelationship(Relationship.SELF.value());
                }
            } else {
                jsonWorkExternalIdentifier.setRelationship(Relationship.SELF.value());
            }
            
            workExternalIdentifiers.getWorkExternalIdentifier().add(jsonWorkExternalIdentifier);
        }
        
        return JsonUtils.convertToJsonString(workExternalIdentifiers);
    }

    /**
     * Converts the database JSON String back into the JAXB message POJO.
     */
    public WorkExternalIdentifiers convertFrom(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        JSONWorkExternalIdentifiers jsonWorkExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONWorkExternalIdentifiers.class);
        if (jsonWorkExternalIdentifiers == null || jsonWorkExternalIdentifiers.getWorkExternalIdentifier() == null) {
            return null;
        }

        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        
        for (JSONWorkExternalIdentifier jsonWorkExternalIdentifier : jsonWorkExternalIdentifiers.getWorkExternalIdentifier()) {
            // Filter out VERSION_OF relationships
            if (jsonWorkExternalIdentifier.getRelationship() != null && !Relationship.VERSION_OF.name().equals(jsonWorkExternalIdentifier.getRelationship())) {
                WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
                
                try {
                    // Use the injected typeMapper
                    String rawType = typeMapper.convertFrom(jsonWorkExternalIdentifier.getWorkExternalIdentifierType());
                    workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(rawType));
                } catch (Exception e) {
                    workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.OTHER_ID);
                }
                
                workExternalIdentifier.setWorkExternalIdentifierId(new org.orcid.jaxb.model.message.WorkExternalIdentifierId());
                if (jsonWorkExternalIdentifier.getWorkExternalIdentifierId() != null) {
                    workExternalIdentifier.getWorkExternalIdentifierId().setContent(jsonWorkExternalIdentifier.getWorkExternalIdentifierId().content);
                }
                
                workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier);
            }            
        }
        
        return workExternalIdentifiers;
    }
}
