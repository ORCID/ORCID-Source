package org.orcid.core.adapter.mapstruct.impl;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.JpaJaxbNotificationAdapter;
import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_v2.NotificationAdministrative;
import org.orcid.jaxb.model.notification.custom_v2.NotificationCustom;
import org.orcid.jaxb.model.notification.custom_v2.NotificationServiceAnnouncement;
import org.orcid.jaxb.model.notification.custom_v2.NotificationTip;
import org.orcid.jaxb.model.notification.permission_v2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.persistence.jpa.entities.*;

/**
 * Handles polymorphic routing for the 7 different Notification types.
 */
@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV2.class, ExternalIdentifierTypeMapper.class}
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
        
        throw new IllegalArgumentException("Unknown NotificationEntity type: " + entity.getClass());
    }

    @Override
    public List<Notification> toNotification(Collection<NotificationEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toNotification).collect(Collectors.toList());
    }

    // 1. Notification Custom 
    @Mapping(source = "putCode", target = "id")
    @Mapping(target = "dateCreated", ignore = true)
    protected abstract NotificationCustomEntity map(NotificationCustom n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate")
    protected abstract NotificationCustom map(NotificationCustomEntity e);

    // 2. Notification Service Announcement
    @Mapping(source = "putCode", target = "id")
    @Mapping(target = "dateCreated", ignore = true)
    protected abstract NotificationServiceAnnouncementEntity map(NotificationServiceAnnouncement n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate")
    protected abstract NotificationServiceAnnouncement map(NotificationServiceAnnouncementEntity e);

    // 3. Notification Tip
    @Mapping(source = "putCode", target = "id")
    @Mapping(target = "dateCreated", ignore = true)
    protected abstract NotificationTipEntity map(NotificationTip n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate")
    protected abstract NotificationTip map(NotificationTipEntity e);

    // 4. Notification Administrative
    @Mapping(source = "putCode", target = "id")
    @Mapping(target = "dateCreated", ignore = true)
    protected abstract NotificationAdministrativeEntity map(NotificationAdministrative n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate")
    protected abstract NotificationAdministrative map(NotificationAdministrativeEntity e);

    // 5. Notification Permission
    @Mapping(source = "putCode", target = "id")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(source = "authorizationUrl.uri", target = "authorizationUrl")
    @Mapping(source = "items.items", target = "notificationItems")
    protected abstract NotificationAddItemsEntity map(NotificationPermission n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate")
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
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(source = "authorizationUrl.uri", target = "authorizationUrl")
    protected abstract NotificationInstitutionalConnectionEntity map(NotificationInstitutionalConnection n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate")
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
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(source = "items.items", target = "notificationItems")
    @Mapping(source = "amendedSection", target = "amendedSection")
    protected abstract NotificationAmendedEntity map(NotificationAmended n);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate")
    @Mapping(source = "notificationItems", target = "items.items")
    @Mapping(source = "amendedSection", target = "amendedSection")
    protected abstract NotificationAmended map(NotificationAmendedEntity e);

    // Custom AmendedSection Enum Converters
    protected String mapAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection section) {
        if (section == null) return org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection.UNKNOWN.name();
        return section.name();
    }

    protected org.orcid.jaxb.model.notification.amended_v2.AmendedSection mapAmendedSection(String section) {
        if (StringUtils.isBlank(section)) return org.orcid.jaxb.model.notification.amended_v2.AmendedSection.UNKNOWN;
        try {
            return org.orcid.jaxb.model.notification.amended_v2.AmendedSection.valueOf(section);
        } catch (IllegalArgumentException e) {
            return org.orcid.jaxb.model.notification.amended_v2.AmendedSection.AFFILIATION;
        }
    }

    // Notification Items Mapping
    @Mapping(source = "externalIdentifier.type", target = "externalIdType")
    @Mapping(source = "externalIdentifier.value", target = "externalIdValue")
    @Mapping(source = "additionalInfo", target = "additionalInfo")
    protected abstract NotificationItemEntity mapItem(Item item);

    @Mapping(source = "externalIdType", target = "externalIdentifier.type")
    @Mapping(source = "externalIdValue", target = "externalIdentifier.value")
    @Mapping(source = "additionalInfo", target = "additionalInfo")
    protected abstract Item mapItem(NotificationItemEntity entity);

    @SuppressWarnings("rawtypes")
    protected String mapAdditionalInfo(Map map) {
        if (map == null || map.isEmpty()) return null;
        return JsonUtils.convertToJsonString(map);
    }

    @SuppressWarnings("rawtypes")
    protected Map mapAdditionalInfo(String json) {
        if (StringUtils.isBlank(json)) return null;
        return JsonUtils.readObjectFromJsonString(json, HashMap.class);
    }

    // XMLGregorianCalendar <-> java.util.Date Converters
    protected Date xmlGregorianCalendarToDate(XMLGregorianCalendar cal) {
        if (cal == null) {
            return null;
        }
        return cal.toGregorianCalendar().getTime();
    }

    protected XMLGregorianCalendar dateToXmlGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Failed to convert Date to XMLGregorianCalendar", e);
        }
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
        StringBuilder pathBuilder = new StringBuilder(uri.getRawPath());
        String query = uri.getRawQuery();
        if (query != null) {
            pathBuilder.append('?').append(query);
        }
        String fragment = uri.getRawFragment();
        if (fragment != null) {
            pathBuilder.append(fragment);
        }
        return pathBuilder.toString();
    }
}