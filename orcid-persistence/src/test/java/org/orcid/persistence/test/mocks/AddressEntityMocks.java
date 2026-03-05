package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.AddressEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class AddressEntityMocks {

    private static final Map<Long, AddressEntity> MOCKS = new HashMap<>();

    static {
        try {
            AddressEntity a1 = new AddressEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), a1);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), a1);
            a1.setId(1L);
            a1.setIso2Country("US");
            a1.setVisibility("PUBLIC");
            a1.setOrcid("4444-4444-4444-4442");
            a1.setDisplayIndex(0L);
            a1.setClientSourceId("APP-5555555555555555");
            MOCKS.put(1L, a1);

            AddressEntity a2 = new AddressEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), a2);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), a2);
            a2.setId(2L);
            a2.setIso2Country("US");
            a2.setVisibility("PUBLIC");
            a2.setOrcid("4444-4444-4444-4447");
            a2.setDisplayIndex(0L);
            a2.setSourceId("4444-4444-4444-4447");
            MOCKS.put(2L, a2);

            AddressEntity a3 = new AddressEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), a3);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), a3);
            a3.setId(3L);
            a3.setIso2Country("CR");
            a3.setVisibility("LIMITED");
            a3.setOrcid("4444-4444-4444-4447");
            a3.setDisplayIndex(0L);
            a3.setClientSourceId("APP-5555555555555555");
            MOCKS.put(3L, a3);

            AddressEntity a4 = new AddressEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), a4);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), a4);
            a4.setId(4L);
            a4.setIso2Country("CR");
            a4.setVisibility("PRIVATE");
            a4.setOrcid("4444-4444-4444-4447");
            a4.setDisplayIndex(0L);
            a4.setClientSourceId("APP-5555555555555555");
            MOCKS.put(4L, a4);

            AddressEntity a5 = new AddressEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), a5);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), a5);
            a5.setId(5L);
            a5.setIso2Country("GB");
            a5.setVisibility("PRIVATE");
            a5.setOrcid("4444-4444-4444-4447");
            a5.setDisplayIndex(0L);
            a5.setSourceId("4444-4444-4444-4447");
            MOCKS.put(5L, a5);

            AddressEntity a6 = new AddressEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), a6);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), a6);
            a6.setId(6L);
            a6.setIso2Country("GB");
            a6.setVisibility("PRIVATE");
            a6.setOrcid("4444-4444-4444-4499");
            a6.setDisplayIndex(0L);
            a6.setClientSourceId("APP-5555555555555555");
            MOCKS.put(6L, a6);

            AddressEntity a7 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a7);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a7);
            a7.setId(7L);
            a7.setIso2Country("US");
            a7.setVisibility("PUBLIC");
            a7.setOrcid("4444-4444-4444-4441");
            a7.setDisplayIndex(0L);
            a7.setClientSourceId("APP-5555555555555555");
            MOCKS.put(7L, a7);

            AddressEntity a8 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a8);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a8);
            a8.setId(8L);
            a8.setIso2Country("US");
            a8.setVisibility("LIMITED");
            a8.setOrcid("4444-4444-4444-4443");
            a8.setDisplayIndex(0L);
            a8.setSourceId("4444-4444-4444-4443");
            a8.setClientSourceId("APP-5555555555555555");
            MOCKS.put(8L, a8);

            AddressEntity a9 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a9);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a9);
            a9.setId(9L);
            a9.setIso2Country("US");
            a9.setVisibility("PUBLIC");
            a9.setOrcid("0000-0000-0000-0003");
            a9.setDisplayIndex(0L);
            a9.setClientSourceId("APP-5555555555555555");
            MOCKS.put(9L, a9);

            AddressEntity a10 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a10);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a10);
            a10.setId(10L);
            a10.setIso2Country("CR");
            a10.setVisibility("LIMITED");
            a10.setOrcid("0000-0000-0000-0003");
            a10.setDisplayIndex(1L);
            a10.setClientSourceId("APP-5555555555555555");
            MOCKS.put(10L, a10);

            AddressEntity a11 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a11);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a11);
            a11.setId(11L);
            a11.setIso2Country("GB");
            a11.setVisibility("PRIVATE");
            a11.setOrcid("0000-0000-0000-0003");
            a11.setDisplayIndex(2L);
            a11.setClientSourceId("APP-5555555555555555");
            MOCKS.put(11L, a11);

            AddressEntity a12 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a12);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a12);
            a12.setId(12L);
            a12.setIso2Country("MX");
            a12.setVisibility("LIMITED");
            a12.setOrcid("0000-0000-0000-0003");
            a12.setDisplayIndex(3L);
            a12.setSourceId("0000-0000-0000-0003");
            MOCKS.put(12L, a12);

            AddressEntity a13 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a13);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a13);
            a13.setId(13L);
            a13.setIso2Country("PE");
            a13.setVisibility("PRIVATE");
            a13.setOrcid("0000-0000-0000-0003");
            a13.setDisplayIndex(4L);
            a13.setSourceId("0000-0000-0000-0003");
            MOCKS.put(13L, a13);

            AddressEntity a14 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a14);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a14);
            a14.setId(14L);
            a14.setIso2Country("US");
            a14.setVisibility("PUBLIC");
            a14.setOrcid("0000-0000-0000-0001");
            a14.setDisplayIndex(4L);
            a14.setClientSourceId("APP-5555555555555555");
            MOCKS.put(14L, a14);

            AddressEntity a15 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a15);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a15);
            a15.setId(15L);
            a15.setIso2Country("CR");
            a15.setVisibility("LIMITED");
            a15.setOrcid("0000-0000-0000-0001");
            a15.setDisplayIndex(4L);
            a15.setClientSourceId("APP-5555555555555555");
            MOCKS.put(15L, a15);

            AddressEntity a16 = new AddressEntity();
            injectDateCreated(parseDate("2016-01-02 15:31:00.00"), a16);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), a16);
            a16.setId(16L);
            a16.setIso2Country("PE");
            a16.setVisibility("PRIVATE");
            a16.setOrcid("0000-0000-0000-0001");
            a16.setDisplayIndex(4L);
            a16.setClientSourceId("APP-5555555555555555");
            MOCKS.put(16L, a16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static AddressEntity getAddress(Long id) {
        return MOCKS.get(id);
    }

    public static Map<Long, AddressEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }
}
