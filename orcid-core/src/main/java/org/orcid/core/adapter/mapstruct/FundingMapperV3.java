package org.orcid.core.adapter.mapstruct;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

@Mapper
public interface FundingMapperV3 {

    FundingMapperV3 INSTANCE = Mappers.getMapper(FundingMapperV3.class);

    default void mapFundingCustomFields(Funding funding, ProfileFundingEntity entity) {
        entity.setOrganizationDefinedType(funding.getOrganizationDefinedType() == null ? null : funding.getOrganizationDefinedType().getContent());
        entity.setUrl(funding.getUrl() == null ? null : funding.getUrl().getValue());
        entity.setTranslatedTitle((funding.getTitle() == null || funding.getTitle().getTranslatedTitle() == null) ? null : funding.getTitle().getTranslatedTitle().getContent());
        entity.setTranslatedTitleLanguageCode(
                (funding.getTitle() == null || funding.getTitle().getTranslatedTitle() == null) ? null : funding.getTitle().getTranslatedTitle().getLanguageCode());
        entity.setNumericAmount((funding.getAmount() == null || funding.getAmount().getContent() == null) ? null : BigDecimal.valueOf(Double.valueOf(funding.getAmount().getContent())));
        entity.setCurrencyCode((funding.getAmount() == null || funding.getAmount().getContent() == null) ? null : funding.getAmount().getCurrencyCode());
    }
}
