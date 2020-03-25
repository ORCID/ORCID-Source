package org.orcid.jaxb.model.v3.release.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"value"})
@XmlRootElement(name = "reported-date", namespace = "http://www.orcid.org/ns/common")
@ApiModel(value = "ReportedDateV3_0")
public class ReportedDate implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @XmlValue
    @XmlSchemaType(name = "dateTime")
    @ApiModelProperty(readOnly = true)
    protected XMLGregorianCalendar value;

    public ReportedDate() {
    }

    public ReportedDate(XMLGregorianCalendar value) {
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setValue(XMLGregorianCalendar value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ReportedDate that = (ReportedDate) o;

        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    public boolean after(LastModifiedDate other) {
        if (this.value == null) {
            return false;
        }

        if (other == null || other.getValue() == null) {
            return true;
        }

        return other.getValue().compare(this.value) < 0;
    }

    @Override
    public String toString() {
        String result = new String();
        result += value.getYear();
        result += "-" + (value.getMonth() < 10 ? "0" + value.getMonth() : value.getMonth());
        result += "-" + (value.getDay() < 10 ? "0" + value.getDay() : value.getDay());
        return result;
    }
}
