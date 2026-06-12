package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.orcid.persistence.jpa.entities.keys.FindMyStuffHistoryEntityPk;

@Entity
@Table(name = "find_my_stuff_history")
@IdClass(FindMyStuffHistoryEntityPk.class)
public class FindMyStuffHistoryEntity extends BaseEntity<FindMyStuffHistoryEntityPk>{
    
    private static final long serialVersionUID = 1L;
    private Boolean optOut;
    private String finderName;
    private long lastCount;
    private String orcid;
    private Boolean actioned;
    
    @Override
    @Transient
    public FindMyStuffHistoryEntityPk getId() {
        return new FindMyStuffHistoryEntityPk(orcid, finderName);
    }
    
    @Id
    @Column(name = "orcid")
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
    @Id
    @Column(name = "finder_name")
    public String getFinderName() {
        return finderName;
    }
    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }
    
    @Column(name = "opt_out")
    public Boolean getOptOut() {
        return optOut;
    }
    public void setOptOut(Boolean optOut) {
        this.optOut = optOut;
    }
    
    @Column(name = "last_count")
    public long getLastCount() {
        return lastCount;
    }
    public void setLastCount(long lastCount) {
        this.lastCount = lastCount;
    }

    @Column(name = "actioned")
    public Boolean getActioned() {
        return actioned;
    }

    public void setActioned(Boolean actioned) {
        this.actioned = actioned;
    }
    
}