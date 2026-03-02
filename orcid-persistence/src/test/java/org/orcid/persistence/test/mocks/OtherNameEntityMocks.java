package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.OtherNameEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class OtherNameEntityMocks {

    private static final Map<Long, OtherNameEntity> MOCKS = new HashMap<>();

    static {
        try {
            OtherNameEntity on1 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on1);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on1);
            on1.setId(1L);
            on1.setDisplayName("Slibberdy Slabinah");
            on1.setOrcid("4444-4444-4444-4443");
            on1.setClientSourceId("APP-5555555555555555");
            on1.setVisibility("PUBLIC");
            on1.setDisplayIndex(0L);
            MOCKS.put(1L, on1);

            OtherNameEntity on2 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on2);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on2);
            on2.setId(2L);
            on2.setDisplayName("Flibberdy Flabinah");
            on2.setOrcid("4444-4444-4444-4443");
            on2.setSourceId("4444-4444-4444-4443");
            on2.setVisibility("PUBLIC");
            on2.setDisplayIndex(0L);
            MOCKS.put(2L, on2);

            OtherNameEntity on3 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on3);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on3);
            on3.setId(3L);
            on3.setDisplayName("other-name-1");
            on3.setOrcid("4444-4444-4444-4441");
            on3.setSourceId("4444-4444-4444-4441");
            on3.setVisibility("PUBLIC");
            on3.setDisplayIndex(0L);
            MOCKS.put(3L, on3);

            OtherNameEntity on4 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on4);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on4);
            on4.setId(4L);
            on4.setDisplayName("other-name-2");
            on4.setOrcid("4444-4444-4444-4441");
            on4.setSourceId("4444-4444-4444-4441");
            on4.setVisibility("PUBLIC");
            on4.setDisplayIndex(0L);
            MOCKS.put(4L, on4);

            OtherNameEntity on5 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on5);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on5);
            on5.setId(5L);
            on5.setDisplayName("Other Name # 1");
            on5.setOrcid("4444-4444-4444-4446");
            on5.setClientSourceId("APP-5555555555555555");
            on5.setVisibility("PUBLIC");
            on5.setDisplayIndex(0L);
            MOCKS.put(5L, on5);

            OtherNameEntity on6 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on6);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on6);
            on6.setId(6L);
            on6.setDisplayName("Other Name # 2");
            on6.setOrcid("4444-4444-4444-4446");
            on6.setSourceId("4444-4444-4444-4446");
            on6.setVisibility("LIMITED");
            on6.setDisplayIndex(0L);
            MOCKS.put(6L, on6);

            OtherNameEntity on7 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on7);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on7);
            on7.setId(7L);
            on7.setDisplayName("Other Name # 3");
            on7.setOrcid("4444-4444-4444-4446");
            on7.setSourceId("4444-4444-4444-4446");
            on7.setVisibility("PRIVATE");
            on7.setDisplayIndex(0L);
            MOCKS.put(7L, on7);

            OtherNameEntity on8 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on8);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on8);
            on8.setId(8L);
            on8.setDisplayName("Other Name # 4");
            on8.setOrcid("4444-4444-4444-4446");
            on8.setClientSourceId("APP-5555555555555555");
            on8.setVisibility("PRIVATE");
            on8.setDisplayIndex(0L);
            MOCKS.put(8L, on8);

            OtherNameEntity on9 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on9);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on9);
            on9.setId(9L);
            on9.setDisplayName("Other Name # 1");
            on9.setOrcid("4444-4444-4444-4447");
            on9.setClientSourceId("APP-5555555555555555");
            on9.setVisibility("PUBLIC");
            on9.setDisplayIndex(0L);
            MOCKS.put(9L, on9);

            OtherNameEntity on10 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on10);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on10);
            on10.setId(10L);
            on10.setDisplayName("Other Name # 1");
            on10.setOrcid("4444-4444-4444-4442");
            on10.setClientSourceId("APP-5555555555555555");
            on10.setVisibility("PUBLIC");
            on10.setDisplayIndex(0L);
            MOCKS.put(10L, on10);

            OtherNameEntity on11 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on11);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on11);
            on11.setId(11L);
            on11.setDisplayName("Other Name # 2");
            on11.setOrcid("4444-4444-4444-4442");
            on11.setClientSourceId("APP-5555555555555555");
            on11.setVisibility("PRIVATE");
            on11.setDisplayIndex(0L);
            MOCKS.put(11L, on11);

            OtherNameEntity on12 = new OtherNameEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), on12);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), on12);
            on12.setId(12L);
            on12.setDisplayName("Other Name # 3");
            on12.setOrcid("4444-4444-4444-4442");
            on12.setSourceId("4444-4444-4444-4442");
            on12.setVisibility("PRIVATE");
            on12.setDisplayIndex(0L);
            MOCKS.put(12L, on12);

            OtherNameEntity on13 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on13);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on13);
            on13.setId(13L);
            on13.setDisplayName("Other Name PUBLIC");
            on13.setOrcid("0000-0000-0000-0003");
            on13.setClientSourceId("APP-5555555555555555");
            on13.setVisibility("PUBLIC");
            on13.setDisplayIndex(0L);
            MOCKS.put(13L, on13);

            OtherNameEntity on14 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on14);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on14);
            on14.setId(14L);
            on14.setDisplayName("Other Name LIMITED");
            on14.setOrcid("0000-0000-0000-0003");
            on14.setClientSourceId("APP-5555555555555555");
            on14.setVisibility("LIMITED");
            on14.setDisplayIndex(1L);
            MOCKS.put(14L, on14);

            OtherNameEntity on15 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on15);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on15);
            on15.setId(15L);
            on15.setDisplayName("Other Name PRIVATE");
            on15.setOrcid("0000-0000-0000-0003");
            on15.setClientSourceId("APP-5555555555555555");
            on15.setVisibility("PRIVATE");
            on15.setDisplayIndex(2L);
            MOCKS.put(15L, on15);

            OtherNameEntity on16 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on16);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on16);
            on16.setId(16L);
            on16.setDisplayName("Other Name SELF LIMITED");
            on16.setOrcid("0000-0000-0000-0003");
            on16.setSourceId("0000-0000-0000-0003");
            on16.setVisibility("LIMITED");
            on16.setDisplayIndex(3L);
            MOCKS.put(16L, on16);

            OtherNameEntity on17 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on17);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on17);
            on17.setId(17L);
            on17.setDisplayName("Other Name SELF PRIVATE");
            on17.setOrcid("0000-0000-0000-0003");
            on17.setSourceId("0000-0000-0000-0003");
            on17.setVisibility("PRIVATE");
            on17.setDisplayIndex(4L);
            MOCKS.put(17L, on17);

            OtherNameEntity on18 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on18);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on18);
            on18.setId(18L);
            on18.setDisplayName("PUBLIC");
            on18.setOrcid("0000-0000-0000-0001");
            on18.setClientSourceId("APP-5555555555555555");
            on18.setVisibility("PUBLIC");
            on18.setDisplayIndex(1L);
            MOCKS.put(18L, on18);

            OtherNameEntity on19 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on19);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on19);
            on19.setId(19L);
            on19.setDisplayName("LIMITED");
            on19.setOrcid("0000-0000-0000-0001");
            on19.setClientSourceId("APP-5555555555555555");
            on19.setVisibility("LIMITED");
            on19.setDisplayIndex(2L);
            MOCKS.put(19L, on19);

            OtherNameEntity on20 = new OtherNameEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), on20);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), on20);
            on20.setId(20L);
            on20.setDisplayName("PRIVATE");
            on20.setOrcid("0000-0000-0000-0001");
            on20.setClientSourceId("APP-5555555555555555");
            on20.setVisibility("PRIVATE");
            on20.setDisplayIndex(3L);
            MOCKS.put(20L, on20);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static OtherNameEntity getOtherName(Long id) {
        return MOCKS.get(id);
    }

    public static Map<Long, OtherNameEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

}
