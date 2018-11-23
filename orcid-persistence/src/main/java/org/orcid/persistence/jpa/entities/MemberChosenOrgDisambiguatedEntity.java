package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "member_chosen_org_disambiguated")
public class MemberChosenOrgDisambiguatedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
