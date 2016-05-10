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
package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "identifier_type")
public class IdentifierTypeEntity extends BaseEntity<Long>{

    private static final long serialVersionUID = 1L;

    private Long id;
    private String id_name;
    private String id_validation_regex;
    private String id_resolution_prefix;
    private Boolean id_deprecated = Boolean.FALSE;
    
    private ClientDetailsEntity sourceClient;
    
    @Column(name = "id_name")
    public String getName() {
        return id_name;
    }

    public void setName(String id_name) {
        this.id_name = id_name;
    }

    @Column(name = "id_validation_regex")
    public String getValidationRegex() {
        return id_validation_regex;
    }

    public void setValidationRegex(String id_validation_regex) {
        this.id_validation_regex = id_validation_regex;
    }

    @Column(name = "id_resolution_prefix")
    public String getResolutionPrefix() {
        return id_resolution_prefix;
    }

    public void setResolutionPrefix(String id_resolution_prefix) {
        this.id_resolution_prefix = id_resolution_prefix;
    }

    @Column(name = "id_deprecated")
    public Boolean getIsDeprecated() {
        return id_deprecated;
    }

    public void setIsDeprecated(Boolean id_deprecated) {
        this.id_deprecated = id_deprecated;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "identifier_type_seq")
    @SequenceGenerator(name = "identifier_type_seq", sequenceName = "identifier_type_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_source_id")
    public ClientDetailsEntity getSourceClient() {
        return sourceClient;
    }

    public void setSourceClient(ClientDetailsEntity sourceClient) {
        this.sourceClient = sourceClient;
    }

}
