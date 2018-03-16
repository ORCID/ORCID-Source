package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "invalid_record_data_changes")
public class InvalidRecordDataChangeEntity extends BaseEntity<Long>{
    private static final long serialVersionUID = -4409462299700027159L;
    private Long id;
    private String sqlUsedToUpdate;
    private String description;
    private Long numChanged;
    private String type;
   
    @Id
    @Override
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "invalid_record_change_seq")
    @SequenceGenerator(name = "invalid_record_change_seq", sequenceName = "invalid_record_change_seq")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "sql_used_to_update")
    public String getSqlUsedToUpdate() {
        return sqlUsedToUpdate;
    }

    public void setSqlUsedToUpdate(String sqlUsedToUpdate) {
        this.sqlUsedToUpdate = sqlUsedToUpdate;
    }

    @Column(name = "description")    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "num_changed")
    public Long getNumChanged() {
        return numChanged;
    }

    public void setNumChanged(Long numChanged) {
        this.numChanged = numChanged;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    } 
}
