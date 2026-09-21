package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rejected_grouping_suggestion")
public class RejectedGroupingSuggestionEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orcid;

    private String putCodes;
    
    @Id
    @Column(name = "put_codes")
    public String getId() {
        return putCodes;
    }

    public void setId(String putCodes) {
        this.putCodes = putCodes;
    }

    @Column
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
}
