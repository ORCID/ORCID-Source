package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.FundingContributorsMapperV3;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONFundingExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.OrgMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbFundingAdapter;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class,
        OrgMapperV3.class,
        FuzzyDateMapperV3.class,
        JSONFundingExternalIdentifiersMapperV3.class,
        FundingContributorsMapperV3.class
    }
)
public abstract class JpaJaxbFundingAdapterImpl implements JpaJaxbFundingAdapter {

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "organizationDefinedType.content", target = "organizationDefinedType")
    @Mapping(source = "title.title.content", target = "title")
    @Mapping(source = "title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "amount.content", target = "numericAmount")
    @Mapping(source = "amount.currencyCode", target = "currencyCode")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "organization", target = "org")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "contributors", target = "contributorsJson")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ProfileFundingEntity toProfileFundingEntity(Funding funding);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "organizationDefinedType.content", target = "organizationDefinedType")
    @Mapping(source = "title.title.content", target = "title")
    @Mapping(source = "title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "amount.content", target = "numericAmount")
    @Mapping(source = "amount.currencyCode", target = "currencyCode")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "organization", target = "org")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "contributors", target = "contributorsJson")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ProfileFundingEntity toProfileFundingEntity(Funding funding, @MappingTarget ProfileFundingEntity existing);


    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "organizationDefinedType", target = "organizationDefinedType.content")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "numericAmount", target = "amount.content")
    @Mapping(source = "currencyCode", target = "amount.currencyCode")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "contributorsJson", target = "contributors")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract Funding toFunding(ProfileFundingEntity profileFundingEntity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "org", target = "organization")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract FundingSummary toFundingSummary(ProfileFundingEntity profileFundingEntity);

    @Override
    public abstract List<Funding> toFunding(Collection<ProfileFundingEntity> fundingEntities);

    @Override
    public abstract List<FundingSummary> toFundingSummary(Collection<ProfileFundingEntity> fundingEntities);
}