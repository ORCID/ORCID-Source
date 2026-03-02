package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class EmailDomainEntityMocks {

    private static final Map<Long, EmailDomainEntity> MOCKS = new HashMap<>();

    static {
        try {
            EmailDomainEntity ed0 = new EmailDomainEntity();
            injectDateCreated(parseDate("None"), ed0);
            injectLastModified(parseDate("None"), ed0);
            ed0.setId(0L);
            ed0.setEmailDomain("orcid.org");
            ed0.setCategory(EmailDomainEntity.DomainCategory.PROFESSIONAL);
            MOCKS.put(0L, ed0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static EmailDomainEntity getEmailDomain(Long id) {
        return MOCKS.get(id);
    }

    public static Map<Long, EmailDomainEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

}
