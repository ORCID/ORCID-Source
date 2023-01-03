package org.orcid.listener.persistence.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_engine_record_status")
public class SearchEngineRecordStatusEntity {
    private String orcid;
    private Date dateCreated;
    private Date lastModified;
    private Integer solrStatus;
    private Date solrLastIndexed;

    
    @Id
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }

    public void setId(String orcid) {
        this.orcid = orcid;
    }
    
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "last_modified")
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Column(name = "solr_status")
    public Integer getSolrStatus() {
        return solrStatus;
    }

    public void setSolrStatus(Integer solrStatus) {
        this.solrStatus = solrStatus;
    }

    @Column(name = "solr_last_indexed")
    public Date getSolrLastIndexed() {
        return solrLastIndexed;
    }

    public void setSolrLastIndexed(Date solrLastIndexed) {
        this.solrLastIndexed = solrLastIndexed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateCreated, lastModified, orcid, solrLastIndexed, solrStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchEngineRecordStatusEntity other = (SearchEngineRecordStatusEntity) obj;
        return Objects.equals(dateCreated, other.dateCreated) && Objects.equals(lastModified, other.lastModified) && Objects.equals(orcid, other.orcid)
                && Objects.equals(solrLastIndexed, other.solrLastIndexed) && Objects.equals(solrStatus, other.solrStatus);
    }

}
