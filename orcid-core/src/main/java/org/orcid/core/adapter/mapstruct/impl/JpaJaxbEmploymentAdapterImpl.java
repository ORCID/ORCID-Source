package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbEmploymentAdapter;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV2;
import org.orcid.core.adapter.mapstruct.OrgMapperV2;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

@Mapper(
    componentModel = "spring", 
    uses = {
        SourceMapperV2.class, 
        FuzzyDateMapperV2.class, 
        VisibilityMapperV2.class,
        OrgMapperV2.class
    }
)
public abstract class JpaJaxbEmploymentAdapterImpl implements JpaJaxbEmploymentAdapter {

    // ========================================================================
    // API -> Database (Creation)
    // ========================================================================

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment) {
        if (employment == null) {
            return null;
        }
        
        OrgAffiliationRelationEntity entity = mapToEntity(employment);
        
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
    protected abstract OrgAffiliationRelationEntity mapToEntity(Employment employment);

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
    public abstract Employment toEmployment(OrgAffiliationRelationEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "department", target = "departmentName")
    @Mapping(source = "title", target = "roleTitle")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = ".", target = "source")
    public abstract EmploymentSummary toEmploymentSummary(OrgAffiliationRelationEntity entity);

    // ========================================================================
    // Collection Mappings
    // ========================================================================

    @Override
    public abstract List<Employment> toEmployment(Collection<OrgAffiliationRelationEntity> entities);

    @Override
    public abstract List<EmploymentSummary> toEmploymentSummary(Collection<OrgAffiliationRelationEntity> entities);

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
    public abstract OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment, @MappingTarget OrgAffiliationRelationEntity existing);
}