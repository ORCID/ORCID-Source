package org.orcid.jaxb.model.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.orcid.jaxb.model.common.WorkType;

public class WorkTypeAdapter extends XmlAdapter<String, WorkType> {

    @Override
    public WorkType unmarshal(String v) throws Exception {
        try {
            return WorkType.fromValue(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalEnumValueException(WorkType.class, v);
        }
    }

    @Override
    public String marshal(WorkType v) throws Exception {
        try {
            return v.value();
        } catch (Exception e) {
            throw new IllegalEnumValueException(WorkType.class, String.valueOf(v));
        }  
    }
}
