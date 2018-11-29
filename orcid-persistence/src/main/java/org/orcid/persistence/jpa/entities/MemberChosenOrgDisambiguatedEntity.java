package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "member_chosen_org_disambiguated")
public class MemberChosenOrgDisambiguatedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orgDisambiguatedId;
    
    @Id
    @Column(name = "org_disambiguated_id")
    public Long getOrgDisambiguatedId() {
        return orgDisambiguatedId;
    }

    public void setOrgDisambiguatedId(Long orgDisambiguatedId) {
        this.orgDisambiguatedId = orgDisambiguatedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MemberChosenOrgDisambiguatedEntity that = (MemberChosenOrgDisambiguatedEntity) o;

        if (!orgDisambiguatedId.equals(that.orgDisambiguatedId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 * orgDisambiguatedId.hashCode();
        return result;
    }

}
