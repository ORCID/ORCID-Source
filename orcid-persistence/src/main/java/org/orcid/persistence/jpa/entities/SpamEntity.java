package org.orcid.persistence.jpa.entities;

import java.util.Date;

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