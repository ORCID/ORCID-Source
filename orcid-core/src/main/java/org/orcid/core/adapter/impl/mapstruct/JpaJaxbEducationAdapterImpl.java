package org.orcid.core.adapter.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV2;
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
    uses = {SourceMapperV2.class, FuzzyDateMapperV2.class, VisibilityMapperV2.class}
)
public abstract class JpaJaxbEducationAdapterImpl implements JpaJaxbEducationAdapter {

    // ========================================================================
    // API -> Database (Creation)
    // ========================================================================

    /**
     * Manual wrapper implementation to ensure displayIndex is ONLY defaulted 
     * during new entity creation, matching the original logic.
     */
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

    /**
     * Internal method for MapStruct to generate the creation logic.
     */
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    @Mapping(source = "lastModifiedDate.value", target = "lastModified")
    @Mapping(source = "departmentName", target = "department")
    @Mapping(source = "roleTitle", target = "title")
    // Orika fieldBToA rules dictated that 'org' properties were only mapped DB -> API.
    // We ignore them here to prevent accidental database overwrites from API submissions.
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
    // MapStruct natively handles the deep nested BToA mappings from Orika
    @Mapping(source = "org.name", target = "organization.name")
    @Mapping(source = "org.city", target = "organization.address.city")
    @Mapping(source = "org.region", target = "organization.address.region")
    @Mapping(source = "org.country", target = "organization.address.country")
    @Mapping(source = "org.orgDisambiguated.sourceId", target = "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier")
    @Mapping(source = "org.orgDisambiguated.sourceType", target = "organization.disambiguatedOrganization.disambiguationSource")
    @Mapping(source = "org.orgDisambiguated.id", target = "organization.disambiguatedOrganization.id")
    public abstract Education toEducation(OrgAffiliationRelationEntity entity);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "department", target = "departmentName")
    @Mapping(source = "title", target = "roleTitle")
    @Mapping(source = "org.name", target = "organization.name")
    @Mapping(source = "org.city", target = "organization.address.city")
    @Mapping(source = "org.region", target = "organization.address.region")
    @Mapping(source = "org.country", target = "organization.address.country")
    @Mapping(source = "org.orgDisambiguated.sourceId", target = "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier")
    @Mapping(source = "org.orgDisambiguated.sourceType", target = "organization.disambiguatedOrganization.disambiguationSource")
    @Mapping(source = "org.orgDisambiguated.id", target = "organization.disambiguatedOrganization.id")
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
    @Mapping(source = "createdDate.value", target = "dateCreated")
    @Mapping(source = "lastModifiedDate.value", target = "lastModified")
    @Mapping(source = "departmentName", target = "department")
    @Mapping(source = "roleTitle", target = "title")
    @Mapping(target = "org", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Education education, @MappingTarget OrgAffiliationRelationEntity existing);
}
