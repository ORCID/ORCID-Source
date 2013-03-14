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

@Table(name = "reference_data")
@Entity
public class RefDataEntity extends BaseEntity<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = -6639759571886764413L;
    private Integer id;
    private String refDataEntityKey;
    private String refDataEntityValue;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "reference_data_seq")
    @SequenceGenerator(name = "reference_data_seq", sequenceName = "reference_data_seq")
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    @Column(name = "ref_data_key", length = 255)
    public String getRefDataEntityKey() {
        return refDataEntityKey;
    }

    @Column(name = "ref_data_value", length = 255)
    public String getRefDataEntityValue() {
        return refDataEntityValue;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRefDataEntityKey(String refDataEntityKey) {
        this.refDataEntityKey = refDataEntityKey;
    }

    public void setRefDataEntityValue(String refDataEntityValue) {
        this.refDataEntityValue = refDataEntityValue;
    }

}
