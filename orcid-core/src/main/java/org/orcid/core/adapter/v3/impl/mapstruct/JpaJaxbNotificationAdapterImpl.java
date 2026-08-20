package org.orcid.core.adapter.v3.impl.mapstruct;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationServiceAnnouncement;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationTip;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.model.v3.release.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAdministrativeEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.persistence.jpa.entities.NotificationServiceAnnouncementEntity;
import org.orcid.persistence.jpa.entities.NotificationTipEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV3.class, ExternalIdentifierTypeMapper.class}
)
public abstract class JpaJaxbNotificationAdapterImpl implements JpaJaxbNotificationAdapter {

    private static final String LAST_RESORT_IDENTITY_PROVIDER_NAME = "identity provider";

    @Autowired
    protected OrcidUrlManager orcidUrlManager;

    @Autowired
    protected IdentityProviderManager identityProviderManager;

    // Polymorphic Dispatchers
    @Override
    public NotificationEntity toNotificationEntity(Notification notification) {
        if (notification == null) return null;

        if (notification instanceof NotificationPermission) return map((NotificationPermission) notification);
        if (notification instanceof NotificationCustom) return map((NotificationCustom) notification);
        if (notification instanceof NotificationAmended) return map((NotificationAmended) notification);
        if (notification instanceof NotificationInstitutionalConnection) return map((NotificationInstitutionalConnection) notification);
        if (notification instanceof NotificationAdministrative) return map((NotificationAdministrative) notification);
        if (notification instanceof NotificationServiceAnnouncement) return map((NotificationServiceAnnouncement) notification);
        if (notification instanceof NotificationTip) return map((NotificationTip) notification);
        if (notification instanceof NotificationFindMyStuff) return map((NotificationFindMyStuff) notification);

        throw new IllegalArgumentException("Unknown Notification type: " + notification.getClass());
    }

    @Override
    public Notification toNotification(NotificationEntity entity) {
        if (entity == null) return null;

        if (entity instanceof NotificationAddItemsEntity) return map((NotificationAddItemsEntity) entity);
        if (entity instanceof NotificationCustomEntity) return map((NotificationCustomEntity) entity);
        if (entity instanceof NotificationAmendedEntity) return map((NotificationAmendedEntity) entity);
        if (entity instanceof NotificationInstitutionalConnectionEntity) return map((NotificationInstitutionalConnectionEntity) entity);
        if (entity instanceof NotificationAdministrativeEntity) return map((NotificationAdministrativeEntity) entity);
        if (entity instanceof NotificationServiceAnnouncementEntity) return map((NotificationServiceAnnouncementEntity) entity);
        if (entity instanceof NotificationTipEntity) return map((NotificationTipEntity) entity);
        if (entity instanceof NotificationFindMyStuffEntity) return map((NotificationFindMyStuffEntity) entity);

        throw new IllegalArgumentException("Unknown NotificationEntity type: " + entity.getClass());
    }

