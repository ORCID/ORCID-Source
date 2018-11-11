package org.orcid.jaxb.model.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonValue;

@XmlType(name = "locale")
@XmlEnum
public enum AvailableLocales {
    @XmlEnumValue("ar")
    AR("ar"),
    @XmlEnumValue("en")
    EN("en"),
    @XmlEnumValue("es")
    ES("es"),
    @XmlEnumValue("fr")
    FR("fr"),
    @XmlEnumValue("it")
    IT("it"),
    @XmlEnumValue("ja")
    JA("ja"),
    @XmlEnumValue("ko")
    KO("ko"),
    @XmlEnumValue("pt")
    PT("pt"),
    @XmlEnumValue("ru")
    RU("ru"),
    @XmlEnumValue("zh_CN")
    ZH_CN("zh_CN"),
    @XmlEnumValue("zh_TW")
    ZH_TW("zh_TW"),
    @XmlEnumValue("xx")
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
        // if we don't support the specified language return english
        return AvailableLocales.EN;
    }

    @Override
    public String toString() {
        return value;
    }
}
