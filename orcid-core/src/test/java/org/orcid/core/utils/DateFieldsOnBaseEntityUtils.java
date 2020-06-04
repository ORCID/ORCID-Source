package org.orcid.core.utils;

import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.orcid.persistence.jpa.entities.BaseEntity;

public final class DateFieldsOnBaseEntityUtils {
    
    public static void setDateFields(BaseEntity b, Date now) throws IllegalAccessException {
        FieldUtils.writeField(b, "dateCreated", now, true);
        FieldUtils.writeField(b, "lastModified", now, true);
    }
    
}
