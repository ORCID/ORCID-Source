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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.Iso3166Country;

/**
 * 
 * @author Will Simpson
 * 
 */

@Entity
@Table(name = "org")
public class OrgEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String city;
    private String region;
    private Iso3166Country country;
    private String url;
    private ProfileEntity source;
    private OrgDisambiguatedEntity orgDisambiguated;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "org_seq")
    @SequenceGenerator(name = "org_seq", sequenceName = "org_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String resolveName() {
        if (orgDisambiguated == null) {
            return name;
        }
        return orgDisambiguated.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public String resolveCity() {
        if (orgDisambiguated == null) {
            return city;
        }
        return orgDisambiguated.getCity();
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public String resolveRegion() {
        if (orgDisambiguated == null) {
            return region;
        }
        return orgDisambiguated.getRegion();
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    public Iso3166Country getCountry() {
        return country;
    }

    public Iso3166Country resolveCountry() {
        if (orgDisambiguated == null) {
            return country;
        }
        return orgDisambiguated.getCountry();
    }

    public void setCountry(Iso3166Country country) {
        this.country = country;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @ManyToOne
    @JoinColumn(name = "source_id")
    public ProfileEntity getSource() {
        return source;
    }

    public void setSource(ProfileEntity source) {
        this.source = source;
    }

    @ManyToOne
    @JoinColumn(name = "org_disambiguated_id")
    public OrgDisambiguatedEntity getOrgDisambiguated() {
        return orgDisambiguated;
    }

    public void setOrgDisambiguated(OrgDisambiguatedEntity orgDisambiguated) {
        this.orgDisambiguated = orgDisambiguated;
    }

}
