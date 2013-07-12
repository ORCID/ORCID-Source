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
    es("Español", "Spanish", new Locale("xes")), // change to es when we go live
    fr("Français", "French", new Locale("xfr")), // change to fr when when go live
    zh_TW("中国传统", "Traditional Chinese", new Locale("xzh_TW")), // change to zh_TW when we go live
    zh_CH("简体中文版","Simplified Chinese", new Locale("xzh_CN")); // change to xzh_CN when we go live
    
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
