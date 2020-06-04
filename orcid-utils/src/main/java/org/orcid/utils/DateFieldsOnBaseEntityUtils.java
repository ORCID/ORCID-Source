package org.orcid.utils;

import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;

public final class DateFieldsOnBaseEntityUtils {
    
    public static void setDateFields(Object b, Date now) throws IllegalAccessException {
        FieldUtils.writeField(b, "dateCreated", now, true);
        FieldUtils.writeField(b, "lastModified", now, true);
    }
    
}
