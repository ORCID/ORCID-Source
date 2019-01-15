package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "validated_public_profile")
public class ValidatedPublicProfileEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean valid;
    
    private String error;
    
    private String orcid;
    
    @Override
    @Id
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }

    public void setId(String orcid) {
        this.orcid = orcid;
    }

    @Column
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @Column
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
