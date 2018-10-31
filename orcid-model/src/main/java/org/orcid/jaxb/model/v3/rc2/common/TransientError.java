package org.orcid.jaxb.model.v3.rc2.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "errorCode", "errorMessage"})
public class TransientError implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "error-code", namespace = "http://www.orcid.org/ns/common", required = true)
    private String errorCode;
    @XmlElement(name = "error-message", namespace = "http://www.orcid.org/ns/common", required = true)
    private String errorMessage;
    @XmlAttribute(name = "transient", required = true)
    private final boolean transientValue = true;

    public TransientError() {

    }

    public TransientError(String code, String message) {
        setErrorCode(code);
        setErrorMessage(message);
    }

    public boolean getTransient() {
        return transientValue;
    }

    public void setTransient(boolean value) {
        // nothing
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String code) {
        this.errorCode = code;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
        result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
        result = prime * result + (transientValue ? 1231 : 1237);
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
        TransientError other = (TransientError) obj;
        if (errorCode == null) {
            if (other.errorCode != null)
                return false;
        } else if (!errorCode.equals(other.errorCode))
            return false;
        if (errorMessage == null) {
            if (other.errorMessage != null)
                return false;
        } else if (!errorMessage.equals(other.errorMessage))
            return false;
        if (transientValue != other.transientValue)
            return false;
        return true;
    }


}
