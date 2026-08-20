package org.orcid.core.adapter.v3.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONPeerReviewWorkExternalIdentifierMapperV3;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.OrgMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbPeerReviewAdapter;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class,
        OrgMapperV3.class,
        FuzzyDateMapperV3.class,
        JSONWorkExternalIdentifiersMapperV3.class,
        JSONPeerReviewWorkExternalIdentifierMapperV3.class
    }
)
public abstract class JpaJaxbPeerReviewAdapterImpl implements JpaJaxbPeerReviewAdapter {

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "subjectUrl.value", target = "subjectUrl")
    @Mapping(source = "subjectName.title.content", target = "subjectName")
    @Mapping(source = "subjectName.translatedTitle.content", target = "subjectTranslatedName")
    @Mapping(source = "subjectName.translatedTitle.languageCode", target = "subjectTranslatedNameLanguageCode")
    @Mapping(source = "subjectContainerName.content", target = "subjectContainerName")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "subjectExternalIdentifier", target = "subjectExternalIdentifiersJson")
    @Mapping(source = "organization", target = "org")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract PeerReviewEntity toPeerReviewEntity(PeerReview peerReview);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "subjectUrl.value", target = "subjectUrl")
    @Mapping(source = "subjectName.title.content", target = "subjectName")
    @Mapping(source = "subjectName.translatedTitle.content", target = "subjectTranslatedName")
    @Mapping(source = "subjectName.translatedTitle.languageCode", target = "subjectTranslatedNameLanguageCode")
    @Mapping(source = "subjectContainerName.content", target = "subjectContainerName")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "subjectExternalIdentifier", target = "subjectExternalIdentifiersJson")
    @Mapping(source = "organization", target = "org")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, @MappingTarget PeerReviewEntity existing);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "subjectUrl", target = "subjectUrl.value")
    @Mapping(source = "subjectName", target = "subjectName.title.content")
    @Mapping(source = "subjectTranslatedName", target = "subjectName.translatedTitle.content")
    @Mapping(source = "subjectTranslatedNameLanguageCode", target = "subjectName.translatedTitle.languageCode")
    @Mapping(source = "subjectContainerName", target = "subjectContainerName.content")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "subjectExternalIdentifiersJson", target = "subjectExternalIdentifier")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract PeerReview toPeerReview(PeerReviewEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = "completionDate", target = "completionDate")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity);

    @Override
    public abstract List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities);

    @Override
    public abstract List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities);
}