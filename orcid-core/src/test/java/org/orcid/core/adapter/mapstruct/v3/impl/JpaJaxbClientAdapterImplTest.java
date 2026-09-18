package org.orcid.core.adapter.mapstruct.v3.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

/**
 * J21-008. `userOBOEnabled` is a configuration field that only ORCID staff are meant to set, and
 * `ClientManagerImpl.create` owns it behind an `addConfigValues` guard that only the staff path
 * reaches. The mapper must therefore never carry it in from the request, or the guard is
 * bypassed by anyone who can create a client.
 *
 * MapStruct maps target properties by name unless told not to, so the protection here is a
 * single `@Mapping(target = "userOBOEnabled", ignore = true)` on the creation overload. The
 * update overload has always had one; the creation overload did not, and that is the defect.
 *
 * No Spring and no database: the generated mapper is instantiated directly. Its one
 * `@Autowired` collaborator, the encryption manager, is used on the entity-to-model path only,
 * and the creation path's `@AfterMapping` hook tolerates a client with no redirect URIs.
 */
public class JpaJaxbClientAdapterImplTest {

    private final JpaJaxbClientAdapterImplImpl adapter = new JpaJaxbClientAdapterImplImpl();

    private Client clientAskingForObo() {
        Client client = new Client();
        client.setName("J21-008 probe");
        client.setDescription("mass assignment probe for user_obo_enabled");
        client.setWebsite("https://example.org/obo-probe");
        client.setUserOBOEnabled(true);
        return client;
    }

    @Test
    public void testCreateDoesNotTakeUserOboEnabledFromTheRequest() {
        ClientDetailsEntity entity = adapter.toEntity(clientAskingForObo());

        assertFalse(
                "a create request asked for the staff-only OBO flag and the mapper honoured it; "
                        + "ClientManagerImpl.create can no longer keep it behind addConfigValues",
                entity.isUserOBOEnabled());
    }

    @Test
    public void testUpdateDoesNotTakeUserOboEnabledFromTheRequest() {
        ClientDetailsEntity existing = new ClientDetailsEntity();
        existing.setUserOBOEnabled(false);

        ClientDetailsEntity entity = adapter.toEntity(clientAskingForObo(), existing);

        assertFalse("the update overload must keep ignoring the flag too", entity.isUserOBOEnabled());
    }

    @Test
    public void testReadStillReportsTheFlag() {
        // The ignore is one-directional on purpose. Reads have to keep showing the flag, or the
        // admin screens that display it go blank and the fix trades one defect for another.
        ClientDetailsEntity entity = new ClientDetailsEntity();
        entity.setUserOBOEnabled(true);

        Client client = adapter.toClient(entity);

        assertTrue("entity to model must still carry the flag", client.isUserOBOEnabled());
    }
}
