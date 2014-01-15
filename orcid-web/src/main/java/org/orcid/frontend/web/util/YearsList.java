/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Will Simpson
 * 
 */
public class YearsList {

    private static final int DEFAULT_EARLIEST_YEAR = 1913;

    public static List<String> createList() {
        return createList((List<String>) null);
    }
    
    public static List<String> createList(int yearInFuture) {
    	return createList((List<String>) null, yearInFuture);
    }

    public static List<String> createList(String itemToPrepend) {
        List<String> listToPrepend = new ArrayList<String>(1);
        listToPrepend.add(itemToPrepend);
        return createList(listToPrepend);
    }

    public static Map<String, String> createMap(String itemToPrepend) {
        Map<String, String> yearsMap = new LinkedHashMap<String, String>();
        int index = 0;
        List<String> yearsList = createList(itemToPrepend);
        for (String year : yearsList) {
            yearsMap.put(String.valueOf(index++), year);
        }

        return yearsMap;

    }

    public static List<String> createList(List<String> listToPrepend) {
        return createList(listToPrepend, getLatestYear(), DEFAULT_EARLIEST_YEAR);
    }
    
    public static List<String> createList(List<String> listToPrepend, int yearsInFuture) {
        return createList(listToPrepend, getLatestYear() + yearsInFuture, DEFAULT_EARLIEST_YEAR);
    }

    private static List<String> createList(List<String> listToPrepend, int latestYear, int earliestYear) {
        List<String> years = new ArrayList<String>();
        if (listToPrepend != null) {
            years.addAll(listToPrepend);
        }
        for (int i = latestYear; i >= earliestYear; i--) {
            years.add(String.valueOf(i));
        }
        return years;
    }

    private static int getLatestYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 3);
        return calendar.get(Calendar.YEAR);
    }

}
