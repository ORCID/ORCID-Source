package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "member_chosen_org_disambiguated")
public class MemberChosenOrgDisambiguatedEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    private String sourceId;
    
    private String sourceType;
    
    private OrgDisambiguatedEntity orgDisambiguatedEntity;
    
    public MemberChosenOrgDisambiguatedEntity() {
    } 
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "member_chosen_org_disambiguated_seq")
    @SequenceGenerator(name = "member_chosen_org_disambiguated_seq", sequenceName = "member_chosen_org_disambiguated_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "source_id")
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(name = "source_type")
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(updatable = false, insertable = false, name = "source_id", referencedColumnName = "source_id"),
        @JoinColumn(updatable = false, insertable = false, name = "source_type", referencedColumnName = "source_type") })
    public OrgDisambiguatedEntity getOrgDisambiguatedEntity() {
        return orgDisambiguatedEntity;
    }

    public void setOrgDisambiguatedEntity(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        this.orgDisambiguatedEntity = orgDisambiguatedEntity;
    }

}
