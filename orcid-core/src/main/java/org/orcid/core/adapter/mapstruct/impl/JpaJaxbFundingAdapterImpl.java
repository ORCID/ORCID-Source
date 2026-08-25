package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbFundingAdapter;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV2;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.core.adapter.mapstruct.JSONFundingExternalIdentifiersMapperV2;
import org.orcid.core.adapter.mapstruct.FundingContributorsMapperV2;

import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

@Mapper(
    componentModel = "spring", 
    uses = {
        SourceMapperV2.class, 
        VisibilityMapperV2.class, 
        FuzzyDateMapperV2.class,
        JSONFundingExternalIdentifiersMapperV2.class, 
        FundingContributorsMapperV2.class
    }
)
public abstract class JpaJaxbFundingAdapterImpl implements JpaJaxbFundingAdapter {

    // ========================================================================
    // API -> Database (Creation)
    // ========================================================================

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "organizationDefinedType.content", target = "organizationDefinedType")
    @Mapping(source = "title.title.content", target = "title")
    @Mapping(source = "title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    // MapStruct natively handles String <-> BigDecimal conversion for numericAmount
    @Mapping(source = "amount.content", target = "numericAmount")
    @Mapping(source = "amount.currencyCode", target = "currencyCode")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "contributors", target = "contributorsJson")
    // Orika mapped these as fieldBToA (Database -> API only)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    public abstract ProfileFundingEntity toProfileFundingEntity(Funding funding);


    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "organizationDefinedType", target = "organizationDefinedType.content")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "numericAmount", target = "amount.content")
    @Mapping(source = "currencyCode", target = "amount.currencyCode")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "contributorsJson", target = "contributors")
    // Nested org mappings
    @Mapping(source = "org.name", target = "organization.name")
    @Mapping(source = "org.city", target = "organization.address.city")
    @Mapping(source = "org.region", target = "organization.address.region")
    @Mapping(source = "org.country", target = "organization.address.country")
    @Mapping(source = "org.orgDisambiguated.sourceId", target = "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier")
    @Mapping(source = "org.orgDisambiguated.sourceType", target = "organization.disambiguatedOrganization.disambiguationSource")
    @Mapping(source = "org.orgDisambiguated.id", target = "organization.disambiguatedOrganization.id")
    public abstract Funding toFunding(ProfileFundingEntity profileFundingEntity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    // Nested org mappings
    @Mapping(source = "org.name", target = "organization.name")
    @Mapping(source = "org.city", target = "organization.address.city")
    @Mapping(source = "org.region", target = "organization.address.region")
    @Mapping(source = "org.country", target = "organization.address.country")
    @Mapping(source = "org.orgDisambiguated.sourceId", target = "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier")
    @Mapping(source = "org.orgDisambiguated.sourceType", target = "organization.disambiguatedOrganization.disambiguationSource")
    @Mapping(source = "org.orgDisambiguated.id", target = "organization.disambiguatedOrganization.id")
    public abstract FundingSummary toFundingSummary(ProfileFundingEntity profileFundingEntity);



    @Override
    public abstract List<Funding> toFunding(Collection<ProfileFundingEntity> fundingEntities);

    @Override
    public abstract List<FundingSummary> toFundingSummary(Collection<ProfileFundingEntity> fundingEntities);


    // ========================================================================
    // API -> Database (Update Existing)
    // ========================================================================

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "organizationDefinedType.content", target = "organizationDefinedType")
    @Mapping(source = "title.title.content", target = "title")
    @Mapping(source = "title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "amount.content", target = "numericAmount")
    @Mapping(source = "amount.currencyCode", target = "currencyCode")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "contributors", target = "contributorsJson")
    // Ignore security-sensitive fields on update
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    public abstract ProfileFundingEntity toProfileFundingEntity(Funding funding, @MappingTarget ProfileFundingEntity existing);
}