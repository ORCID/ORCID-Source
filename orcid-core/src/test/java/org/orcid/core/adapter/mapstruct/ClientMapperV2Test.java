package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.ClientMapperV2;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

public class ClientMapperV2Test {

    @Test
    public void syncRedirectUrisFromClientShouldInitializeSetWhenMissing() {
        Client client = mock(Client.class);
        when(client.getClientRedirectUris()).thenReturn(null);

        ClientDetailsEntity entity = new ClientDetailsEntity();
        entity.setId("APP-1");

        ClientMapperV2.INSTANCE.syncRedirectUrisFromClient(client, entity);

        assertNotNull(entity.getClientRegisteredRedirectUris());
        assertTrue(entity.getClientRegisteredRedirectUris().isEmpty());
    }

    @Test
    public void populateClientFromEntityShouldDecryptPrimarySecret() {
        Client client = mock(Client.class);

        ClientDetailsEntity entity = new ClientDetailsEntity();
        SortedSet<ClientSecretEntity> secrets = new TreeSet<ClientSecretEntity>();
        secrets.add(new ClientSecretEntity("encrypted-primary", "APP-1", true));
        entity.setClientSecrets(secrets);

        EncryptionManager encryptionManager = mock(EncryptionManager.class);
        when(encryptionManager.decryptForInternalUse("encrypted-primary")).thenReturn("decrypted-primary");

        ClientMapperV2.INSTANCE.populateClientFromEntity(entity, client, encryptionManager);

        verify(client).setDecryptedSecret("decrypted-primary");
    }
}
