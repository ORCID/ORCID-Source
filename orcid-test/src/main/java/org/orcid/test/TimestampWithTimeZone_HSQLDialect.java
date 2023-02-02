package org.orcid.test;

import java.sql.Types;

import org.hibernate.dialect.HSQLDialect;

public class TimestampWithTimeZone_HSQLDialect extends HSQLDialect {

    public TimestampWithTimeZone_HSQLDialect() {
        super();
        this.registerHibernateType(Types.TIMESTAMP_WITH_TIMEZONE, CLOSED_QUOTE);
    }
    
}
