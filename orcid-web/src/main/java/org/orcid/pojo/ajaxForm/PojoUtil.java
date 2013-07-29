package org.orcid.pojo.ajaxForm;

public class PojoUtil {
    public static boolean isEmpty(Text text) {
        if (text == null || text.getValue() == null || text.getValue().trim().isEmpty()) return true;
        return false;
    }

    
    public static boolean isEmpty(String string) {
        if (string == null || string.trim().isEmpty()) return true;
        return false;
    }

}
