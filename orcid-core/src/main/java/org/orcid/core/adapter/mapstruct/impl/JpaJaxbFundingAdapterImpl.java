package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import java.math.BigDecimal;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import org.orcid.core.adapter.JpaJaxbFundingAdapter;
import org.orcid.core.adapter.mapstruct.*;

import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

@Mapper(
    componentModel = "spring", 
    uses = {
        SourceMapperV2.class, 
        VisibilityMapperV2.class, 
        FuzzyDateMapperV2.class,
        OrgMapperV2.class,
        UrlMapperV2.class,
        TitleMapperV2.class,
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
    @Mapping(source = "title.title", target = "title")
    @Mapping(source = "title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "amount.content", target = "numericAmount", qualifiedByName = "amountContentToNumericAmount")
    @Mapping(source = "amount.currencyCode", target = "currencyCode")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "contributors", target = "contributorsJson")
    // Orika mapped these as fieldBToA (Database -> API only)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    public abstract ProfileFundingEntity toProfileFundingEntity(Funding funding);

    // Preserves legacy Orika behavior: parses via Double so trailing ".0" is retained (e.g. "1234" -> 1234.0)
    @Named("amountContentToNumericAmount")
    protected BigDecimal amountContentToNumericAmount(String content) {
        return content == null ? null : BigDecimal.valueOf(Double.valueOf(content));
    }


    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "organizationDefinedType", target = "organizationDefinedType.content")
    @Mapping(source = "title", target = "title.title")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "numericAmount", target = "amount.content")
    @Mapping(source = "currencyCode", target = "amount.currencyCode")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "contributorsJson", target = "contributors")
    // Nested org mappings
    @Mapping(source = "org", target = "organization")
    @Mapping(source = ".", target = "source")
    public abstract Funding toFunding(ProfileFundingEntity profileFundingEntity);

    @AfterMapping
    protected void afterToFunding(ProfileFundingEntity entity, @MappingTarget Funding funding) {
        if (funding.getTitle() != null) {
            if (funding.getTitle().getTranslatedTitle() != null && 
                (funding.getTitle().getTranslatedTitle().getContent() == null || funding.getTitle().getTranslatedTitle().getContent().trim().isEmpty())) {
                funding.getTitle().setTranslatedTitle(null);
            }
            if (funding.getTitle().getTitle() == null && funding.getTitle().getTranslatedTitle() == null) {
                funding.setTitle(null);
            }
        }
        if (funding.getOrganizationDefinedType() != null && (funding.getOrganizationDefinedType().getContent() == null || funding.getOrganizationDefinedType().getContent().trim().isEmpty())) {
            funding.setOrganizationDefinedType(null);
        }
        if (funding.getAmount() != null && funding.getAmount().getContent() == null && funding.getAmount().getCurrencyCode() == null) {
            funding.setAmount(null);
        }
    }

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "title", target = "title.title")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    // Nested org mappings
    @Mapping(source = "org", target = "organization")
    @Mapping(source = ".", target = "source")
    public abstract FundingSummary toFundingSummary(ProfileFundingEntity profileFundingEntity);

    @AfterMapping
    protected void afterToFundingSummary(ProfileFundingEntity entity, @MappingTarget FundingSummary fundingSummary) {
        if (fundingSummary.getTitle() != null) {
            if (fundingSummary.getTitle().getTranslatedTitle() != null && 
                (fundingSummary.getTitle().getTranslatedTitle().getContent() == null || fundingSummary.getTitle().getTranslatedTitle().getContent().trim().isEmpty())) {
                fundingSummary.getTitle().setTranslatedTitle(null);
            }
            if (fundingSummary.getTitle().getTitle() == null && fundingSummary.getTitle().getTranslatedTitle() == null) {
                fundingSummary.setTitle(null);
            }
        }
    }

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
    @Mapping(source = "title.title", target = "title")
    @Mapping(source = "title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "amount.content", target = "numericAmount", qualifiedByName = "amountContentToNumericAmount")
    @Mapping(source = "amount.currencyCode", target = "currencyCode")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "contributors", target = "contributorsJson")
    // Ignore security-sensitive fields on update
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "org", ignore = true)
    public abstract ProfileFundingEntity toProfileFundingEntity(Funding funding, @MappingTarget ProfileFundingEntity existing);
}