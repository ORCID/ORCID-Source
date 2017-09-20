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
package org.orcid.frontend.spring.i18n;

import java.util.ArrayList;
import java.util.List;

public enum OrcidWebLocale {
    ES("es"), EN("en"), IT("it"), FR("fr"), PT("pt"), ZH_CN("zh", "CN"), ZH_TW("zh", "TW"), JA("ja"), RU("ru"), KO("ko");
    
    private String lang;
    private String country;
    
    OrcidWebLocale(String lang) {
        this.lang = lang;
    }
    
    OrcidWebLocale(String lang, String country) {
        this.lang = lang;
        this.country = country;
    }
    
    public static List<OrcidWebLocale> findByLang(String lang) {
        List<OrcidWebLocale> locales = new ArrayList<OrcidWebLocale>();
        for(OrcidWebLocale locale : OrcidWebLocale.values()) {
            if(locale.lang.equals(lang)) {
                locales.add(locale);
            }
        }
        return locales;
    }
    
    public static OrcidWebLocale find(String lang, String country) {
        for(OrcidWebLocale locale : OrcidWebLocale.values()) {
            if(locale.lang.equals(lang) && locale.country.equals(country)) {
                return locale;
            }
        }
        return null;
    }
}
