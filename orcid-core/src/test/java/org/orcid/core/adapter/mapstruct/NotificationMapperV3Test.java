package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.AdditionalInfoJsonMapper;
import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;
import org.orcid.core.adapter.mapstruct.NotificationMapperV3;
import org.orcid.core.adapter.mapstruct.UrlMapperV3;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.model.v3.release.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;

@RunWith(MockitoJUnitRunner.class)
public class NotificationMapperV3Test {

    @Spy
    private final ExternalIdentifierTypeMapper externalIdentifierTypeMapper = Mappers.getMapper(ExternalIdentifierTypeMapper.class);

    @Spy
    private final UrlMapperV3 urlMapperV3 = Mappers.getMapper(UrlMapperV3.class);

    @InjectMocks
    private final NotificationMapperV3 mapper = Mappers.getMapper(NotificationMapperV3.class);

    @Test
    public void buildAuthorizationUrlIfBlankShouldBuildFromBaseUrlAndPath() {
        String value = NotificationMapperV3.INSTANCE.buildAuthorizationUrlIfBlank("", "/oauth/authorize", "https://orcid.org");

        assertEquals("https://orcid.org/oauth/authorize", value);
    }

    @Test
    public void mapFindMyStuffAtoBShouldSetAuthUrlAndProviderWhenBlank() {
        NotificationFindMyStuff model = new NotificationFindMyStuff();
        model.setServiceProviderId("provider-1");

        NotificationFindMyStuffEntity entity = new NotificationFindMyStuffEntity();
        NotificationMapperV3.INSTANCE.mapFindMyStuffAtoB(model, entity, "", "https://orcid.org/oauth/authorize");

        assertEquals("https://orcid.org/oauth/authorize", entity.getAuthorizationUrl());
        assertEquals("provider-1", entity.getAuthenticationProviderId());
    }

    @Test
    public void mapFindMyStuffAtoBShouldNotOverwriteWhenExistingUrlPresent() {
        NotificationFindMyStuff model = new NotificationFindMyStuff();
        model.setServiceProviderId("provider-2");

        NotificationFindMyStuffEntity entity = new NotificationFindMyStuffEntity();
        entity.setAuthorizationUrl("existing");
        NotificationMapperV3.INSTANCE.mapFindMyStuffAtoB(model, entity, entity.getAuthorizationUrl(), "ignored");

        assertEquals("existing", entity.getAuthorizationUrl());
    }

    @Test
    public void mapItemAtoBShouldDeserializeAdditionalInfo() {
        NotificationItemEntity entity = new NotificationItemEntity();
        entity.setAdditionalInfo("{\"k\":\"v\"}");

        Item item = new Item();
        NotificationMapperV3.INSTANCE.mapItemAtoB(entity, item, AdditionalInfoJsonMapper.INSTANCE);

        assertNotNull(item.getAdditionalInfo());
        assertEquals("v", item.getAdditionalInfo().get("k"));
    }

    @Test
    public void mapItemBtoAShouldSerializeAdditionalInfo() {
        Item item = new Item();
        Map<String, Object> additionalInfo = new HashMap<String, Object>();
        additionalInfo.put("k", "v");
        item.setAdditionalInfo(additionalInfo);

        NotificationItemEntity entity = new NotificationItemEntity();
        NotificationMapperV3.INSTANCE.mapItemBtoA(item, entity, AdditionalInfoJsonMapper.INSTANCE);

        assertNotNull(entity.getAdditionalInfo());
        assertEquals("v", AdditionalInfoJsonMapper.INSTANCE.fromJson(entity.getAdditionalInfo()).get("k"));
    }

    @Test
    public void testToItemAndToNotificationItemEntity() {
        Item item = new Item();
        item.setPutCode("123");
        item.setItemName("Work Name");
        item.setItemType(ItemType.WORK);
        ExternalID extId = new ExternalID();
        extId.setType("doi");
        extId.setValue("10.1234/test");
        Url url = new Url();
        url.setValue("https://doi.org/10.1234/test");
        extId.setUrl(url);
        extId.setRelationship(Relationship.SELF);
        item.setExternalIdentifier(extId);

        NotificationItemEntity entity = mapper.toNotificationItemEntity(item);
        assertNotNull(entity);
        org.junit.Assert.assertNull(entity.getId());
        assertEquals("WORK", entity.getItemType());
        assertEquals("Work Name", entity.getItemName());
        assertEquals("DOI", entity.getExternalIdType());
        assertEquals("10.1234/test", entity.getExternalIdValue());
        assertEquals("https://doi.org/10.1234/test", entity.getExternalIdUrl());
        assertEquals("SELF", entity.getExternalIdRelationship());

        entity.setId(123L);
        Item mappedBack = mapper.toItem(entity);
        assertNotNull(mappedBack);
        assertEquals("123", mappedBack.getPutCode());
        assertEquals("Work Name", mappedBack.getItemName());
        assertEquals(ItemType.WORK, mappedBack.getItemType());
        assertNotNull(mappedBack.getExternalIdentifier());
        assertEquals("doi", mappedBack.getExternalIdentifier().getType());
        assertEquals("10.1234/test", mappedBack.getExternalIdentifier().getValue());
        assertEquals("https://doi.org/10.1234/test", mappedBack.getExternalIdentifier().getUrl().getValue());
        assertEquals(Relationship.SELF, mappedBack.getExternalIdentifier().getRelationship());
    }

    @Test
    public void testMapPermissionBtoAInitializesItems() {
        NotificationPermission notification = new NotificationPermission();
        NotificationAddItemsEntity entity = new NotificationAddItemsEntity();

        NotificationMapperV3.INSTANCE.mapPermissionBtoA(entity, notification, "/path", "orcid.org");
        assertNotNull(notification.getItems());
        assertNotNull(notification.getItems().getItems());
    }

    @Test
    public void testMapAmendedBtoAInitializesItems() {
        NotificationAmended notification = new NotificationAmended();
        NotificationAmendedEntity entity = new NotificationAmendedEntity();

        NotificationMapperV3.INSTANCE.mapAmendedBtoA(entity, notification);
        assertNotNull(notification.getItems());
        assertNotNull(notification.getItems().getItems());
    }
}
