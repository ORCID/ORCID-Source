package org.orcid.pojo.ajaxForm;

import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;

import java.time.LocalDate;
import java.time.ZoneId;

public class ProfileEmailDomain {

    protected String value;

    protected String visibility;

    private Date createdDate;

    private Date lastModified;

    public static ProfileEmailDomain valueOf(ProfileEmailDomainEntity ed) {
        ProfileEmailDomain emailDomain = new ProfileEmailDomain();

        if (ed != null) {
            emailDomain.setValue(ed.getEmailDomain());
            emailDomain.setVisibility(ed.getVisibility());

            if (ed.getDateCreated() != null) {
                Date createdDate = new Date();
                LocalDate date = ed.getDateCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                createdDate.setYear(String.valueOf(date.getYear()));
                createdDate.setMonth(String.valueOf(date.getMonthValue()));
                createdDate.setDay(String.valueOf(date.getDayOfMonth()));
                createdDate.setTimestamp(ed.getDateCreated().toInstant().toEpochMilli());
                emailDomain.setCreatedDate(createdDate);
            }

            if (ed.getLastModified() != null) {
                Date lastModifiedDate = new Date();
                LocalDate date = ed.getLastModified().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                lastModifiedDate.setYear(String.valueOf(date.getYear()));
                lastModifiedDate.setMonth(String.valueOf(date.getMonthValue()));
                lastModifiedDate.setDay(String.valueOf(date.getDayOfMonth()));
                emailDomain.setLastModified(lastModifiedDate);
            }

        }
        return emailDomain;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
