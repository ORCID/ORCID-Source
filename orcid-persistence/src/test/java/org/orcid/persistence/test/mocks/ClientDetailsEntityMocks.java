package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ClientDetailsEntityMocks {

    private static final Map<String, ClientDetailsEntity> MOCKS = new HashMap<>();

    static {
        try {
            ClientDetailsEntity e0 = new ClientDetailsEntity();
            injectDateCreated(parseDate("2012-04-25"), e0);
            injectLastModified(parseDate("2012-04-25"), e0);
            e0.setId("APP-5555555555555555");
            e0.setClientType("PREMIUM_CREATOR");
            e0.setClientName("Source Client 1");
            e0.setClientDescription("A test source client");
            e0.setClientWebsite("www.5555555555555555.com");
            e0.setGroupProfileId("5555-5555-5555-5558");
            e0.setPersistentTokensEnabled(true);
            e0.setAllowAutoDeprecate(true);
            e0.setUserOBOEnabled(false);
            MOCKS.put("APP-5555555555555555", e0);
            ClientDetailsEntity e1 = new ClientDetailsEntity();
            injectDateCreated(parseDate("2012-04-25"), e1);
            injectLastModified(parseDate("2012-04-25"), e1);
            e1.setId("APP-5555555555555556");
            e1.setClientType("PREMIUM_CREATOR");
            e1.setClientName("Source Client 2");
            e1.setClientDescription("A test source client");
            e1.setClientWebsite("www.5555555555555556.com");
            e1.setGroupProfileId("5555-5555-5555-5558");
            e1.setPersistentTokensEnabled(true);
            e1.setAllowAutoDeprecate(false);
            e1.setUserOBOEnabled(false);
            MOCKS.put("APP-5555555555555556", e1);
            ClientDetailsEntity e2 = new ClientDetailsEntity();
            injectDateCreated(parseDate("2012-04-25"), e2);
            injectLastModified(parseDate("2012-04-25"), e2);
            e2.setId("APP-5555555555555557");
            e2.setClientType("PREMIUM_CREATOR");
            e2.setClientName("Source Client 3");
            e2.setClientDescription("A test source client");
            e2.setClientWebsite("www.5555555555555557.com");
            e2.setGroupProfileId("5555-5555-5555-5558");
            e2.setPersistentTokensEnabled(true);
            e2.setAllowAutoDeprecate(true);
            e2.setUserOBOEnabled(false);
            MOCKS.put("APP-5555555555555557", e2);
            ClientDetailsEntity e3 = new ClientDetailsEntity();
            injectDateCreated(parseDate("2012-04-25"), e3);
            injectLastModified(parseDate("2012-04-25"), e3);
            e3.setId("APP-5555555555555558");
            e3.setClientType("PREMIUM_CREATOR");
            e3.setClientName("Source Client 4");
            e3.setClientDescription("A test source client");
            e3.setClientWebsite("www.5555555555555558.com");
            e3.setGroupProfileId("5555-5555-5555-5558");
            e3.setPersistentTokensEnabled(true);
            e3.setAllowAutoDeprecate(true);
            e3.setUserOBOEnabled(true);
            MOCKS.put("APP-5555555555555558", e3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientDetailsEntity getClientDetails(String id) {
        return MOCKS.get(id);
    }

    public static Map<String, ClientDetailsEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }
}
