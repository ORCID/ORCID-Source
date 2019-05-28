package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "invalid_issn_group_id_record")
public class InvalidIssnGroupIdRecordEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String notes;
    
    private Long id;

    @Override
    @Id
    @Column
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Column
    public String getNotes() {
        return notes;
    }

    public void setNotes(String note) {
        this.notes = note;
    }
    
}
