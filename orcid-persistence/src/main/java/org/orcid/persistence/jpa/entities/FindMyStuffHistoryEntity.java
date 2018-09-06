package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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