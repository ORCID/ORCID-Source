package org.orcid.persistence.jpa.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.Visibility;

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
    protected Visibility visibility;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "work_seq")
    @SequenceGenerator(name = "work_seq", sequenceName = "work_seq")
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

    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

}