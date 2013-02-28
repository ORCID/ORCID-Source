/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * orcid-entities - Dec 6, 2011 - AddressEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "address")
public class AddressEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = -1688297948072822159L;

    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateOrProvince;
    private String country;
    private String postalCode;

    /**
     * The id to be used to identify the address. This uses
     * {@link GenerationType#SEQUENCE} to set
     * 
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    /**
     * Set id to be used to identify the address. You do not call this from your
     * class unless for testing purposes as the underlying JPA implementation
     * should set this for you
     * 
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Line one of your address. E.g. 14 Butterfly Cottage
     * 
     * @return the addressLine1
     */
    @Column(name = "address_line_1", length = 350)
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Set line one of your address. E.g. Butterfly Cottage
     * 
     * @param addressLine1
     *            the addressLine1 to set
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Line two of your address. E.g. 14 College Road
     * 
     * @return the addressLine2
     */
    @Column(name = "address_line_2", length = 350)
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Set Line two of your address. E.g. 14 College Road
     * 
     * @param addressLine2
     *            the addressLine2 to set
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * The city or town (or village) E.g. Brighton
     * 
     * @return the city
     */
    @Column(name = "city", length = 150)
    public String getCity() {
        return city;
    }

    /**
     * Sets the city or town (or village) E.g. Brighton
     * 
     * @param city
     *            the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * The state or province of the address. E.g. East Sussex
     * 
     * @return the stateOrProvince
     */
    @Column(name = "state_or_province", length = 150)
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * Sets the state or province of the address. E.g. East Sussex
     * 
     * @param stateOrProvince
     *            the stateOrProvince to set
     */
    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    /**
     * The country that the address is in. E.g. United Kingdom
     * 
     * @return the country
     */
    @Column(name = "country", length = 100)
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country that the address is in. E.g. United Kingdom
     * 
     * @param country
     *            the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * The post or zip code for the address. E.g. BN1 3FE
     * 
     * @return the postalCode
     */
    @Column(name = "postal_code", length = 15)
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the post or zip code for the address. E.g. BN1 3FE
     * 
     * @param postalCode
     *            the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
