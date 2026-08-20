package org.orcid.core.adapter.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV2;
import org.orcid.core.adapter.mapstruct.JSONPeerReviewWorkExternalIdentifierMapperV2;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;


@Mapper(
    componentModel = "spring", 
    uses = {
        SourceMapperV2.class, 
        VisibilityMapperV2.class, 
        FuzzyDateMapperV2.class,
        JSONPeerReviewWorkExternalIdentifierMapperV2.class
    }
)
public abstract class JpaJaxbPeerReviewAdapterImpl implements JpaJaxbPeerReviewAdapter {
    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "reviewerRole", target = "reviewerRole")
    @Mapping(source = "reviewUrl.value", target = "reviewUrl")
    @Mapping(source = "reviewType", target = "reviewType")
    @Mapping(source = "reviewGroupId", target = "groupId")
    @Mapping(source = "subjectExternalIdentifier.type", target = "subjectExternalIdentifierType")
    @Mapping(source = "subjectExternalIdentifier.value", target = "subjectExternalIdentifierValue")
    @Mapping(source = "subjectExternalIdentifier.url.value", target = "subjectUrl")
    @Mapping(source = "subjectExternalIdentifier.relationship", target = "subjectExternalIdentifierRelationship")
    @Mapping(source = "subjectContainerName.content", target = "subjectContainerName")
    @Mapping(source = "subjectType", target = "subjectType")
    @Mapping(source = "subjectName.title.content", target = "subjectTitle")
    @Mapping(source = "subjectName.subtitle.content", target = "subjectSubtitle")
    @Mapping(source = "subjectName.translatedTitle.content", target = "subjectTranslatedTitle")
    @Mapping(source = "subjectName.translatedTitle.languageCode", target = "subjectTranslatedTitleLanguageCode")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    // Security & Audit ignore guards
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    public abstract PeerReviewEntity toPeerReviewEntity(PeerReview peerReview);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "reviewerRole", target = "reviewerRole")
    @Mapping(source = "reviewUrl", target = "reviewUrl.value")
    @Mapping(source = "reviewType", target = "reviewType")
    @Mapping(source = "groupId", target = "reviewGroupId")
    @Mapping(source = "subjectExternalIdentifierType", target = "subjectExternalIdentifier.type")
    @Mapping(source = "subjectExternalIdentifierValue", target = "subjectExternalIdentifier.value")
    @Mapping(source = "subjectUrl", target = "subjectExternalIdentifier.url.value")
    @Mapping(source = "subjectExternalIdentifierRelationship", target = "subjectExternalIdentifier.relationship")
    @Mapping(source = "subjectContainerName", target = "subjectContainerName.content")
    @Mapping(source = "subjectType", target = "subjectType")
    @Mapping(source = "subjectTitle", target = "subjectName.title.content")
    @Mapping(source = "subjectSubtitle", target = "subjectName.subtitle.content")
    @Mapping(source = "subjectTranslatedTitle", target = "subjectName.translatedTitle.content")
    @Mapping(source = "subjectTranslatedTitleLanguageCode", target = "subjectName.translatedTitle.languageCode")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    // Nested Org Mappings
    @Mapping(source = "org.name", target = "organization.name")
    @Mapping(source = "org.city", target = "organization.address.city")
    @Mapping(source = "org.region", target = "organization.address.region")
    @Mapping(source = "org.country", target = "organization.address.country")
    @Mapping(source = "org.orgDisambiguated.sourceId", target = "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier")
    @Mapping(source = "org.orgDisambiguated.sourceType", target = "organization.disambiguatedOrganization.disambiguationSource")
    @Mapping(source = "org.orgDisambiguated.id", target = "organization.disambiguatedOrganization.id")
    public abstract PeerReview toPeerReview(PeerReviewEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "reviewerRole", target = "reviewerRole")
    @Mapping(source = "reviewUrl", target = "reviewUrl.value")
    @Mapping(source = "reviewType", target = "reviewType")
    @Mapping(source = "groupId", target = "reviewGroupId")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    // Nested Org Mappings
    @Mapping(source = "org.name", target = "organization.name")
    @Mapping(source = "org.city", target = "organization.address.city")
    @Mapping(source = "org.region", target = "organization.address.region")
    @Mapping(source = "org.country", target = "organization.address.country")
    @Mapping(source = "org.orgDisambiguated.sourceId", target = "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier")
    @Mapping(source = "org.orgDisambiguated.sourceType", target = "organization.disambiguatedOrganization.disambiguationSource")
    @Mapping(source = "org.orgDisambiguated.id", target = "organization.disambiguatedOrganization.id")
    public abstract PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity);


    @Override
    public abstract List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities);

    @Override
    public abstract List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "reviewerRole", target = "reviewerRole")
    @Mapping(source = "reviewUrl.value", target = "reviewUrl")
    @Mapping(source = "reviewType", target = "reviewType")
    @Mapping(source = "reviewGroupId", target = "groupId")
    @Mapping(source = "subjectExternalIdentifier.type", target = "subjectExternalIdentifierType")
    @Mapping(source = "subjectExternalIdentifier.value", target = "subjectExternalIdentifierValue")
    @Mapping(source = "subjectExternalIdentifier.url.value", target = "subjectUrl")
    @Mapping(source = "subjectExternalIdentifier.relationship", target = "subjectExternalIdentifierRelationship")
    @Mapping(source = "subjectContainerName.content", target = "subjectContainerName")
    @Mapping(source = "subjectType", target = "subjectType")
    @Mapping(source = "subjectName.title.content", target = "subjectTitle")
    @Mapping(source = "subjectName.subtitle.content", target = "subjectSubtitle")
    @Mapping(source = "subjectName.translatedTitle.content", target = "subjectTranslatedTitle")
    @Mapping(source = "subjectName.translatedTitle.languageCode", target = "subjectTranslatedTitleLanguageCode")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    public abstract PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, @MappingTarget PeerReviewEntity existing);
}
