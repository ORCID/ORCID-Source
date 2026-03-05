package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class ExternalIdentifierEntityMocks {

    private static final Map<Long, ExternalIdentifierEntity> MOCKS = new HashMap<>();

    static {
        try {
            ExternalIdentifierEntity ei1 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei1);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei1);
            ei1.setId(1L);
            ei1.setExternalIdReference("d3clan");
            ei1.setExternalIdCommonName("Facebook");
            ei1.setOrcid("4444-4444-4444-4443");
            ei1.setExternalIdUrl("http://www.facebook.com/d3clan");
            ei1.setSourceId("4444-4444-4444-4441");
            ei1.setVisibility("PUBLIC");
            ei1.setDisplayIndex(0L);
            MOCKS.put(1L, ei1);

            ExternalIdentifierEntity ei2 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei2);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei2);
            ei2.setId(2L);
            ei2.setExternalIdReference("abc123");
            ei2.setExternalIdCommonName("Facebook");
            ei2.setOrcid("4444-4444-4444-4442");
            ei2.setExternalIdUrl("http://www.facebook.com/abc123");
            ei2.setClientSourceId("APP-5555555555555555");
            ei2.setVisibility("PUBLIC");
            ei2.setDisplayIndex(0L);
            MOCKS.put(2L, ei2);

            ExternalIdentifierEntity ei3 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei3);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei3);
            ei3.setId(3L);
            ei3.setExternalIdReference("abc456");
            ei3.setExternalIdCommonName("Facebook");
            ei3.setOrcid("4444-4444-4444-4442");
            ei3.setExternalIdUrl("http://www.facebook.com/abc456");
            ei3.setSourceId("4444-4444-4444-4442");
            ei3.setVisibility("LIMITED");
            ei3.setDisplayIndex(0L);
            MOCKS.put(3L, ei3);

            ExternalIdentifierEntity ei4 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei4);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei4);
            ei4.setId(4L);
            ei4.setExternalIdReference("abc789");
            ei4.setExternalIdCommonName("Facebook");
            ei4.setOrcid("4444-4444-4444-4442");
            ei4.setExternalIdUrl("http://www.facebook.com/abc789");
            ei4.setSourceId("4444-4444-4444-4441");
            ei4.setVisibility("PRIVATE");
            ei4.setDisplayIndex(0L);
            MOCKS.put(4L, ei4);

            ExternalIdentifierEntity ei5 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei5);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei5);
            ei5.setId(5L);
            ei5.setExternalIdReference("abc012");
            ei5.setExternalIdCommonName("Facebook");
            ei5.setOrcid("4444-4444-4444-4442");
            ei5.setExternalIdUrl("http://www.facebook.com/abc012");
            ei5.setClientSourceId("APP-5555555555555555");
            ei5.setVisibility("PRIVATE");
            ei5.setDisplayIndex(0L);
            MOCKS.put(5L, ei5);

            ExternalIdentifierEntity ei6 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei6);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei6);
            ei6.setId(6L);
            ei6.setExternalIdReference("xyz012");
            ei6.setExternalIdCommonName("Google");
            ei6.setOrcid("4444-4444-4444-4444");
            ei6.setExternalIdUrl("http://www.google.com/xyz012");
            ei6.setClientSourceId("APP-5555555555555555");
            ei6.setVisibility("PUBLIC");
            ei6.setDisplayIndex(0L);
            MOCKS.put(6L, ei6);

            ExternalIdentifierEntity ei7 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei7);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei7);
            ei7.setId(7L);
            ei7.setExternalIdReference("A-0001");
            ei7.setExternalIdCommonName("A-0001");
            ei7.setOrcid("4444-4444-4444-4441");
            ei7.setExternalIdUrl("http://ext-id/A-0001");
            ei7.setClientSourceId("APP-5555555555555555");
            ei7.setVisibility("PUBLIC");
            ei7.setDisplayIndex(0L);
            MOCKS.put(7L, ei7);

            ExternalIdentifierEntity ei8 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei8);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei8);
            ei8.setId(8L);
            ei8.setExternalIdReference("A-0002");
            ei8.setExternalIdCommonName("A-0002");
            ei8.setOrcid("4444-4444-4444-4441");
            ei8.setExternalIdUrl("http://ext-id/A-0002");
            ei8.setClientSourceId("APP-5555555555555555");
            ei8.setVisibility("LIMITED");
            ei8.setDisplayIndex(0L);
            MOCKS.put(8L, ei8);

            ExternalIdentifierEntity ei9 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei9);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei9);
            ei9.setId(9L);
            ei9.setExternalIdReference("ref1");
            ei9.setExternalIdCommonName("type1");
            ei9.setOrcid("4444-4444-4444-4497");
            ei9.setExternalIdUrl("http://ext-id/ref1");
            ei9.setClientSourceId("APP-5555555555555556");
            ei9.setVisibility("PRIVATE");
            ei9.setDisplayIndex(0L);
            MOCKS.put(9L, ei9);

            ExternalIdentifierEntity ei10 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei10);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei10);
            ei10.setId(10L);
            ei10.setExternalIdReference("ref2");
            ei10.setExternalIdCommonName("type2");
            ei10.setOrcid("4444-4444-4444-4497");
            ei10.setExternalIdUrl("http://ext-id/ref2");
            ei10.setClientSourceId("APP-5555555555555555");
            ei10.setVisibility("PRIVATE");
            ei10.setDisplayIndex(0L);
            MOCKS.put(10L, ei10);

            ExternalIdentifierEntity ei11 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei11);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei11);
            ei11.setId(11L);
            ei11.setExternalIdReference("ref3");
            ei11.setExternalIdCommonName("type3");
            ei11.setOrcid("4444-4444-4444-4497");
            ei11.setExternalIdUrl("http://ext-id/ref3");
            ei11.setClientSourceId("APP-5555555555555555");
            ei11.setVisibility("LIMITED");
            ei11.setDisplayIndex(0L);
            MOCKS.put(11L, ei11);

            ExternalIdentifierEntity ei12 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei12);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei12);
            ei12.setId(12L);
            ei12.setExternalIdReference("ref4");
            ei12.setExternalIdCommonName("type4");
            ei12.setOrcid("4444-4444-4444-4497");
            ei12.setExternalIdUrl("http://ext-id/ref4");
            ei12.setClientSourceId("APP-5555555555555555");
            ei12.setVisibility("PUBLIC");
            ei12.setDisplayIndex(0L);
            MOCKS.put(12L, ei12);

            ExternalIdentifierEntity ei13 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei13);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei13);
            ei13.setId(13L);
            ei13.setExternalIdReference("public_ref");
            ei13.setExternalIdCommonName("public_type");
            ei13.setOrcid("0000-0000-0000-0003");
            ei13.setExternalIdUrl("http://ext-id/public_ref");
            ei13.setClientSourceId("APP-5555555555555555");
            ei13.setVisibility("PUBLIC");
            ei13.setDisplayIndex(0L);
            MOCKS.put(13L, ei13);

            ExternalIdentifierEntity ei14 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei14);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei14);
            ei14.setId(14L);
            ei14.setExternalIdReference("limited_ref");
            ei14.setExternalIdCommonName("limited_type");
            ei14.setOrcid("0000-0000-0000-0003");
            ei14.setExternalIdUrl("http://ext-id/limited_ref");
            ei14.setClientSourceId("APP-5555555555555555");
            ei14.setVisibility("LIMITED");
            ei14.setDisplayIndex(1L);
            MOCKS.put(14L, ei14);

            ExternalIdentifierEntity ei15 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei15);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei15);
            ei15.setId(15L);
            ei15.setExternalIdReference("private_ref");
            ei15.setExternalIdCommonName("private_type");
            ei15.setOrcid("0000-0000-0000-0003");
            ei15.setExternalIdUrl("http://ext-id/private_ref");
            ei15.setClientSourceId("APP-5555555555555555");
            ei15.setVisibility("PRIVATE");
            ei15.setDisplayIndex(2L);
            MOCKS.put(15L, ei15);

            ExternalIdentifierEntity ei16 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei16);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei16);
            ei16.setId(16L);
            ei16.setExternalIdReference("self_limited_ref");
            ei16.setExternalIdCommonName("self_limited_type");
            ei16.setOrcid("0000-0000-0000-0003");
            ei16.setExternalIdUrl("http://ext-id/self/limited");
            ei16.setSourceId("0000-0000-0000-0003");
            ei16.setVisibility("LIMITED");
            ei16.setDisplayIndex(3L);
            MOCKS.put(16L, ei16);

            ExternalIdentifierEntity ei17 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei17);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei17);
            ei17.setId(17L);
            ei17.setExternalIdReference("self_private_ref");
            ei17.setExternalIdCommonName("self_private_type");
            ei17.setOrcid("0000-0000-0000-0003");
            ei17.setExternalIdUrl("http://ext-id/self/private");
            ei17.setSourceId("0000-0000-0000-0003");
            ei17.setVisibility("PRIVATE");
            ei17.setDisplayIndex(4L);
            MOCKS.put(17L, ei17);

            ExternalIdentifierEntity ei18 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:32:00.00"), ei18);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei18);
            ei18.setId(18L);
            ei18.setExternalIdReference("self_public_ref");
            ei18.setExternalIdCommonName("self_public_type");
            ei18.setOrcid("0000-0000-0000-0003");
            ei18.setExternalIdUrl("http://ext-id/self/public");
            ei18.setSourceId("0000-0000-0000-0003");
            ei18.setVisibility("PUBLIC");
            ei18.setDisplayIndex(5L);
            MOCKS.put(18L, ei18);

            ExternalIdentifierEntity ei19 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:32:00.00"), ei19);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei19);
            ei19.setId(19L);
            ei19.setExternalIdReference("self_public_user_obo_ref");
            ei19.setExternalIdCommonName("self_public_user_obo_type");
            ei19.setOrcid("0000-0000-0000-0003");
            ei19.setExternalIdUrl("http://ext-id/self/obo/public");
            ei19.setClientSourceId("APP-5555555555555558");
            ei19.setVisibility("PUBLIC");
            ei19.setDisplayIndex(6L);
            MOCKS.put(19L, ei19);

            ExternalIdentifierEntity ei20 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei20);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei20);
            ei20.setId(20L);
            ei20.setExternalIdReference("public");
            ei20.setExternalIdCommonName("public");
            ei20.setOrcid("0000-0000-0000-0001");
            ei20.setExternalIdUrl("http://ext-id/self/public");
            ei20.setClientSourceId("APP-5555555555555555");
            ei20.setVisibility("PUBLIC");
            ei20.setDisplayIndex(1L);
            MOCKS.put(20L, ei20);

            ExternalIdentifierEntity ei21 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei21);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei21);
            ei21.setId(21L);
            ei21.setExternalIdReference("limited");
            ei21.setExternalIdCommonName("limited");
            ei21.setOrcid("0000-0000-0000-0001");
            ei21.setExternalIdUrl("http://ext-id/self/limited");
            ei21.setClientSourceId("APP-5555555555555555");
            ei21.setVisibility("LIMITED");
            ei21.setDisplayIndex(2L);
            MOCKS.put(21L, ei21);

            ExternalIdentifierEntity ei22 = new ExternalIdentifierEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), ei22);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), ei22);
            ei22.setId(22L);
            ei22.setExternalIdReference("private");
            ei22.setExternalIdCommonName("private");
            ei22.setOrcid("0000-0000-0000-0001");
            ei22.setExternalIdUrl("http://ext-id/self/private");
            ei22.setClientSourceId("APP-5555555555555555");
            ei22.setVisibility("PRIVATE");
            ei22.setDisplayIndex(2L);
            MOCKS.put(22L, ei22);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExternalIdentifierEntity getExternalIdentifier(Long id) {
        return MOCKS.get(id);
    }

    public static Map<Long, ExternalIdentifierEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }
}
