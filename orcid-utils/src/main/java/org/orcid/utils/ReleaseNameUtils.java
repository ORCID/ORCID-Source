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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ReleaseNameUtils {

    private static String releaseName;

    static {
        releaseName = readReleaseNameFromFile();
        if (StringUtils.isBlank(releaseName)) {
            releaseName = DateUtils.convertToXMLGregorianCalendar(new Date()).toXMLFormat();
        }
    }

    private static String readReleaseNameFromFile() {
        try (InputStream is = ReleaseNameUtils.class.getResourceAsStream("/release_name.txt")) {
            if (is != null) {
                String input = IOUtils.toString(is);
                return input.trim();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading release name file", e);
        }
        return null;
    }

    public static String getReleaseName() {
        return releaseName;
    }

}
