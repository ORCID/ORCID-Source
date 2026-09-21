package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */
@Entity
@Table(name = "work")
public class WorkLastModifiedEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String orcid;
    private Long displayIndex;
    protected String visibility;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "work_seq")
    @SequenceGenerator(name = "work_seq", sequenceName = "work_seq", allocationSize = 1)
    @Column(name = "work_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "orcid", updatable = false, insertable = false)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "display_index", updatable = false, insertable = false)
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }

    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

}