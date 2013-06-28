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
package org.orcid.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 2011-2012 ORCID
 * </p>
 * <p>
 * User: Declan Newman (declan) Date: 10/02/2012
 * </p>
 */
public class OrcidStringUtils {

    private static final Pattern pattern = Pattern.compile("(\\d{4}-){3,}\\d{3}[\\dX]");

    public static boolean isValidOrcid(String orcid) {
        if (StringUtils.isNotBlank(orcid)) {
            return pattern.matcher(orcid).matches();
        } else {
            return false;
        }
    }

    public static String getOrcidNumber(String orcid) {
        Matcher matcher = pattern.matcher(orcid);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static Map<String, String> resourceBundleToMap(ResourceBundle resource) {
        Map<String, String> map = new HashMap<String, String>();

        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = resource.getString(key);
            map.put(key, resource.getString(key));
        }

        return map;
    }

}
