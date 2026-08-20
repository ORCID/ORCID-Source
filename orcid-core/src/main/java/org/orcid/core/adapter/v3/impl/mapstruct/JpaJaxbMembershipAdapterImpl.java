package org.orcid.core.adapter.v3.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.OrgMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbMembershipAdapter;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class,
        JSONExternalIdentifiersMapperV3.class,
        OrgMapperV3.class,
        FuzzyDateMapperV3.class
    }
)
public abstract class JpaJaxbMembershipAdapterImpl implements JpaJaxbMembershipAdapter {

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "departmentName", target = "department")
    @Mapping(source = "roleTitle", target = "title")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "organization", target = "org")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Membership membership);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "departmentName", target = "department")
    @Mapping(source = "roleTitle", target = "title")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "organization", target = "org")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Membership membership, @MappingTarget OrgAffiliationRelationEntity existing);


    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "department", target = "departmentName")
    @Mapping(source = "title", target = "roleTitle")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Membership toMembership(OrgAffiliationRelationEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "department", target = "departmentName")
    @Mapping(source = "title", target = "roleTitle")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract MembershipSummary toMembershipSummary(OrgAffiliationRelationEntity entity);


    // ========================================================================
    // Collection Mappings
    // ========================================================================

    @Override
    public abstract List<Membership> toMembership(Collection<OrgAffiliationRelationEntity> entities);

    @Override
    public abstract List<MembershipSummary> toMembershipSummary(Collection<OrgAffiliationRelationEntity> entities);
}