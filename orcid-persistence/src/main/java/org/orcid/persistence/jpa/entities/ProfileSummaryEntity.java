package org.orcid.persistence.jpa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */
@Entity
@Table(name = "profile")
public class ProfileSummaryEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;
    private String orcid;
    private RecordNameEntity recordNameEntity;

    public ProfileSummaryEntity() {
        super();
    }

    public ProfileSummaryEntity(String orcid) {
        super();
        this.orcid = orcid;
    }

    @Id
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }

    public void setId(String orcid) {
        this.orcid = orcid;
    }

    @OneToOne(mappedBy = "profile", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})    
    public RecordNameEntity getRecordNameEntity() {
        return recordNameEntity;
    }

    public void setRecordNameEntity(RecordNameEntity recordNameEntity) {
        this.recordNameEntity = recordNameEntity;
    }

}
