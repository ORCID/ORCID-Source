package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.permission_v2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;

@Mapper
public interface NotificationMapperV2 {

    NotificationMapperV2 INSTANCE = Mappers.getMapper(NotificationMapperV2.class);

    default String buildAuthorizationUrlIfBlank(String existingUrl, String path, OrcidUrlManager orcidUrlManager) {
        if (StringUtils.isBlank(existingUrl)) {
            return orcidUrlManager.getBaseUrl() + path;
        }
        return existingUrl;
    }

    default void mapPermissionBtoA(NotificationAddItemsEntity entity, NotificationPermission notification, String fullPath, String baseHost) {
        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
        if (authUrl != null) {
            authUrl.setPath(fullPath);
            authUrl.setHost(baseHost);
        }
    }

    default void mapInstitutionalBtoA(NotificationInstitutionalConnectionEntity entity, NotificationInstitutionalConnection notification, String fullPath,
            String baseHost, IdentityProviderManager identityProviderManager, String lastResortName) {
        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
        if (authUrl != null) {
            authUrl.setPath(fullPath);
            authUrl.setHost(baseHost);
        }
        String providerId = entity.getAuthenticationProviderId();
        if (StringUtils.isNotBlank(providerId)) {
            String idpName = identityProviderManager.retrieveIdentitifyProviderName(providerId);
            notification.setIdpName(idpName);
        } else {
            notification.setIdpName(lastResortName);
        }
    }

    default void mapItemAtoB(NotificationItemEntity entity, Item item, AdditionalInfoJsonMapper additionalInfoJsonMapper) {
        item.setAdditionalInfo(additionalInfoJsonMapper.fromJson(entity.getAdditionalInfo()));
    }

    default void mapItemBtoA(Item item, NotificationItemEntity entity, AdditionalInfoJsonMapper additionalInfoJsonMapper) {
        if (item.getAdditionalInfo() != null) {
            entity.setAdditionalInfo(additionalInfoJsonMapper.toJson(item.getAdditionalInfo()));
        }
    }

    default void mapAmendedAtoB(NotificationAmended model, NotificationAmendedEntity entity) {
        if (model.getAmendedSection() == null) {
            return;
        }

        switch (model.getAmendedSection()) {
        case AFFILIATION:
            entity.setAmendedSection(AmendedSection.AFFILIATION.name());
            break;
        case BIO:
            entity.setAmendedSection(AmendedSection.BIO.name());
            break;
        case EDUCATION:
            entity.setAmendedSection(AmendedSection.EDUCATION.name());
            break;
        case EMPLOYMENT:
            entity.setAmendedSection(AmendedSection.EMPLOYMENT.name());
            break;
        case EXTERNAL_IDENTIFIERS:
            entity.setAmendedSection(AmendedSection.EXTERNAL_IDENTIFIERS.name());
            break;
        case FUNDING:
            entity.setAmendedSection(AmendedSection.FUNDING.name());
            break;
        case PEER_REVIEW:
            entity.setAmendedSection(AmendedSection.PEER_REVIEW.name());
            break;
        case PREFERENCES:
            entity.setAmendedSection(AmendedSection.PREFERENCES.name());
            break;
        case UNKNOWN:
            entity.setAmendedSection(AmendedSection.UNKNOWN.name());
            break;
        case RESEARCH_RESOURCE:
            entity.setAmendedSection(AmendedSection.RESEARCH_RESOURCE.name());
            break;
        case WORK:
            entity.setAmendedSection(AmendedSection.WORK.name());
            break;
        default:
            entity.setAmendedSection(AmendedSection.UNKNOWN.name());
            break;
        }
    }

    default void mapAmendedBtoA(NotificationAmendedEntity entity, NotificationAmended model) {
        if (entity.getAmendedSection() == null) {
            return;
        }

        if (AmendedSection.AFFILIATION.name().equals(entity.getAmendedSection()) || AmendedSection.DISTINCTION.name().equals(entity.getAmendedSection())
                || AmendedSection.INVITED_POSITION.name().equals(entity.getAmendedSection()) || AmendedSection.MEMBERSHIP.name().equals(entity.getAmendedSection())
                || AmendedSection.QUALIFICATION.name().equals(entity.getAmendedSection()) || AmendedSection.SERVICE.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.AFFILIATION);
        } else if (AmendedSection.BIO.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.BIO);
        } else if (AmendedSection.EDUCATION.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.EDUCATION);
        } else if (AmendedSection.EMPLOYMENT.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.EMPLOYMENT);
        } else if (AmendedSection.EXTERNAL_IDENTIFIERS.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.EXTERNAL_IDENTIFIERS);
        } else if (AmendedSection.FUNDING.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.FUNDING);
        } else if (AmendedSection.PEER_REVIEW.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.PEER_REVIEW);
        } else if (AmendedSection.PREFERENCES.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.PREFERENCES);
        } else if (AmendedSection.UNKNOWN.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.UNKNOWN);
        } else if (AmendedSection.RESEARCH_RESOURCE.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.RESEARCH_RESOURCE);
        } else if (AmendedSection.WORK.name().equals(entity.getAmendedSection())) {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.WORK);
        } else {
            model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.UNKNOWN);
        }
    }
}
