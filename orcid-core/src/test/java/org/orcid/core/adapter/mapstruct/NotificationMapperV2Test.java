package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;

public class NotificationMapperV2Test {

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
}
