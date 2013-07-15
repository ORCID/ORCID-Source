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
package org.orcid.core.locale;

import java.util.Locale;

public enum LocaleEnum {
    en("English", "English", new Locale("en")),
    es("Español", "Spanish", new Locale("es")),
    fr("Français", "French", new Locale("fr")),
    zh_TW("繁體中文", "Traditional Chinese", new Locale("zh_TW")),
    zh_CH("简体中文","Simplified Chinese", new Locale("zh_CN"));
    
    private String displayName;
    private String displayName_en;
    private Locale locale;
    
    
    private LocaleEnum(String displayName, String displayName_en, Locale locale) {
        this.displayName = displayName;
        this.displayName_en = displayName_en;
        this.locale= locale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayName_en() {
        return displayName_en;
    }

    public Locale getLocale() {
        return locale;
    }

}
