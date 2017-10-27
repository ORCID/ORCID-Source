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
package org.orcid.jaxb.model.v3.dev1.common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType
public class TransientNonEmptyString {
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

    public void setTransient() {
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
