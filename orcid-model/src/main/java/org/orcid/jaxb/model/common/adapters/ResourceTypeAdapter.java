package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.ResourceType;

public class ResourceTypeAdapter extends XmlAdapter<String, ResourceType> {

    @Override
    public ResourceType unmarshal(String v) throws Exception {
        try {
            return ResourceType.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(ResourceType.class, v);
        }
    }

    @Override
    public String marshal(ResourceType v) throws Exception {
        try {
            return v.name();
        } catch (Exception e) {
            throw new IllegalEnumValueException(ResourceType.class, String.valueOf(v));
        }
    }
}
