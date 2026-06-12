package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 
 * @author Will Simpson
 * 
 */
@Entity
@Table(name = "subject")
public class SubjectEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;
    private String name;

    public SubjectEntity() {
    }

    public SubjectEntity(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Transient
    public String getId() {
        return name;
    }

}
