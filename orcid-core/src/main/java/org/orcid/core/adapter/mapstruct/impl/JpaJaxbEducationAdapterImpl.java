package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV2;
import org.orcid.core.adapter.mapstruct.OrgMapperV2;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * MapStruct automatically generates the implementation (JpaJaxbEducationAdapterImpl) 
 * and registers it as a Spring Component.
 */
@Mapper(
    componentModel = "spring", 
    uses = {
        SourceMapperV2.class, 
        FuzzyDateMapperV2.class, 
        VisibilityMapperV2.class,
        OrgMapperV2.class
    }
)
public abstract class JpaJaxbEducationAdapterImpl implements JpaJaxbEducationAdapter {

    // ========================================================================
    // API -> Database (Creation)
    // ========================================================================

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Education education) {
        if (education == null) {
            return null;
        }
        
        OrgAffiliationRelationEntity entity = mapToEntity(education);
        
        if (entity.getDisplayIndex() == null) {
            entity.setDisplayIndex(0L);
        }
        
        return entity;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "departmentName", target = "department")
    @Mapping(source = "roleTitle", target = "title")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract OrgAffiliationRelationEntity mapToEntity(Education education);


    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "department", target = "departmentName")
    @Mapping(source = "title", target = "roleTitle")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = ".", target = "source")
    public abstract Education toEducation(OrgAffiliationRelationEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "department", target = "departmentName")
    @Mapping(source = "title", target = "roleTitle")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = ".", target = "source")
    public abstract EducationSummary toEducationSummary(OrgAffiliationRelationEntity entity);


    // ========================================================================
    // Collection Mappings
    // ========================================================================

    @Override
    public abstract List<Education> toEducation(Collection<OrgAffiliationRelationEntity> entities);

    @Override
    public abstract List<EducationSummary> toEducationSummary(Collection<OrgAffiliationRelationEntity> entities);


    // ========================================================================
    // API -> Database (Update Existing)
    // ========================================================================

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "departmentName", target = "department")
    @Mapping(source = "roleTitle", target = "title")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Education education, @MappingTarget OrgAffiliationRelationEntity existing);
}