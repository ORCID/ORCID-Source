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

    private static final String RELEASE_NAME_PROPERTY = "releaseName";
    
    private static String releaseName;

    static {
        // Read release name from property, because it may already have been set
        // by another web app
        String releaseNameFromSystemProperty = System.getProperty(RELEASE_NAME_PROPERTY);
        if (StringUtils.isBlank(releaseNameFromSystemProperty)) {
            releaseName = readReleaseNameFromFile();
            if (StringUtils.isBlank(releaseName)) {
                releaseName = DateUtils.convertToXMLGregorianCalendar(new Date()).toXMLFormat();
            }
            // Set the system property, so that other web apps can use the same
            // value
            System.setProperty(RELEASE_NAME_PROPERTY, releaseName);
        } else {
            releaseName = releaseNameFromSystemProperty;
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
