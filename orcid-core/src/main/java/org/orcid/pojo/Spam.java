package org.orcid.pojo;

import java.io.Serializable;

import org.orcid.pojo.ajaxForm.Date;

public class Spam implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Date createdDate;
    
    private Date lastModifiedDate;

    private Date reportedDate;

    private String sourceType;

    private Integer count;
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Date getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
    public static Spam fromValue(org.orcid.jaxb.model.v3.release.record.Spam spam) {
        if (spam == null)
            return null;
        
        Spam s = new Spam();
              
        s.setCount(spam.getSpamCounter());
        
        s.setSourceType(spam.getSourceType().toString());
        
        s.setReportedDate(Date.valueOf(spam.getReportedDate()));
        
        s.setCreatedDate(Date.valueOf(spam.getCreatedDate()));
        
        s.setLastModifiedDate(Date.valueOf(spam.getLastModifiedDate()));               
        
        return s;
    }

}