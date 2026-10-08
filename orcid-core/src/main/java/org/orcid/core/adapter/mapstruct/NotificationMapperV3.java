package org.orcid.core.adapter.mapstruct;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.Items;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.model.v3.release.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

@Mapper(componentModel = "spring", uses = { ExternalIdentifierTypeMapper.class, UrlMapperV3.class })
public interface NotificationMapperV3 {

    NotificationMapperV3 INSTANCE = Mappers.getMapper(NotificationMapperV3.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "externalIdentifier.type", target = "externalIdType", qualifiedByName = "apiToDb")
    @Mapping(source = "externalIdentifier.value", target = "externalIdValue")
    @Mapping(source = "externalIdentifier.url", target = "externalIdUrl")
    @Mapping(source = "externalIdentifier.relationship", target = "externalIdRelationship")
    @Mapping(source = "additionalInfo", target = "additionalInfo")
    NotificationItemEntity toNotificationItemEntity(Item item);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "externalIdType", target = "externalIdentifier.type", qualifiedByName = "dbToApi")
    @Mapping(source = "externalIdValue", target = "externalIdentifier.value")
    @Mapping(source = "externalIdUrl", target = "externalIdentifier.url")
    @Mapping(source = "externalIdRelationship", target = "externalIdentifier.relationship")
    @Mapping(source = "additionalInfo", target = "additionalInfo")
    Item toItem(NotificationItemEntity entity);

    @AfterMapping
    default void afterToItem(NotificationItemEntity entity, @MappingTarget Item item) {
        if (StringUtils.isBlank(entity.getExternalIdType()) && StringUtils.isBlank(entity.getExternalIdValue())
                && StringUtils.isBlank(entity.getExternalIdUrl()) && StringUtils.isBlank(entity.getExternalIdRelationship())) {
            item.setExternalIdentifier(null);
        }
    }

    @SuppressWarnings("rawtypes")
    default String mapAdditionalInfo(Map map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        return JsonUtils.convertToJsonString(map);
    }

    @SuppressWarnings("rawtypes")
    default Map mapAdditionalInfo(String json) {
        if (PojoUtil.isEmpty(json)) {
            return null;
        }
        return JsonUtils.readObjectFromJsonString(json, HashMap.class);
    }

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
        if (notification.getItems() == null) {
            notification.setItems(new Items());
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

    default void mapAmendedBtoA(NotificationAmendedEntity entity, NotificationAmended notification) {
        if (notification.getItems() == null) {
            notification.setItems(new Items());
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
