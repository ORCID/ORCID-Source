package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.AdditionalInfoJsonMapper;
import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;
import org.orcid.core.adapter.mapstruct.NotificationMapperV2;
import org.orcid.core.adapter.mapstruct.UrlMapperV2;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;

@RunWith(MockitoJUnitRunner.class)
public class NotificationMapperV2Test {

    @Spy
    private final ExternalIdentifierTypeMapper externalIdentifierTypeMapper = Mappers.getMapper(ExternalIdentifierTypeMapper.class);

    @Spy
    private final UrlMapperV2 urlMapperV2 = Mappers.getMapper(UrlMapperV2.class);

    @InjectMocks
    private final NotificationMapperV2 mapper = Mappers.getMapper(NotificationMapperV2.class);

    @Test
    public void buildAuthorizationUrlIfBlankShouldBuildFromBaseUrlAndPath() {
        OrcidUrlManager orcidUrlManager = mock(OrcidUrlManager.class);
        when(orcidUrlManager.getBaseUrl()).thenReturn("https://orcid.org");

        String value = NotificationMapperV2.INSTANCE.buildAuthorizationUrlIfBlank("", "/oauth/authorize", orcidUrlManager);

        assertEquals("https://orcid.org/oauth/authorize", value);
    }

    @Test
    public void mapAmendedAtoBShouldMapKnownSection() {
        NotificationAmended model = new NotificationAmended();
        model.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.WORK);

        NotificationAmendedEntity entity = new NotificationAmendedEntity();
        NotificationMapperV2.INSTANCE.mapAmendedAtoB(model, entity);

        assertEquals("WORK", entity.getAmendedSection());
    }

    @Test
    public void mapAmendedBtoAShouldMapNewAffiliationTypesToAffiliation() {
        NotificationAmendedEntity entity = new NotificationAmendedEntity();
        entity.setAmendedSection("DISTINCTION");

        NotificationAmended model = new NotificationAmended();
        NotificationMapperV2.INSTANCE.mapAmendedBtoA(entity, model);

        assertNotNull(model.getAmendedSection());
        assertEquals(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.AFFILIATION, model.getAmendedSection());
    }

    @Test
    public void mapItemAtoBShouldDeserializeAdditionalInfo() {
        NotificationItemEntity entity = new NotificationItemEntity();
        entity.setAdditionalInfo("{\"k\":\"v\"}");

        Item item = new Item();
        NotificationMapperV2.INSTANCE.mapItemAtoB(entity, item, AdditionalInfoJsonMapper.INSTANCE);

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
        NotificationMapperV2.INSTANCE.mapItemBtoA(item, entity, AdditionalInfoJsonMapper.INSTANCE);

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
        org.orcid.jaxb.model.common_v2.Url url = new org.orcid.jaxb.model.common_v2.Url();
        url.setValue("https://doi.org/10.1234/test");
        extId.setUrl(url);
        extId.setRelationship(Relationship.SELF);
        item.setExternalIdentifier(extId);

        NotificationItemEntity entity = mapper.toNotificationItemEntity(item);
        assertNotNull(entity);
        assertEquals(Long.valueOf(123L), entity.getId());
        assertEquals("WORK", entity.getItemType());
        assertEquals("Work Name", entity.getItemName());
        assertEquals("DOI", entity.getExternalIdType());
        assertEquals("10.1234/test", entity.getExternalIdValue());
        assertEquals("https://doi.org/10.1234/test", entity.getExternalIdUrl());
        assertEquals("SELF", entity.getExternalIdRelationship());

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

        NotificationMapperV2.INSTANCE.mapPermissionBtoA(entity, notification, "/path", "orcid.org");
        assertNotNull(notification.getItems());
        assertNotNull(notification.getItems().getItems());
    }

    @Test
    public void testMapAmendedBtoAInitializesItems() {
        NotificationAmended notification = new NotificationAmended();
        NotificationAmendedEntity entity = new NotificationAmendedEntity();

        NotificationMapperV2.INSTANCE.mapAmendedBtoA(entity, notification);
        assertNotNull(notification.getItems());
        assertNotNull(notification.getItems().getItems());
    }
}
