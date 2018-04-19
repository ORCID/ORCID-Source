package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "email_frequency")
public class EmailFrequencyEntity extends BaseEntity<String> {
    private static final long serialVersionUID = 7517492246345512743L;
    private String id;
    private String orcid;
    private Float sendChangeNotifications;
    private Float sendAdministrativeChangeNotifications;
    private Float sendMemberUpdateRequests;
    private Boolean sendQuarterlyTips = Boolean.FALSE;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "orcid", updatable = false, insertable = true)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "send_change_notifications")
    public Float getSendChangeNotifications() {
        return sendChangeNotifications;
    }

    public void setSendChangeNotifications(Float sendChangeNotifications) {
        this.sendChangeNotifications = sendChangeNotifications;
    }

    @Column(name = "send_administrative_change_notifications")
    public Float getSendAdministrativeChangeNotifications() {
        return sendAdministrativeChangeNotifications;
    }

    public void setSendAdministrativeChangeNotifications(Float sendAdministrativeChangeNotifications) {
        this.sendAdministrativeChangeNotifications = sendAdministrativeChangeNotifications;
    }

    @Column(name = "send_member_update_requests")
    public Float getSendMemberUpdateRequests() {
        return sendMemberUpdateRequests;
    }

    public void setSendMemberUpdateRequests(Float sendMemberUpdateRequests) {
        this.sendMemberUpdateRequests = sendMemberUpdateRequests;
    }

    @Column(name = "send_quarterly_tips")
    public Boolean getSendQuarterlyTips() {
        return sendQuarterlyTips;
    }

    public void setSendQuarterlyTips(Boolean sendQuarterlyTips) {
        this.sendQuarterlyTips = sendQuarterlyTips;
    }
}
