package org.orcid.jaxb.model.common.adapters;

import java.util.HashMap;
import java.util.Map;

public class IllegalEnumValueException extends RuntimeException {
    private static final long serialVersionUID = 8244547823214423277L;
    Class enumClass;
    String invalidValue;

    public IllegalEnumValueException(Class enumClass, String invalidValue) {
        this.enumClass = enumClass;
        this.invalidValue = invalidValue;
    }

    public Class getEnumClass() {
        return enumClass;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public Map<String, String> getParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("value", invalidValue);
        params.put("enum", enumClass.getName());
        return params;
    }
}
