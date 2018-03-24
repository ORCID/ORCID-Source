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
