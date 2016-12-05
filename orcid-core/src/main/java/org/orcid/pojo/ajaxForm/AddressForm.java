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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common_rc4.Country;
import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.record_rc4.Address;

public class AddressForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    private Iso2Country iso2Country;
    private String countryName;
    private String putCode;
    private Visibility visibility;
    private Long displayIndex;
    private Date createdDate;
    private Date lastModified;
    private String source;
    private String sourceName;

    public static AddressForm valueOf(Address address) {
        AddressForm form = new AddressForm();

        if (address == null) return form;

        if (address.getCountry() != null && address.getCountry().getValue() != null) {
            form.setIso2Country(Iso2Country.valueOf(address.getCountry().getValue()));
        }

        if (address.getVisibility() != null) {
            form.setVisibility(Visibility.valueOf(address.getVisibility()));
        }

        if (address.getPutCode() != null) {
            form.setPutCode(String.valueOf(address.getPutCode()));
        }

        if (address.getCreatedDate() != null) {
            Date createdDate = new Date();
            createdDate.setYear(String.valueOf(address.getCreatedDate().getValue().getYear()));
            createdDate.setMonth(String.valueOf(address.getCreatedDate().getValue().getMonth()));
            createdDate.setDay(String.valueOf(address.getCreatedDate().getValue().getDay()));
            form.setCreatedDate(createdDate);
        }

        if (address.getLastModifiedDate() != null) {
            Date lastModifiedDate = new Date();
            lastModifiedDate.setYear(String.valueOf(address.getLastModifiedDate().getValue().getYear()));
            lastModifiedDate.setMonth(String.valueOf(address.getLastModifiedDate().getValue().getMonth()));
            lastModifiedDate.setDay(String.valueOf(address.getLastModifiedDate().getValue().getDay()));
            form.setLastModified(lastModifiedDate);
        }

        if (address.getSource() != null) {
            // Set source
            form.setSource(address.getSource().retrieveSourcePath());
            if (address.getSource().getSourceName() != null) {
                form.setSourceName(address.getSource().getSourceName().getContent());
            }
        }
        if (address.getDisplayIndex() != null) {
            form.setDisplayIndex(address.getDisplayIndex());
        } else {
            form.setDisplayIndex(0L);
        }

        return form;
    }

    public Address toAddress() {
        Address address = new Address();

        if (this.iso2Country != null && this.iso2Country.getValue() != null) {
            Country country = new Country();
            country.setValue(this.iso2Country.getValue().value());
            address.setCountry(country);
        }
       
        if (this.visibility != null && this.visibility.getVisibility() != null) {
            address.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.fromValue(this.getVisibility().getVisibility().value()));
        }

        if (!PojoUtil.isEmpty(this.getPutCode())) {
            address.setPutCode(Long.valueOf(this.getPutCode()));
        }

        if (displayIndex != null) {
            address.setDisplayIndex(displayIndex);
        } else {
            address.setDisplayIndex(0L);
        }

        address.setSource(new Source(source));

        return address;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Iso2Country getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(Iso2Country iso2Country) {
        this.iso2Country = iso2Country;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
}
