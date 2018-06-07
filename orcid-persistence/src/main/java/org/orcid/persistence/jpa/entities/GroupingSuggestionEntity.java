package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "grouping_suggestion")
public class GroupingSuggestionEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    
    private String orcid;

    private String workPutCodesJson;
    
    private Date dismissedDate;
    
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "profile_history_event_seq")
    @SequenceGenerator(name = "profile_history_event_seq", sequenceName = "profile_history_event_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
    @Column(name = "work_put_codes_json")
    public String getWorkPutCodesJson() {
        return workPutCodesJson;
    }

    public void setWorkPutCodesJson(String workPutCodesJson) {
        this.workPutCodesJson = workPutCodesJson;
    }

    @Column(name = "dismissed_date")
    public Date getDismissedDate() {
        return dismissedDate;
    }

    public void setDismissedDate(Date dismissedDate) {
        this.dismissedDate = dismissedDate;
    }
    
}
