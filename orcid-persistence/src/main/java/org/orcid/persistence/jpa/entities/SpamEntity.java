package org.orcid.persistence.jpa.entities;

import java.util.Date;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 *
 * @author Daniel Palafox
 *
 */
@Entity
@Table(name = "spam")
public class SpamEntity extends BaseEntity<Long> implements OrcidAware {
    private static final long serialVersionUID = 1L;
    private Long id;    
    private String orcid;
    private SourceType sourceType;
    private Integer spamCounter;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "spam_seq")
    @SequenceGenerator(name = "spam_seq", sequenceName = "spam_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "orcid")
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Column(name = "spam_counter")
    public Integer getSpamCounter() {
        return spamCounter;
    }

    public void setSpamCounter(Integer spamCounter) {
        this.spamCounter = spamCounter;
    }  

    
    
    
}