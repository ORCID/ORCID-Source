package org.orcid.core.adapter.mapstruct.v3.impl;

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
import org.orcid.core.adapter.v3.JpaJaxbInvitedPositionAdapter;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
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
public abstract class JpaJaxbInvitedPositionAdapterImpl implements JpaJaxbInvitedPositionAdapter {

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
    public abstract OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(InvitedPosition invitedPosition);

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
    public abstract OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(InvitedPosition invitedPosition, @MappingTarget OrgAffiliationRelationEntity existing);

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
    @Mapping(source = ".", target = "source")
    public abstract InvitedPosition toInvitedPosition(OrgAffiliationRelationEntity entity);

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
    @Mapping(source = ".", target = "source")
    public abstract InvitedPositionSummary toInvitedPositionSummary(OrgAffiliationRelationEntity entity);

    @Override
    public abstract List<InvitedPosition> toInvitedPosition(Collection<OrgAffiliationRelationEntity> entities);

    @Override
    public abstract List<InvitedPositionSummary> toInvitedPositionSummary(Collection<OrgAffiliationRelationEntity> entities);
}