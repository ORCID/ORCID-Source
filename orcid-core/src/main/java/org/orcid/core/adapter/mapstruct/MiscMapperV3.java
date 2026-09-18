package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;

@Mapper
public interface MiscMapperV3 {

    MiscMapperV3 INSTANCE = Mappers.getMapper(MiscMapperV3.class);

    default void mapExternalIdentifierAtoB(PersonExternalIdentifier model, ExternalIdentifierEntity entity) {
        entity.setExternalIdUrl(model.getUrl() == null ? null : model.getUrl().getValue());
    }

    default void mapOrgBtoA(OrgEntity entity, Organization model) {
        if (entity.getRegion() != null && !entity.getRegion().isEmpty()) {
            if (model.getAddress() == null) {
                model.setAddress(new OrganizationAddress());
            }
            model.getAddress().setRegion(entity.getRegion());
        }
    }

    default void mapResearchResourceAtoB(ResearchResource model, ResearchResourceEntity entity) {
        entity.setTranslatedTitle((model.getProposal() == null || model.getProposal().getTitle() == null || model.getProposal().getTitle().getTranslatedTitle() == null) ? null
                : model.getProposal().getTitle().getTranslatedTitle().getContent());
        entity.setTranslatedTitleLanguageCode(
                (model.getProposal() == null || model.getProposal().getTitle() == null || model.getProposal().getTitle().getTranslatedTitle() == null) ? null
                        : model.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        entity.setUrl((model.getProposal() == null || model.getProposal().getUrl() == null) ? null : model.getProposal().getUrl().getValue());
    }

    default void mapAffiliationAtoB(Affiliation model, OrgAffiliationRelationEntity entity) {
        entity.setUrl(model.getUrl() == null ? null : model.getUrl().getValue());
    }
}
