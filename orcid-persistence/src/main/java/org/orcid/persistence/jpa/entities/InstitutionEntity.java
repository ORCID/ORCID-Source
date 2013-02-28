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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

/**
 * <p/>
 * Holds details about an institution. This is a straightforward container with
 * information about the institution that can be used for affiliates, primary
 * and previous work places
 * <p/>
 * orcid-entities - Dec 6, 2011 - InstitutionEntity
 * 
 * @author Declan Newman (declan)
 */

@Entity
@Table(name = "institution")
public class InstitutionEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = -1043337280106705869L;

    private Long id;
    private String name;
    private AddressEntity address;

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "institution_seq")
    @SequenceGenerator(name = "institution_seq", sequenceName = "institution_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    @Column(name = "institution_name", length = 350)
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The address that corresponds to this institution
     * 
     * @return the address
     */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    public AddressEntity getAddress() {
        return address;
    }

    /**
     * Sets the address that corresponds to this institution
     * 
     * @param address
     *            the address to set
     */
    public void setAddress(AddressEntity address) {
        this.address = address;
    }

}
