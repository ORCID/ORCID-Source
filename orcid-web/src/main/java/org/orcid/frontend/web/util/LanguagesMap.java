package org.orcid.frontend.web.util;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

public class LanguagesMap {
    /**
     * This map contains all the available languages in all available locales.
     * The structure looks like this:
     * 
     * For each Locale that already accessed the site, a new map will be
     * created, that map contains a name of the form "Language(Country)" with a
     * key of the form "language-code_country-code". If the local contains
     * variants, the name will look like
     * "Language Variant (Country) and the key will look like "
     * language-code_country-code_variant_code"
     * */
    private static Map<String, Map<String, String>> i18nLanguagesMap = new TreeMap<String, Map<String, String>>();

    private static final Locale[] locales = Locale.getAvailableLocales();
    
    /**
     * TODO
     * */
    public static Map<String, String> getLanguagesMap(Locale locale) {
        if (locale == null)
            locale = Locale.US;

        if (i18nLanguagesMap.containsKey(buildLocaleKey(locale)))
            return i18nLanguagesMap.get(buildLocaleKey(locale));
        else {
            Map<String, String> newLanguageMap = LanguagesMap.buildLanguageMap(locale);
            i18nLanguagesMap.put(buildLocaleKey(locale), newLanguageMap);
            return newLanguageMap;
        }
    }
    
    /**
     * TODO
     * */
    private static Map<String, String> buildLanguageMap(Locale userLocale) {
        Map<String, String> languagesMap = new TreeMap<String, String>();                         
        
        for(Locale locale : locales){
            //It is ordered backwards to keep it sorted by language and country 
            languagesMap.put(buildLanguageValue(locale, userLocale), buildLanguageKey(locale));
        }
        
        return languagesMap;
    }

    /**
     * TODO
     * */
    public static String buildLanguageValue(Locale locale, Locale userLocale) {
        String variant = locale.getVariant();
        String displayVariant = locale.getDisplayVariant(userLocale);        
        String language = WordUtils.capitalize(locale.getDisplayLanguage(userLocale));
        
        if (StringUtils.isEmpty(variant))
            if(StringUtils.isEmpty(locale.getDisplayCountry(userLocale)))
                return language;
            else
                return language + " (" + locale.getDisplayCountry(userLocale) + ')';
        else 
            if(StringUtils.isEmpty(locale.getDisplayCountry(userLocale)))
                return language + ' ' + displayVariant;
            else
                return language + ' ' + displayVariant + " (" + locale.getDisplayCountry(userLocale) + ')';
    }

    /**
     * TODO
     * */
    public static String buildLanguageKey(Locale locale) {
        String variant = locale.getVariant();
        if (StringUtils.isEmpty(variant))
            if(StringUtils.isEmpty(locale.getCountry()))
                return locale.getLanguage();
            else
                return locale.getLanguage() + '_' + locale.getCountry();
        else 
            if(StringUtils.isEmpty(locale.getCountry()))
                return locale.getLanguage() + '_' + variant;
            else
                return locale.getLanguage() + '_' + locale.getCountry() + '_' + variant;
    }
    
    /**
     * TODO
     * */
    private static String buildLocaleKey(Locale locale){
        if(StringUtils.isEmpty(locale.getVariant()))
            return locale.getLanguage() + '-' + locale.getCountry();
        else
            return locale.getLanguage() + '-' + locale.getCountry() + '-' + locale.getVariant();
    }
}
