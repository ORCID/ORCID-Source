package org.orcid.jaxb.model.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * List of available locales in the ORCID registry.
 * 
 * <p>
 * This enum defines the list of available locale in the ORCID registry
 * </p>
 * 
 * <p>
 * Besides of defining the enum value here, the developer need to generate the
 * translation files with the help of transifex, see <a href=
 * "https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/i18n/README.md">README.md</a>
 * and update the list of avaliable locales in <a href=
 * "https://github.com/ORCID/ORCID-Source/blob/master/SUPPORTED_LOCALES.md">SUPPORTED_LOCALES.md</a>.
 * </p>
 */
@XmlType(name = "locale")
@XmlEnum
public enum AvailableLocales {
    AR("ar"), 
    CS("cs"), 
    EN("en"), 
    ES("es"), 
    FR("fr"), 
    IT("it"), 
    JA("ja"), 
    KO("ko"), 
    PT("pt"), 
    RU("ru"), 
    ZH_CN("zh_CN"), 
    ZH_TW("zh_TW"), 
    XX("xx");
    private final String value;

    AvailableLocales(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @JsonValue
    public String jsonValue() {
        return this.name();
    }

    public static AvailableLocales fromValue(String v) {
        for (AvailableLocales c : AvailableLocales.values()) {
            if (v.startsWith(c.value)) {
                return c;
            }
        }
        // if we don't support the specified language return English
        return AvailableLocales.EN;
    }

    @Override
    public String toString() {
        return value;
    }
}
