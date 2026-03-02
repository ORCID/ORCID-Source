package org.orcid.persistence.test.mocks;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MocksHelper {
    static Date parseDate(String dateStr) {
        try {
            if (dateStr.length() == 10) {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    static void injectDateCreated(Date dateCreated, Object o) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(o.getClass(), "dateCreated");
        field.setAccessible(true);
        field.set(o, dateCreated);
        field.setAccessible(false);
    }

    static void injectLastModified(Date lastModified, Object o) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(o.getClass(), "lastModified");
        field.setAccessible(true);
        field.set(o, lastModified);
        field.setAccessible(false);
    }

    private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }
}
