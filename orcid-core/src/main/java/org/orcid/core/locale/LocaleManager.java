package org.orcid.core.locale;

import java.util.Locale;
import java.util.Map;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.Local;

public interface LocaleManager {

    /**
     * @return The currently active locale
     */
    Locale getLocale();

    Locale getLocaleFromOrcidProfile(OrcidProfile orcidProfile);

    /**
     * @param messageCode
     *            The code of the message in the messages properties file
     * @param messageParams
     *            Values to use in {} placeholders in the message
     * @return The localized message (using the locale for the current thread)
     */
    String resolveMessage(String messageCode, Object... messageParams);

    /**
     * @param messageCode
     *            The code of the message in the messages properties file
     * @param locale
     *          The locale we want the message on           
     * @param messageParams
     *            Values to use in {} placeholders in the message
     * @return The localized message (using the locale for the current thread)
     */
    String resolveMessage(String messageCode, Locale locale, Object... messageParams);
    
    public Local getJavascriptMessages(Locale locale);

    public Map<String, String> getCountries(Locale locale);

}
