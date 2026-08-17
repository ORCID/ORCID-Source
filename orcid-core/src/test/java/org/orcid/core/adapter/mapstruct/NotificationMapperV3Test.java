package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.model.v3.release.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;

public class NotificationMapperV3Test {

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
}
