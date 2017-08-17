package org.orcid.persistence.jpa.entities.decoupled;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.persistence.jpa.entities.DisplayIndexInterface;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;

@Entity
@Table(name = "work")
public class DecoupledWorkEntity extends WorkBaseEntity implements DisplayIndexInterface {
    /**
     * 
     */
    private static final long serialVersionUID = -6768274763176017890L;
    protected String citation;
    protected Iso3166Country iso2Country;
    protected CitationType citationType;
    protected String contributorsJson;
    protected Date addedToProfileDate;

    @Column(name = "citation", length = 5000)
    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "citation_type", length = 100)
    public CitationType getCitationType() {
        return citationType;
    }

    public void setCitationType(CitationType citationType) {
        this.citationType = citationType;
    }

    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "iso2_country", length = 2)
    public Iso3166Country getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(Iso3166Country iso2Country) {
        this.iso2Country = iso2Country;
    }

    @Column(name = "added_to_profile_date")
    public Date getAddedToProfileDate() {
        return addedToProfileDate;
    }

    public void setAddedToProfileDate(Date addedToProfileDate) {
        this.addedToProfileDate = addedToProfileDate;
    }
}
