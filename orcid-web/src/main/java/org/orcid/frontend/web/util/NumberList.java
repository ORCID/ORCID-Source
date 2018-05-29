package org.orcid.frontend.web.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class NumberList {

    private static final int DEFAULT_MINIMUM = 1;

    public static List<String> createList(int maximum) {
        return createList((List<String>) null, maximum);
    }

    public static List<String> createList(String itemToPrepend, int maximum) {
        List<String> listToPrepend = new ArrayList<String>(1);
        listToPrepend.add(itemToPrepend);
        return createList(listToPrepend, maximum);
    }

    public static List<String> createList(List<String> listToPrepend, int maximum) {
        return createList(listToPrepend, DEFAULT_MINIMUM, maximum);
    }

    private static List<String> createList(List<String> listToPrepend, int minimum, int maximum) {
        List<String> numberList = new ArrayList<String>();
        if (listToPrepend != null) {
            numberList.addAll(listToPrepend);
        }
        for (int i = minimum; i <= maximum; i++) {
            numberList.add(StringUtils.leftPad(String.valueOf(i), 2, '0'));
        }
        return numberList;
    }

}
