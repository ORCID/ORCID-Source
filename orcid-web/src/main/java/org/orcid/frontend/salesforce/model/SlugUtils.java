package org.orcid.frontend.salesforce.model;

/**
 * 
 * @author Will Simpson
 *
 */
public class SlugUtils {

    private static final String SLUG_SEPARATOR = "-";

    public static boolean containsSlug(String url) {
        return (url == null) ? false : (url.indexOf(SlugUtils.SLUG_SEPARATOR) > 0); 
    }
    
    public static String extractIdFromSlug(String slug) {
        return slug.substring(0, slug.indexOf(SlugUtils.SLUG_SEPARATOR));
    }

}
