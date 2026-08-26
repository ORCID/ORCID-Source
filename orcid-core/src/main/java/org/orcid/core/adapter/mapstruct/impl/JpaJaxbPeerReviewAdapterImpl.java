package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV2;
import org.orcid.core.adapter.mapstruct.JSONPeerReviewWorkExternalIdentifierMapperV2;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV2;
import org.orcid.core.adapter.mapstruct.OrgMapperV2;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

@Mapper(
    componentModel = "spring", 
    uses = {
        SourceMapperV2.class, 
        VisibilityMapperV2.class, 
        FuzzyDateMapperV2.class,
        OrgMapperV2.class,
        JSONWorkExternalIdentifiersMapperV2.class,
        JSONPeerReviewWorkExternalIdentifierMapperV2.class
    }
)
public abstract class JpaJaxbPeerReviewAdapterImpl implements JpaJaxbPeerReviewAdapter {

    // ========================================================================
    // Custom Mapping Methods
    // ========================================================================

    public WorkType mapSubjectType(String subjectType) {
        if (subjectType == null) {
            return null;
        }
        try {
            return WorkType.valueOf(subjectType);
        } catch (IllegalArgumentException e) {
            // Fallback for invalid legacy data like "GRANT"
            return WorkType.OTHER;
        }
    }

    // ========================================================================
    // API -> Database (Creation)
    // ========================================================================

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "subjectExternalIdentifier", target = "subjectExternalIdentifiersJson")
    @Mapping(source = "subjectContainerName.content", target = "subjectContainerName")
    @Mapping(source = "subjectType", target = "subjectType")
    @Mapping(source = "subjectName.title.content", target = "subjectName")
    @Mapping(source = "subjectName.translatedTitle.content", target = "subjectTranslatedName")
    @Mapping(source = "subjectName.translatedTitle.languageCode", target = "subjectTranslatedNameLanguageCode")
    @Mapping(source = "subjectUrl.value", target = "subjectUrl")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(source = "organization", target = "org")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract PeerReviewEntity toPeerReviewEntity(PeerReview peerReview);


    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "subjectExternalIdentifiersJson", target = "subjectExternalIdentifier")
    @Mapping(source = "subjectContainerName", target = "subjectContainerName.content")
    @Mapping(source = "subjectType", target = "subjectType")
    @Mapping(source = "subjectName", target = "subjectName.title.content")
    @Mapping(source = "subjectTranslatedName", target = "subjectName.translatedTitle.content")
    @Mapping(source = "subjectTranslatedNameLanguageCode", target = "subjectName.translatedTitle.languageCode")
    @Mapping(source = "subjectUrl", target = "subjectUrl.value")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = ".", target = "source")
    public abstract PeerReview toPeerReview(PeerReviewEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = ".", target = "source")
    public abstract PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity);


    // ========================================================================
    // Collection Mappings
    // ========================================================================

    @Override
    public abstract List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities);

    @Override
    public abstract List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities);


    // ========================================================================
    // API -> Database (Update Existing)
    // ========================================================================

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "subjectExternalIdentifier", target = "subjectExternalIdentifiersJson")
    @Mapping(source = "subjectContainerName.content", target = "subjectContainerName")
    @Mapping(source = "subjectType", target = "subjectType")
    @Mapping(source = "subjectName.title.content", target = "subjectName")
    @Mapping(source = "subjectName.translatedTitle.content", target = "subjectTranslatedName")
    @Mapping(source = "subjectName.translatedTitle.languageCode", target = "subjectTranslatedNameLanguageCode")
    @Mapping(source = "subjectUrl.value", target = "subjectUrl")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(source = "organization", target = "org")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, @MappingTarget PeerReviewEntity existing);
}