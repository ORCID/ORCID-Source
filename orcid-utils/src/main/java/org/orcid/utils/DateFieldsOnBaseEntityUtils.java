package org.orcid.utils;

import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;

public final class DateFieldsOnBaseEntityUtils {
    
    public static void setDateFields(Object b, Date date) throws IllegalAccessException {
        FieldUtils.writeField(b, "dateCreated", date, true);
        FieldUtils.writeField(b, "lastModified", date, true);
    }
    
    public static void setDateFields(Object b, Date created, Date lastModified) throws IllegalAccessException {
        FieldUtils.writeField(b, "dateCreated", created, true);
        FieldUtils.writeField(b, "lastModified", lastModified, true);
    }
    
    public static void setLastModifiedDateFields(Object b, Date lastModified) throws IllegalAccessException {
        FieldUtils.writeField(b, "lastModified", lastModified, true);
    }
}
