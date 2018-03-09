package org.orcid.listener.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "record_status")
public class RecordStatusEntity {
    private Date dateCreated;
    private Date lastModified;
    private String orcid;
    private Integer dumpStatus12Api = 0;
    private Integer dumpStatus20Api = 0;
    private Integer dumpStatus20ActivitiesApi = 0;
    private Integer solrStatus20Api = 0;
    private Date lastIndexedDump12Api;
    private Date lastIndexedDump20Api;
    private Date lastIndexedDump20ActivitiesApi;
    private Date lastIndexedSolr20Api;
    
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

    @Column(name = "api_1_2_dump_status")
    public Integer getDumpStatus12Api() {
        return dumpStatus12Api;
    }

    public void setDumpStatus12Api(Integer dumpStatus12Api) {
        this.dumpStatus12Api = dumpStatus12Api;
    }

    @Column(name = "api_2_0_dump_status")
    public Integer getDumpStatus20Api() {
        return dumpStatus20Api;
    }

    public void setDumpStatus20Api(Integer dumpStatus20Api) {
        this.dumpStatus20Api = dumpStatus20Api;
    }

    @Column(name = "api_2_0_solr_status")
    public Integer getSolrStatus20Api() {
        return solrStatus20Api;
    }

    public void setSolrStatus20Api(Integer solrStatus20Api) {
        this.solrStatus20Api = solrStatus20Api;
    }

    @Column(name = "api_2_0_activities_dump_status")
    public Integer getDumpStatus20ActivitiesApi() {
        return dumpStatus20ActivitiesApi;
    }

    public void setDumpStatus20ActivitiesApi(Integer dumpStatus20ActivitiesApi) {
        this.dumpStatus20ActivitiesApi = dumpStatus20ActivitiesApi;
    }

    @Column(name = "api_1_2_dump_last_indexed")
    public Date getLastIndexedDump12Api() {
        return lastIndexedDump12Api;
    }

    public void setLastIndexedDump12Api(Date lastIndexedDump12Api) {
        this.lastIndexedDump12Api = lastIndexedDump12Api;
    }

    @Column(name = "api_2_0_dump_last_indexed")
    public Date getLastIndexedDump20Api() {
        return lastIndexedDump20Api;
    }

    public void setLastIndexedDump20Api(Date lastIndexedDump20Api) {
        this.lastIndexedDump20Api = lastIndexedDump20Api;
    }

    @Column(name = "api_2_0_activities_dump_last_indexed")
    public Date getLastIndexedDump20ActivitiesApi() {
        return lastIndexedDump20ActivitiesApi;
    }

    public void setLastIndexedDump20ActivitiesApi(Date lastIndexedDump20ActivitiesApi) {
        this.lastIndexedDump20ActivitiesApi = lastIndexedDump20ActivitiesApi;
    }

    @Column(name = "api_2_0_solr_last_indexed")
    public Date getLastIndexedSolr20Api() {
        return lastIndexedSolr20Api;
    }

    public void setLastIndexedSolr20Api(Date lastIndexedSolr20Api) {
        this.lastIndexedSolr20Api = lastIndexedSolr20Api;
    }        
}