    @Override
    public List<Notification> toNotification(Collection<NotificationEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toNotification).collect(Collectors.toList());
    }

    // 1. Notification Custom 
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    protected abstract NotificationCustomEntity map(NotificationCustom n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    protected abstract NotificationCustom map(NotificationCustomEntity e);

    // 2. Notification Service Announcement
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    protected abstract NotificationServiceAnnouncementEntity map(NotificationServiceAnnouncement n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    protected abstract NotificationServiceAnnouncement map(NotificationServiceAnnouncementEntity e);

    // 3. Notification Tip
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    protected abstract NotificationTipEntity map(NotificationTip n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    protected abstract NotificationTip map(NotificationTipEntity e);

    // 4. Notification Administrative
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    protected abstract NotificationAdministrativeEntity map(NotificationAdministrative n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    protected abstract NotificationAdministrative map(NotificationAdministrativeEntity e);

    // 5. Notification Permission
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    @Mapping(source = "authorizationUrl.uri", target = "authorizationUrl")
    @Mapping(source = "items.items", target = "notificationItems")
    protected abstract NotificationAddItemsEntity map(NotificationPermission n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "authorizationUrl", target = "authorizationUrl.uri")
    @Mapping(source = "notificationItems", target = "items.items")
    protected abstract NotificationPermission map(NotificationAddItemsEntity e);

    @AfterMapping
    protected void afterMapPermission(NotificationPermission n, @MappingTarget NotificationAddItemsEntity entity) {
        if (StringUtils.isBlank(entity.getAuthorizationUrl()) && n.getAuthorizationUrl() != null) {
            String authUrl = orcidUrlManager.getBaseUrl() + n.getAuthorizationUrl().getPath();
            validateAndConvertToURI(authUrl);
            entity.setAuthorizationUrl(authUrl);
        }
    }

    @AfterMapping
    protected void afterMapPermissionEntity(NotificationAddItemsEntity entity, @MappingTarget NotificationPermission n) {
        AuthorizationUrl authUrl = n.getAuthorizationUrl();
        if (authUrl != null && authUrl.getUri() != null) {
            authUrl.setPath(extractFullPath(authUrl.getUri()));
            authUrl.setHost(orcidUrlManager.getBaseHost());
        }
    }

    // 6. Notification Institutional Connection   
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    @Mapping(source = "authorizationUrl.uri", target = "authorizationUrl")
    protected abstract NotificationInstitutionalConnectionEntity map(NotificationInstitutionalConnection n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "authorizationUrl", target = "authorizationUrl.uri")
    protected abstract NotificationInstitutionalConnection map(NotificationInstitutionalConnectionEntity e);

    @AfterMapping
    protected void afterMapInstitutionalConnection(NotificationInstitutionalConnection n, @MappingTarget NotificationInstitutionalConnectionEntity entity) {
        if (StringUtils.isBlank(entity.getAuthorizationUrl()) && n.getAuthorizationUrl() != null) {
            String authUrl = orcidUrlManager.getBaseUrl() + n.getAuthorizationUrl().getPath();
            validateAndConvertToURI(authUrl);
            entity.setAuthorizationUrl(authUrl);
        }
    }

    @AfterMapping
    protected void afterMapInstitutionalConnectionEntity(NotificationInstitutionalConnectionEntity entity, @MappingTarget NotificationInstitutionalConnection n) {
        AuthorizationUrl authUrl = n.getAuthorizationUrl();
        if (authUrl != null && authUrl.getUri() != null) {
            authUrl.setPath(extractFullPath(authUrl.getUri()));
            authUrl.setHost(orcidUrlManager.getBaseHost());
        }

        String providerId = entity.getAuthenticationProviderId();
        if (StringUtils.isNotBlank(providerId)) {
            String idpName = identityProviderManager.retrieveIdentitifyProviderName(providerId);
            n.setIdpName(idpName);
        } else {
            n.setIdpName(LAST_RESORT_IDENTITY_PROVIDER_NAME);
        }
    }

    // 7. Notification Amended
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    @Mapping(source = "items.items", target = "notificationItems")
    @Mapping(source = "amendedSection", target = "amendedSection")
    protected abstract NotificationAmendedEntity map(NotificationAmended n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "notificationItems", target = "items.items")
    @Mapping(source = "amendedSection", target = "amendedSection")
    protected abstract NotificationAmended map(NotificationAmendedEntity e);

    // 8. Notification Find My Stuff (V3 specific)
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "createdDate.value", target = "dateCreated")
    @Mapping(source = "authorizationUrl.uri", target = "authorizationUrl")
    protected abstract NotificationFindMyStuffEntity map(NotificationFindMyStuff n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "authorizationUrl", target = "authorizationUrl.uri")
    protected abstract NotificationFindMyStuff map(NotificationFindMyStuffEntity e);

    @AfterMapping
    protected void afterMapFindMyStuff(NotificationFindMyStuff n, @MappingTarget NotificationFindMyStuffEntity entity) {
        if (StringUtils.isBlank(entity.getAuthorizationUrl()) && n.getAuthorizationUrl() != null) {
            String authUrl = orcidUrlManager.getBaseUrl() + n.getAuthorizationUrl().getPath();
            validateAndConvertToURI(authUrl);
            entity.setAuthorizationUrl(authUrl);
        }
        entity.setAuthenticationProviderId(n.getServiceProviderId());
    }

    @AfterMapping
    protected void afterMapFindMyStuffEntity(NotificationFindMyStuffEntity entity, @MappingTarget NotificationFindMyStuff n) {
        AuthorizationUrl authUrl = n.getAuthorizationUrl();
        if (authUrl != null && authUrl.getUri() != null) {
            authUrl.setPath(extractFullPath(authUrl.getUri()));
            authUrl.setHost(orcidUrlManager.getBaseHost());
        }
        n.setServiceProviderId(entity.getAuthenticationProviderId());
    }

    // Custom AmendedSection Enum Converters
    protected String mapAmendedSection(org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection section) {
        if (section == null) return org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection.UNKNOWN.name();
        return section.name();
    }

    protected org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection mapAmendedSection(String section) {
        if (StringUtils.isBlank(section)) return org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection.UNKNOWN;
        try {
            return org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection.valueOf(section);
        } catch (IllegalArgumentException e) {
            return org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection.AFFILIATION;
        }
    }

    // Notification Items Mapping
    @Mapping(source = "externalIdentifier.type", target = "externalIdType")
    @Mapping(source = "externalIdentifier.value", target = "externalIdValue")
    @Mapping(source = "externalIdentifier.url.value", target = "externalIdUrl")
    @Mapping(source = "externalIdentifier.relationship", target = "externalIdRelationship")
    @Mapping(source = "additionalInfo", target = "additionalInfo")
    protected abstract NotificationItemEntity mapItem(Item item);

    @Mapping(source = "externalIdType", target = "externalIdentifier.type")
    @Mapping(source = "externalIdValue", target = "externalIdentifier.value")
    @Mapping(source = "externalIdUrl", target = "externalIdentifier.url.value")
    @Mapping(source = "externalIdRelationship", target = "externalIdentifier.relationship")
    @Mapping(source = "additionalInfo", target = "additionalInfo")
    protected abstract Item mapItem(NotificationItemEntity entity);

    @SuppressWarnings("rawtypes")
    protected String mapAdditionalInfo(Map map) {
        if (map == null || map.isEmpty()) return null;
        return JsonUtils.convertToJsonString(map);
    }

    @SuppressWarnings("rawtypes")
    protected Map mapAdditionalInfo(String json) {
        if (PojoUtil.isEmpty(json)) return null;
        return JsonUtils.readObjectFromJsonString(json, HashMap.class);
    }

    // URI Validation Utilities
    private URI validateAndConvertToURI(String uriString) {
        try {
            return new URI(uriString);
        } catch (Exception e) {
            throw new OrcidValidationException("Problem parsing uri", e);
        }
    }

    private String extractFullPath(String uriString) {
        URI uri = validateAndConvertToURI(uriString);
        StringBuilder pathBuilder = new StringBuilder(uri.getRawPath() != null ? uri.getRawPath() : "");
        String query = uri.getRawQuery();
        if (query != null) {
            pathBuilder.append('?').append(query);
        }
        String fragment = uri.getRawFragment();
        if (fragment != null) {
            pathBuilder.append('#').append(fragment);
        }
        return pathBuilder.toString();
    }
}