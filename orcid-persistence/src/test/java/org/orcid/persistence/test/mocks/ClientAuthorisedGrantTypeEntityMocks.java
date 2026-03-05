package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.keys.ClientAuthorisedGrantTypePk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ClientAuthorisedGrantTypeEntityMocks {

    private static final Map<ClientAuthorisedGrantTypePk, ClientAuthorisedGrantTypeEntity> MOCKS = new HashMap<>();

    static {
        try {
            ClientAuthorisedGrantTypeEntity e0 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e0);
            injectLastModified(new Date(), e0);
            ClientAuthorisedGrantTypePk pke0 = new ClientAuthorisedGrantTypePk("APP-5555555555555555", "authorization_code");
            MOCKS.put(pke0, e0);
            ClientAuthorisedGrantTypeEntity e1 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e1);
            injectLastModified(new Date(), e1);
            ClientAuthorisedGrantTypePk pke1 = new ClientAuthorisedGrantTypePk("APP-5555555555555555", "client_credentials");
            MOCKS.put(pke1, e1);
            ClientAuthorisedGrantTypeEntity e2 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e2);
            injectLastModified(new Date(), e2);
            ClientAuthorisedGrantTypePk pke2 = new ClientAuthorisedGrantTypePk("APP-5555555555555555", "refresh_token");
            MOCKS.put(pke2, e2);
            ClientAuthorisedGrantTypeEntity e3 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e3);
            injectLastModified(new Date(), e3);
            ClientAuthorisedGrantTypePk pke3 = new ClientAuthorisedGrantTypePk("APP-5555555555555556", "authorization_code");
            MOCKS.put(pke3, e3);
            ClientAuthorisedGrantTypeEntity e4 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e4);
            injectLastModified(new Date(), e4);
            ClientAuthorisedGrantTypePk pke4 = new ClientAuthorisedGrantTypePk("APP-5555555555555556", "client_credentials");
            MOCKS.put(pke4, e4);
            ClientAuthorisedGrantTypeEntity e5 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e5);
            injectLastModified(new Date(), e5);
            ClientAuthorisedGrantTypePk pke5 = new ClientAuthorisedGrantTypePk("APP-5555555555555556", "refresh_token");
            MOCKS.put(pke5, e5);
            ClientAuthorisedGrantTypeEntity e6 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e6);
            injectLastModified(new Date(), e6);
            ClientAuthorisedGrantTypePk pke6 = new ClientAuthorisedGrantTypePk("APP-5555555555555557", "authorization_code");
            MOCKS.put(pke6, e6);
            ClientAuthorisedGrantTypeEntity e7 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e7);
            injectLastModified(new Date(), e7);
            ClientAuthorisedGrantTypePk pke7 = new ClientAuthorisedGrantTypePk("APP-5555555555555557", "client_credentials");
            MOCKS.put(pke7, e7);
            ClientAuthorisedGrantTypeEntity e8 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e8);
            injectLastModified(new Date(), e8);
            ClientAuthorisedGrantTypePk pke8 = new ClientAuthorisedGrantTypePk("APP-5555555555555557", "refresh_token");
            MOCKS.put(pke8, e8);
            ClientAuthorisedGrantTypeEntity e9 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e9);
            injectLastModified(new Date(), e9);
            ClientAuthorisedGrantTypePk pke9 = new ClientAuthorisedGrantTypePk("APP-5555555555555558", "authorization_code");
            MOCKS.put(pke9, e9);
            ClientAuthorisedGrantTypeEntity e10 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e10);
            injectLastModified(new Date(), e10);
            ClientAuthorisedGrantTypePk pke10 = new ClientAuthorisedGrantTypePk("APP-5555555555555558", "client_credentials");
            MOCKS.put(pke10, e10);
            ClientAuthorisedGrantTypeEntity e11 = new ClientAuthorisedGrantTypeEntity();
            injectDateCreated(new Date(), e11);
            injectLastModified(new Date(), e11);
            ClientAuthorisedGrantTypePk pke11 = new ClientAuthorisedGrantTypePk("APP-5555555555555558", "refresh_token");
            MOCKS.put(pke11, e11);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientAuthorisedGrantTypeEntity getClientAuthorisedGrantType(ClientAuthorisedGrantTypePk id) {
        return MOCKS.get(id);
    }

    public static Map<ClientAuthorisedGrantTypePk, ClientAuthorisedGrantTypeEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }
}
