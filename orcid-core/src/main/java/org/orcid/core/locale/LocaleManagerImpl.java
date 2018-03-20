package org.orcid.core.locale;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.manager.CountryManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.utils.FunctionsOverCollections;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.UTF8Control;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class LocaleManagerImpl implements LocaleManager {

    private MessageSource messageSource;

    @Resource
    protected CountryManager countryManager;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    @Override
    public Locale getLocaleFromOrcidProfile(OrcidProfile orcidProfile) {
        Locale locale = null;
        if (orcidProfile.getOrcidPreferences() != null && orcidProfile.getOrcidPreferences().getLocale() != null) {
            orcidProfile.getOrcidPreferences().getLocale().value();
            locale = LocaleUtils.toLocale(orcidProfile.getOrcidPreferences().getLocale().value());
        } else {
            locale = LocaleUtils.toLocale("en");
        }
        return locale;
    }

    @Override
    public String resolveMessage(String messageCode, Object... messageParams) {
        return messageSource.getMessage(messageCode, messageParams, getLocale());
    }

    @Override
    public String resolveMessage(String messageCode, Locale locale, Object... messageParams) {
        return messageSource.getMessage(messageCode, messageParams, locale);
    }
    
    @Cacheable(value = "locale-messages", key = "#locale.toString().concat('-javascript')")
    public org.orcid.pojo.Local getJavascriptMessages(Locale locale) {
        org.orcid.pojo.Local lPojo = new org.orcid.pojo.Local();
        lPojo.setLocale(locale.toString());

        ResourceBundle resource = ResourceBundle.getBundle("i18n/javascript", locale, new UTF8Control());
        lPojo.setMessages(OrcidStringUtils.resourceBundleToMap(resource));

        return lPojo;
    }

    /*
     * Get country names from i18n files
     */
    @Cacheable(value = "locale-messages", key = "#locale.toString().concat('-countries-map')")
    public Map<String, String> getCountries(Locale locale) {
        ResourceBundle resource = ResourceBundle.getBundle("i18n/messages", locale, new UTF8Control());
        Map<String, String> dbCountries = countryManager.retrieveCountriesAndIsoCodes();
        Map<String, String> countries = new LinkedHashMap<String, String>();
        for (String key : dbCountries.keySet()) {
            countries.put(key, resource.getString(buildInternationalizationKey(CountryIsoEntity.class, key)));
        }
        FunctionsOverCollections.sortMapsByValues(countries);
        return countries;
    }

    @SuppressWarnings("rawtypes")
    public static String buildInternationalizationKey(Class theClass, String key) {
        return theClass.getName() + '.' + key;
    }

}
