package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ResearcherUrlEntityMocks {

    private static final Map<Long, ResearcherUrlEntity> MOCKS = new HashMap<>();

    static {
        try {
            ResearcherUrlEntity ru1 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru1);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru1);
            ru1.setId(1L);
            ru1.setUrl("www.4444-4444-4444-4441.com");
            ru1.setUrlName("444_1");
            ru1.setOrcid("4444-4444-4444-4441");
            ru1.setSourceId("4444-4444-4444-4441");
            ru1.setVisibility("PUBLIC");
            ru1.setDisplayIndex(0L);
            MOCKS.put(1L, ru1);

            ResearcherUrlEntity ru2 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru2);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru2);
            ru2.setId(2L);
            ru2.setUrl("http://www.researcherurl2.com?id=1");
            ru2.setUrlName("443_1");
            ru2.setOrcid("4444-4444-4444-4443");
            ru2.setSourceId("4444-4444-4444-4443");
            ru2.setVisibility("PUBLIC");
            ru2.setDisplayIndex(0L);
            MOCKS.put(2L, ru2);

            ResearcherUrlEntity ru3 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru3);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru3);
            ru3.setId(3L);
            ru3.setUrl("http://www.researcherurl2.com?id=2");
            ru3.setUrlName("443_2");
            ru3.setOrcid("4444-4444-4444-4443");
            ru3.setSourceId("4444-4444-4444-4443");
            ru3.setVisibility("PUBLIC");
            ru3.setDisplayIndex(0L);
            MOCKS.put(3L, ru3);

            ResearcherUrlEntity ru4 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru4);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru4);
            ru4.setId(4L);
            ru4.setUrl("www.4444-4444-4444-4445.com");
            ru4.setOrcid("4444-4444-4444-4445");
            ru4.setClientSourceId("APP-5555555555555555");
            ru4.setVisibility("PUBLIC");
            ru4.setDisplayIndex(0L);
            MOCKS.put(4L, ru4);

            ResearcherUrlEntity ru5 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru5);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru5);
            ru5.setId(5L);
            ru5.setUrl("http://www.researcherurl2.com?id=5");
            ru5.setUrlName("443_3");
            ru5.setOrcid("4444-4444-4444-4443");
            ru5.setClientSourceId("APP-5555555555555555");
            ru5.setVisibility("LIMITED");
            ru5.setDisplayIndex(0L);
            MOCKS.put(5L, ru5);

            ResearcherUrlEntity ru6 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru6);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru6);
            ru6.setId(6L);
            ru6.setUrl("http://www.researcherurl2.com?id=6");
            ru6.setUrlName("443_4");
            ru6.setOrcid("4444-4444-4444-4443");
            ru6.setSourceId("4444-4444-4444-4443");
            ru6.setVisibility("PRIVATE");
            ru6.setDisplayIndex(0L);
            MOCKS.put(6L, ru6);

            ResearcherUrlEntity ru7 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru7);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru7);
            ru7.setId(7L);
            ru7.setUrl("http://www.researcherurl2.com?id=7");
            ru7.setUrlName("443_5");
            ru7.setOrcid("4444-4444-4444-4443");
            ru7.setClientSourceId("APP-5555555555555555");
            ru7.setVisibility("PRIVATE");
            ru7.setDisplayIndex(0L);
            MOCKS.put(7L, ru7);

            ResearcherUrlEntity ru8 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru8);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru8);
            ru8.setId(8L);
            ru8.setUrl("http://www.researcherurl2.com?id=8");
            ru8.setUrlName("443_6");
            ru8.setOrcid("4444-4444-4444-4443");
            ru8.setSourceId("4444-4444-4444-4443");
            ru8.setVisibility("LIMITED");
            ru8.setDisplayIndex(0L);
            MOCKS.put(8L, ru8);

            ResearcherUrlEntity ru9 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru9);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru9);
            ru9.setId(9L);
            ru9.setUrl("http://www.researcherurl.com?id=9");
            ru9.setUrlName("1");
            ru9.setOrcid("4444-4444-4444-4442");
            ru9.setSourceId("4444-4444-4444-4442");
            ru9.setVisibility("PUBLIC");
            ru9.setDisplayIndex(0L);
            MOCKS.put(9L, ru9);

            ResearcherUrlEntity ru10 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru10);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru10);
            ru10.setId(10L);
            ru10.setUrl("http://www.researcherurl.com?id=10");
            ru10.setUrlName("2");
            ru10.setOrcid("4444-4444-4444-4442");
            ru10.setSourceId("4444-4444-4444-4442");
            ru10.setVisibility("LIMITED");
            ru10.setDisplayIndex(0L);
            MOCKS.put(10L, ru10);

            ResearcherUrlEntity ru11 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru11);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru11);
            ru11.setId(11L);
            ru11.setUrl("http://www.researcherurl.com?id=11");
            ru11.setUrlName("3");
            ru11.setOrcid("4444-4444-4444-4442");
            ru11.setSourceId("4444-4444-4444-4442");
            ru11.setVisibility("PRIVATE");
            ru11.setDisplayIndex(0L);
            MOCKS.put(11L, ru11);

            ResearcherUrlEntity ru12 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru12);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru12);
            ru12.setId(12L);
            ru12.setUrl("http://www.researcherurl.com?id=12");
            ru12.setUrlName("4");
            ru12.setOrcid("4444-4444-4444-4442");
            ru12.setClientSourceId("APP-5555555555555555");
            ru12.setVisibility("PRIVATE");
            ru12.setDisplayIndex(0L);
            MOCKS.put(12L, ru12);

            ResearcherUrlEntity ru13 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru13);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru13);
            ru13.setId(13L);
            ru13.setUrl("http://www.researcherurl.com?id=13");
            ru13.setUrlName("public_rurl");
            ru13.setOrcid("0000-0000-0000-0003");
            ru13.setClientSourceId("APP-5555555555555555");
            ru13.setVisibility("PUBLIC");
            ru13.setDisplayIndex(0L);
            MOCKS.put(13L, ru13);

            ResearcherUrlEntity ru14 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru14);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru14);
            ru14.setId(14L);
            ru14.setUrl("http://www.researcherurl.com?id=14");
            ru14.setUrlName("limited_rurl");
            ru14.setOrcid("0000-0000-0000-0003");
            ru14.setClientSourceId("APP-5555555555555555");
            ru14.setVisibility("LIMITED");
            ru14.setDisplayIndex(1L);
            MOCKS.put(14L, ru14);

            ResearcherUrlEntity ru15 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru15);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru15);
            ru15.setId(15L);
            ru15.setUrl("http://www.researcherurl.com?id=15");
            ru15.setUrlName("private_rurl");
            ru15.setOrcid("0000-0000-0000-0003");
            ru15.setClientSourceId("APP-5555555555555555");
            ru15.setVisibility("PRIVATE");
            ru15.setDisplayIndex(2L);
            MOCKS.put(15L, ru15);

            ResearcherUrlEntity ru16 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru16);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru16);
            ru16.setId(16L);
            ru16.setUrl("http://www.researcherurl.com?id=16");
            ru16.setUrlName("self_limited_rurl");
            ru16.setOrcid("0000-0000-0000-0003");
            ru16.setSourceId("0000-0000-0000-0003");
            ru16.setVisibility("LIMITED");
            ru16.setDisplayIndex(3L);
            MOCKS.put(16L, ru16);

            ResearcherUrlEntity ru17 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru17);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru17);
            ru17.setId(17L);
            ru17.setUrl("http://www.researcherurl.com?id=17");
            ru17.setUrlName("self_private_rurl");
            ru17.setOrcid("0000-0000-0000-0003");
            ru17.setSourceId("0000-0000-0000-0003");
            ru17.setVisibility("PRIVATE");
            ru17.setDisplayIndex(4L);
            MOCKS.put(17L, ru17);

            ResearcherUrlEntity ru18 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru18);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru18);
            ru18.setId(18L);
            ru18.setUrl("http://www.researcherurl.com?id=18");
            ru18.setUrlName("public");
            ru18.setOrcid("0000-0000-0000-0001");
            ru18.setClientSourceId("APP-5555555555555555");
            ru18.setVisibility("PUBLIC");
            ru18.setDisplayIndex(1L);
            MOCKS.put(18L, ru18);

            ResearcherUrlEntity ru19 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru19);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru19);
            ru19.setId(19L);
            ru19.setUrl("http://www.researcherurl.com?id=19");
            ru19.setUrlName("limited");
            ru19.setOrcid("0000-0000-0000-0001");
            ru19.setClientSourceId("APP-5555555555555555");
            ru19.setVisibility("LIMITED");
            ru19.setDisplayIndex(1L);
            MOCKS.put(19L, ru19);

            ResearcherUrlEntity ru20 = new ResearcherUrlEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ru20);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ru20);
            ru20.setId(20L);
            ru20.setUrl("http://www.researcherurl.com?id=20");
            ru20.setUrlName("private");
            ru20.setOrcid("0000-0000-0000-0001");
            ru20.setClientSourceId("APP-5555555555555555");
            ru20.setVisibility("PRIVATE");
            ru20.setDisplayIndex(1L);
            MOCKS.put(20L, ru20);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResearcherUrlEntity getResearcherUrl(Long id) {
        return MOCKS.get(id);
    }

    public static Map<Long, ResearcherUrlEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

}
