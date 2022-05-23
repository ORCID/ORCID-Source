package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "work")
public class MinimizedExtendedWorkEntity extends WorkBaseEntity implements OrcidAware {

    private static final long serialVersionUID = 1L;

    protected String orcid;
    protected String contributorsJson;

    @Column(name = "orcid", updatable = false, insertable = true)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

}