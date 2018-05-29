package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
