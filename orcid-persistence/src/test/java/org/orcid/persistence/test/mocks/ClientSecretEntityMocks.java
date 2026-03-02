package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ClientSecretEntityMocks {

    private static final Map<org.orcid.persistence.jpa.entities.ClientSecretPk, ClientSecretEntity> MOCKS = new HashMap<>();

    static {
        try {
            ClientSecretEntity e0 = new ClientSecretEntity();
            injectDateCreated(parseDate("2012-04-25"), e0);
            injectLastModified(parseDate("2012-04-25"), e0);
            org.orcid.persistence.jpa.entities.ClientSecretPk pke0 = new org.orcid.persistence.jpa.entities.ClientSecretPk("APP-5555555555555555", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
            e0.setId(pke0);
            e0.setPrimary(true);
            MOCKS.put(pke0, e0);
            ClientSecretEntity e1 = new ClientSecretEntity();
            injectDateCreated(parseDate("2012-04-25"), e1);
            injectLastModified(parseDate("2012-04-25"), e1);
            org.orcid.persistence.jpa.entities.ClientSecretPk pke1 = new org.orcid.persistence.jpa.entities.ClientSecretPk("APP-5555555555555556", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
            e1.setId(pke1);
            e1.setPrimary(true);
            MOCKS.put(pke1, e1);
            ClientSecretEntity e2 = new ClientSecretEntity();
            injectDateCreated(parseDate("2021-08-15"), e2);
            injectLastModified(parseDate("2021-08-15"), e2);
            org.orcid.persistence.jpa.entities.ClientSecretPk pke2 = new org.orcid.persistence.jpa.entities.ClientSecretPk("APP-5555555555555557", "B8M3Vy8aSvLeV+A4kx9HnQ==");
            e2.setId(pke2);
            e2.setPrimary(true);
            MOCKS.put(pke2, e2);
            ClientSecretEntity e3 = new ClientSecretEntity();
            injectDateCreated(parseDate("2021-08-15"), e3);
            injectLastModified(parseDate("2021-08-15"), e3);
            org.orcid.persistence.jpa.entities.ClientSecretPk pke3 = new org.orcid.persistence.jpa.entities.ClientSecretPk("APP-5555555555555557", "DaVVhgVl3ab+6HYDyGCBbg==");
            e3.setId(pke3);
            e3.setPrimary(false);
            MOCKS.put(pke3, e3);
            ClientSecretEntity e4 = new ClientSecretEntity();
            injectDateCreated(parseDate("2021-08-15"), e4);
            injectLastModified(parseDate("2021-08-15"), e4);
            org.orcid.persistence.jpa.entities.ClientSecretPk pke4 = new org.orcid.persistence.jpa.entities.ClientSecretPk("APP-5555555555555557", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
            e4.setId(pke4);
            e4.setPrimary(false);
            MOCKS.put(pke4, e4);
            ClientSecretEntity e5 = new ClientSecretEntity();
            injectDateCreated(parseDate("2021-08-15"), e5);
            injectLastModified(parseDate("2021-08-15"), e5);
            org.orcid.persistence.jpa.entities.ClientSecretPk pke5 = new org.orcid.persistence.jpa.entities.ClientSecretPk("APP-5555555555555558", "DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx");
            e5.setId(pke5);
            e5.setPrimary(true);
            MOCKS.put(pke5, e5);
            ClientSecretEntity e6 = new ClientSecretEntity();
            injectDateCreated(parseDate("2021-08-15"), e6);
            injectLastModified(parseDate("2021-08-15"), e6);
            org.orcid.persistence.jpa.entities.ClientSecretPk pke6 = new org.orcid.persistence.jpa.entities.ClientSecretPk("APP-5555555555555558", "DaVVhgVl3ab+6HYDyGCBbg==");
            e6.setId(pke6);
            e6.setPrimary(false);
            MOCKS.put(pke6, e6);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientSecretEntity getClientSecret(org.orcid.persistence.jpa.entities.ClientSecretPk id) {
        return MOCKS.get(id);
    }

    public static Map<org.orcid.persistence.jpa.entities.ClientSecretPk, ClientSecretEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

    private static Date parseDate(String dateStr) {
        try {
            if (dateStr.length() == 10) {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
