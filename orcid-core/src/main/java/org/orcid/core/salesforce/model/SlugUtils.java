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
package org.orcid.core.salesforce.model;

import java.io.IOException;

import com.github.slugify.Slugify;

/**
 * 
 * @author Will Simpson
 *
 */
public class SlugUtils {

    private static final String SLUG_SEPARATOR = "-";

    private static Slugify slugify;
    static {
        try {
            slugify = new Slugify();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing slugify", e);
        }
    }

    public static String createSlug(String id, String name) {
        return id + SLUG_SEPARATOR + slugify.slugify(name);
    }

    public static String extractIdFromSlug(String slug) {
        return slug.substring(0, slug.indexOf(SlugUtils.SLUG_SEPARATOR));
    }

}
