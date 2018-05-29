package org.orcid.jaxb.model.v3.rc1.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType
public class TransientNonEmptyString implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String value;
    private final boolean transientValue = true;

    public TransientNonEmptyString() {

    }

    public TransientNonEmptyString(String value) {
        this.value = value;
    }

    @XmlAttribute
    public boolean getTransient() {
        return transientValue;
    }

    public void setTransient(boolean value) {
        // nothing
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String number) {
        this.value = number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (transientValue ? 1231 : 1237);
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransientNonEmptyString other = (TransientNonEmptyString) obj;
        if (transientValue != other.transientValue)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TransientNonEmptyString [value=" + value + ", transientValue=" + transientValue + "]";
    }

}
