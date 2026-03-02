package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ClientResourceIdEntity;
import org.orcid.persistence.jpa.entities.keys.ClientResourceIdPk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ClientResourceIdEntityMocks {

    private static final Map<ClientResourceIdPk, ClientResourceIdEntity> MOCKS = new HashMap<>();

    static {
        try {
            ClientResourceIdEntity e0 = new ClientResourceIdEntity();
            injectDateCreated(new Date(), e0);
            injectLastModified(new Date(), e0);
            ClientResourceIdPk pke0 = new ClientResourceIdPk("APP-5555555555555555", "orcid");
            MOCKS.put(pke0, e0);
            ClientResourceIdEntity e1 = new ClientResourceIdEntity();
            injectDateCreated(new Date(), e1);
            injectLastModified(new Date(), e1);
            ClientResourceIdPk pke1 = new ClientResourceIdPk("APP-5555555555555556", "orcid");
            MOCKS.put(pke1, e1);
            ClientResourceIdEntity e2 = new ClientResourceIdEntity();
            injectDateCreated(new Date(), e2);
            injectLastModified(new Date(), e2);
            ClientResourceIdPk pke2 = new ClientResourceIdPk("APP-5555555555555557", "orcid");
            MOCKS.put(pke2, e2);
            ClientResourceIdEntity e3 = new ClientResourceIdEntity();
            injectDateCreated(new Date(), e3);
            injectLastModified(new Date(), e3);
            ClientResourceIdPk pke3 = new ClientResourceIdPk("APP-5555555555555558", "orcid");
            MOCKS.put(pke3, e3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientResourceIdEntity getClientResourceId(ClientResourceIdPk id) {
        return MOCKS.get(id);
    }

    public static Map<ClientResourceIdPk, ClientResourceIdEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

}
