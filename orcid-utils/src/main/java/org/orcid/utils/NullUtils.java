/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.utils;

/**
 * @author Will Simpson
 */
public class NullUtils {

    public static String blankIfNull(String string) {
        return string == null ? "" : string;
    }

    public static boolean allNull(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean anyNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean noneNull(Object... objects) {
        return !anyNull(objects);
    }

    public static int compareNulls(Object thisObject, Object otherObject) {
        if (thisObject == null) {
            return otherObject == null ? 0 : 1;
        } else {
            return otherObject == null ? -1 : 0;
        }
    }

    public static <T extends Comparable<T>> int compareObjectsNullSafe(T thisObject, T otherObject) {
        if (anyNull(thisObject, otherObject)) {
            return compareNulls(thisObject, otherObject);
        }
        return thisObject.compareTo(otherObject);
    }

}
