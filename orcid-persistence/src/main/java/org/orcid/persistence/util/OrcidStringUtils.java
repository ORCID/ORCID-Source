package org.orcid.persistence.util;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * TODO: Once the jersey migration is over, this should go back to the orcid-utils package so it could be reused form the orcid-persistence package 
 */
@Deprecated
public class OrcidStringUtils {
    public static String ORCID_STRING = "(\\d{4}-){3}\\d{3}[\\dX]";
    public static final Pattern orcidPattern = Pattern.compile(ORCID_STRING);
    
    public static int compareStrings(String string, String otherString) {
        if (anyNull(string, otherString)) {
            return compareNulls(string, otherString);
        }
        return string.compareTo(otherString);
    }
    
    public static boolean anyNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
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
    
    public static boolean isValidOrcid(String orcid) {
        if (StringUtils.isNotBlank(orcid)) {
            return orcidPattern.matcher(orcid).matches();
        } else {
            return false;
        }
    }
}
