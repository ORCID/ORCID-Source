package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ClientRedirectUriEntityMocks {

    private static final Map<ClientRedirectUriPk, ClientRedirectUriEntity> MOCKS = new HashMap<>();

    static {
        try {
            ClientRedirectUriEntity e0 = new ClientRedirectUriEntity();
            injectDateCreated(new Date(), e0);
            injectLastModified(new Date(), e0);
            ClientRedirectUriPk pke0 = new ClientRedirectUriPk("APP-5555555555555555", "http://www.APP-5555555555555555.com/redirect/oauth", "default");
            e0.setPredefinedClientScope("None");
            MOCKS.put(pke0, e0);
            ClientRedirectUriEntity e1 = new ClientRedirectUriEntity();
            injectDateCreated(new Date(), e1);
            injectLastModified(new Date(), e1);
            ClientRedirectUriPk pke1 = new ClientRedirectUriPk("APP-5555555555555555", "http://www.APP-5555555555555555.com/redirect/oauth/institutional_sign_in", "institutional-sign-in");
            e1.setPredefinedClientScope("/authenticate");
            MOCKS.put(pke1, e1);
            ClientRedirectUriEntity e2 = new ClientRedirectUriEntity();
            injectDateCreated(new Date(), e2);
            injectLastModified(new Date(), e2);
            ClientRedirectUriPk pke2 = new ClientRedirectUriPk("APP-5555555555555557", "http://www.APP-5555555555555557.com/redirect/oauth", "default");
            e2.setPredefinedClientScope("None");
            MOCKS.put(pke2, e2);
            ClientRedirectUriEntity e3 = new ClientRedirectUriEntity();
            injectDateCreated(new Date(), e3);
            injectLastModified(new Date(), e3);
            ClientRedirectUriPk pke3 = new ClientRedirectUriPk("APP-5555555555555558", "http://www.APP-5555555555555558.com/redirect/oauth", "default");
            e3.setPredefinedClientScope("None");
            MOCKS.put(pke3, e3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientRedirectUriEntity getClientRedirectUri(ClientRedirectUriPk id) {
        return MOCKS.get(id);
    }

    public static Map<ClientRedirectUriPk, ClientRedirectUriEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

}
