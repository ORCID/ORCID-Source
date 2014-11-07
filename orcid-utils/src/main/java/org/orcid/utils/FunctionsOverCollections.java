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

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class FunctionsOverCollections {

    /**
     * Get a Map<String, String> and sort it by their values instead of their keys.
     * @param unsortedMap
     * @return a Map with the same key-values than the unsortedMap, but, ordered by values.
     * */
    public static Map<String, String> sortMapsByValues(Map<String, String> unsortedMap){
        ValueComparator valueComparator =  new ValueComparator(unsortedMap);
        TreeMap<String,String> sortedMap = new TreeMap<String,String>(valueComparator);
        sortedMap.putAll(unsortedMap);               
        return sortedMap;
    }        
}

class ValueComparator implements Comparator<String> {

    Map<String, String> base;
    public ValueComparator(Map<String, String> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        return base.get(a).compareTo(base.get(b));
    }
}