package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.ClientMapperV3;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

public class ClientMapperV3Test {

    private static final String IETF_EXCHANGE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";

    @Test
    public void syncRedirectUrisFromClientShouldInitializeSetWhenMissing() {
        Client client = mock(Client.class);
        when(client.getClientRedirectUris()).thenReturn(null);

        ClientDetailsEntity entity = new ClientDetailsEntity();
        entity.setId("APP-1");

        ClientMapperV3.INSTANCE.syncRedirectUrisFromClient(client, entity);

        assertNotNull(entity.getClientRegisteredRedirectUris());
        assertTrue(entity.getClientRegisteredRedirectUris().isEmpty());
    }

    @Test
    public void populateClientFromEntityShouldSetOboEnabledAndDecryptSecret() {
        Client client = mock(Client.class);

        ClientDetailsEntity entity = new ClientDetailsEntity();

        SortedSet<ClientSecretEntity> secrets = new TreeSet<ClientSecretEntity>();
        secrets.add(new ClientSecretEntity("encrypted-primary", "APP-1", true));
        entity.setClientSecrets(secrets);

        Set<ClientAuthorisedGrantTypeEntity> grantTypes = new HashSet<ClientAuthorisedGrantTypeEntity>();
        ClientAuthorisedGrantTypeEntity grantType = new ClientAuthorisedGrantTypeEntity();
        grantType.setClientId("APP-1");
        grantType.setGrantType(IETF_EXCHANGE_GRANT_TYPE);
        grantTypes.add(grantType);
        entity.setClientAuthorizedGrantTypes(grantTypes);

        EncryptionManager encryptionManager = mock(EncryptionManager.class);
        when(encryptionManager.decryptForInternalUse("encrypted-primary")).thenReturn("decrypted-primary");

        ClientMapperV3.INSTANCE.populateClientFromEntity(entity, client, encryptionManager);

        verify(client).setDecryptedSecret("decrypted-primary");
        verify(client).setOboEnabled(true);
    }
}
