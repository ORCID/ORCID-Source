package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.model.v3.release.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;

@Mapper
public interface NotificationMapperV3 {

    NotificationMapperV3 INSTANCE = Mappers.getMapper(NotificationMapperV3.class);

    default String buildAuthorizationUrlIfBlank(String existingUrl, String path, String baseUrl) {
        if (StringUtils.isBlank(existingUrl)) {
            return baseUrl + path;
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

    default void mapFindMyStuffAtoB(NotificationFindMyStuff notification, NotificationFindMyStuffEntity entity, String existingAuthorizationUrl,
            String builtAuthorizationUrl) {
        if (StringUtils.isBlank(existingAuthorizationUrl)) {
            entity.setAuthorizationUrl(builtAuthorizationUrl);
            entity.setAuthenticationProviderId(notification.getServiceProviderId());
        }
    }

    default void mapFindMyStuffBtoA(NotificationFindMyStuffEntity entity, NotificationFindMyStuff notification, String fullPath, String baseHost) {
        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
        if (authUrl != null) {
            authUrl.setPath(fullPath);
            authUrl.setHost(baseHost);
        }
        notification.setServiceProviderId(entity.getAuthenticationProviderId());
    }

    default void mapItemAtoB(NotificationItemEntity entity, Item item, AdditionalInfoJsonMapper additionalInfoJsonMapper) {
        item.setAdditionalInfo(additionalInfoJsonMapper.fromJson(entity.getAdditionalInfo()));
    }

    default void mapItemBtoA(Item item, NotificationItemEntity entity, AdditionalInfoJsonMapper additionalInfoJsonMapper) {
        if (item.getAdditionalInfo() != null) {
            entity.setAdditionalInfo(additionalInfoJsonMapper.toJson(item.getAdditionalInfo()));
        }
    }
}
