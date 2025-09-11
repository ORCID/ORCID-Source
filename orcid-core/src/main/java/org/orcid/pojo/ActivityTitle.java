package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.Date;

public class ActivityTitle {

    private long putCode;
    private String title;
    private boolean isDefault;
    private int featuredDisplayIndex;
    private boolean isPublic;
    private String publicationYear;
    private String publicationMonth;
    private String publicationDay;
    private String workType;
    private String journalTitle;

    public long getPutCode() {
        return putCode;
    }

    public void setPutCode(long putCode) {
        this.putCode = putCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getPublicationDay() {
        return publicationDay;
    }

    public void setPublicationDay(String publicationDay) {
        this.publicationDay = publicationDay;
    }

    public String getPublicationMonth() {
        return publicationMonth;
    }

    public void setPublicationMonth(String publicationMonth) {
        this.publicationMonth = publicationMonth;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public int getFeaturedDisplayIndex() {
        return featuredDisplayIndex;
    }

    public void setFeaturedDisplayIndex(int featuredDisplayIndex) {
        this.featuredDisplayIndex= featuredDisplayIndex;
    }
}
