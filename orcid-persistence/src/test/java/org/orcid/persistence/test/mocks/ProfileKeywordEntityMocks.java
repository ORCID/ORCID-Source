package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ProfileKeywordEntityMocks {

    private static final Map<Long, ProfileKeywordEntity> MOCKS = new HashMap<>();

    static {
        try {
            ProfileKeywordEntity k1 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k1);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k1);
            k1.setId(1L);
            k1.setKeywordName("tea making");
            k1.setOrcid("4444-4444-4444-4443");
            k1.setVisibility("PUBLIC");
            k1.setDisplayIndex(0L);
            k1.setClientSourceId("APP-5555555555555555");
            MOCKS.put(1L, k1);

            ProfileKeywordEntity k2 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k2);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k2);
            k2.setId(2L);
            k2.setKeywordName("coffee making");
            k2.setOrcid("4444-4444-4444-4443");
            k2.setVisibility("LIMITED");
            k2.setDisplayIndex(0L);
            k2.setSourceId("4444-4444-4444-4443");
            MOCKS.put(2L, k2);

            ProfileKeywordEntity k3 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k3);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k3);
            k3.setId(3L);
            k3.setKeywordName("chocolat making");
            k3.setOrcid("4444-4444-4444-4443");
            k3.setVisibility("PRIVATE");
            k3.setDisplayIndex(0L);
            k3.setSourceId("4444-4444-4444-4443");
            MOCKS.put(3L, k3);

            ProfileKeywordEntity k4 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k4);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k4);
            k4.setId(4L);
            k4.setKeywordName("what else can we make?");
            k4.setOrcid("4444-4444-4444-4443");
            k4.setVisibility("PRIVATE");
            k4.setDisplayIndex(0L);
            k4.setClientSourceId("APP-5555555555555555");
            MOCKS.put(4L, k4);

            ProfileKeywordEntity k5 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k5);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k5);
            k5.setId(5L);
            k5.setKeywordName("keyword-1");
            k5.setOrcid("4444-4444-4444-4441");
            k5.setVisibility("PUBLIC");
            k5.setDisplayIndex(0L);
            k5.setClientSourceId("APP-5555555555555555");
            MOCKS.put(5L, k5);

            ProfileKeywordEntity k6 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k6);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k6);
            k6.setId(6L);
            k6.setKeywordName("keyword-2");
            k6.setOrcid("4444-4444-4444-4441");
            k6.setVisibility("PUBLIC");
            k6.setDisplayIndex(0L);
            k6.setClientSourceId("APP-5555555555555555");
            MOCKS.put(6L, k6);

            ProfileKeywordEntity k7 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k7);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k7);
            k7.setId(7L);
            k7.setKeywordName("My keyword");
            k7.setOrcid("4444-4444-4444-4442");
            k7.setVisibility("PUBLIC");
            k7.setDisplayIndex(0L);
            k7.setClientSourceId("APP-5555555555555555");
            MOCKS.put(7L, k7);

            ProfileKeywordEntity k8 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), k8);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), k8);
            k8.setId(8L);
            k8.setKeywordName("My keyword");
            k8.setOrcid("4444-4444-4444-4499");
            k8.setVisibility("PUBLIC");
            k8.setDisplayIndex(0L);
            k8.setClientSourceId("APP-5555555555555555");
            MOCKS.put(8L, k8);

            ProfileKeywordEntity k9 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k9);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k9);
            k9.setId(9L);
            k9.setKeywordName("PUBLIC");
            k9.setOrcid("0000-0000-0000-0003");
            k9.setVisibility("PUBLIC");
            k9.setDisplayIndex(0L);
            k9.setClientSourceId("APP-5555555555555555");
            MOCKS.put(9L, k9);

            ProfileKeywordEntity k10 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k10);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k10);
            k10.setId(10L);
            k10.setKeywordName("LIMITED");
            k10.setOrcid("0000-0000-0000-0003");
            k10.setVisibility("LIMITED");
            k10.setDisplayIndex(1L);
            k10.setClientSourceId("APP-5555555555555555");
            MOCKS.put(10L, k10);

            ProfileKeywordEntity k11 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k11);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k11);
            k11.setId(11L);
            k11.setKeywordName("PRIVATE");
            k11.setOrcid("0000-0000-0000-0003");
            k11.setVisibility("PRIVATE");
            k11.setDisplayIndex(2L);
            k11.setClientSourceId("APP-5555555555555555");
            MOCKS.put(11L, k11);

            ProfileKeywordEntity k12 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k12);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k12);
            k12.setId(12L);
            k12.setKeywordName("SELF LIMITED");
            k12.setOrcid("0000-0000-0000-0003");
            k12.setVisibility("LIMITED");
            k12.setDisplayIndex(3L);
            k12.setSourceId("0000-0000-0000-0003");
            MOCKS.put(12L, k12);

            ProfileKeywordEntity k13 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k13);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k13);
            k13.setId(13L);
            k13.setKeywordName("SELF PRIVATE");
            k13.setOrcid("0000-0000-0000-0003");
            k13.setVisibility("PRIVATE");
            k13.setDisplayIndex(4L);
            k13.setSourceId("0000-0000-0000-0003");
            MOCKS.put(13L, k13);

            ProfileKeywordEntity k14 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k14);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k14);
            k14.setId(14L);
            k14.setKeywordName("PUBLIC");
            k14.setOrcid("0000-0000-0000-0001");
            k14.setVisibility("PUBLIC");
            k14.setDisplayIndex(4L);
            k14.setClientSourceId("APP-5555555555555555");
            MOCKS.put(14L, k14);

            ProfileKeywordEntity k15 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k15);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k15);
            k15.setId(15L);
            k15.setKeywordName("LIMITED");
            k15.setOrcid("0000-0000-0000-0001");
            k15.setVisibility("LIMITED");
            k15.setDisplayIndex(4L);
            k15.setClientSourceId("APP-5555555555555555");
            MOCKS.put(15L, k15);

            ProfileKeywordEntity k16 = new ProfileKeywordEntity();
            injectDateCreated(parseDate("2016-04-01 15:31:00.00"), k16);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), k16);
            k16.setId(16L);
            k16.setKeywordName("PRIVATE");
            k16.setOrcid("0000-0000-0000-0001");
            k16.setVisibility("PRIVATE");
            k16.setDisplayIndex(4L);
            k16.setClientSourceId("APP-5555555555555555");
            MOCKS.put(16L, k16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ProfileKeywordEntity getProfileKeyword(Long id) {
        return MOCKS.get(id);
    }

    public static Map<Long, ProfileKeywordEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

}
