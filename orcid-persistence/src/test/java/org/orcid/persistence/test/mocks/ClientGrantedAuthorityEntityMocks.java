package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.keys.ClientGrantedAuthorityPk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ClientGrantedAuthorityEntityMocks {

    private static final Map<ClientGrantedAuthorityPk, ClientGrantedAuthorityEntity> MOCKS = new HashMap<>();

    static {
        try {
            ClientGrantedAuthorityEntity e0 = new ClientGrantedAuthorityEntity();
            injectDateCreated(new Date(), e0);
            injectLastModified(new Date(), e0);
            ClientGrantedAuthorityPk pke0 = new ClientGrantedAuthorityPk("APP-5555555555555555", "ROLE_CLIENT");
            MOCKS.put(pke0, e0);
            ClientGrantedAuthorityEntity e1 = new ClientGrantedAuthorityEntity();
            injectDateCreated(new Date(), e1);
            injectLastModified(new Date(), e1);
            ClientGrantedAuthorityPk pke1 = new ClientGrantedAuthorityPk("APP-5555555555555556", "ROLE_CLIENT");
            MOCKS.put(pke1, e1);
            ClientGrantedAuthorityEntity e2 = new ClientGrantedAuthorityEntity();
            injectDateCreated(new Date(), e2);
            injectLastModified(new Date(), e2);
            ClientGrantedAuthorityPk pke2 = new ClientGrantedAuthorityPk("APP-5555555555555557", "ROLE_CLIENT");
            MOCKS.put(pke2, e2);
            ClientGrantedAuthorityEntity e3 = new ClientGrantedAuthorityEntity();
            injectDateCreated(new Date(), e3);
            injectLastModified(new Date(), e3);
            ClientGrantedAuthorityPk pke3 = new ClientGrantedAuthorityPk("APP-5555555555555558", "ROLE_CLIENT");
            MOCKS.put(pke3, e3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientGrantedAuthorityEntity getClientGrantedAuthority(ClientGrantedAuthorityPk id) {
        return MOCKS.get(id);
    }

    public static Map<ClientGrantedAuthorityPk, ClientGrantedAuthorityEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }
}
