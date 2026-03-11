package org.orcid.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "notification")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type")
// @formatter:off
@NamedNativeQueries({
    @NamedNativeQuery(
        name = NotificationEntity.FIND_ORCIDS_WITH_UNSENT_NOTIFICATIONS_ON_EMAIL_FREQUENCIES_TABLE, 
        query = "SELECT DISTINCT p.orcid, COALESCE(p.completed_date, p.date_created) " +
                " FROM profile p " +
                " JOIN notification n ON p.orcid = n.orcid " +
                " JOIN email_frequency f ON p.orcid = f.orcid " +
                " WHERE n.sent_date IS NULL " +
                " AND n.archived_date IS NULL " +
                " AND n.date_created > now() - INTERVAL '4' MONTH " + 
                " AND p.claimed = true " +
                " AND p.profile_deactivation_date IS NULL " +  
                " AND p.primary_record IS NULL " +  
                " AND p.record_locked = false " +
                " AND (" +
                "   (n.notification_type IN ('ADMINISTRATIVE', 'CUSTOM') AND f.send_administrative_change_notifications < :never) " + 
                "   OR (n.notification_type = 'AMENDED' AND f.send_change_notifications < :never) " +
                "   OR (n.notification_type IN ('PERMISSION', 'INSTITUTIONAL_CONNECTION') AND f.send_member_update_requests < :never)" + 
                " ) ORDER BY p.orcid;"
    ),
    @NamedNativeQuery(
        name = NotificationEntity.FIND_NOTIFICATIONS_TO_SEND_BY_ORCID,
        query = "WITH stats AS (SELECT MAX(sent_date) AS max_sent_date FROM notification WHERE orcid = :orcid) " +
                "SELECT n.* FROM notification n " +
                "CROSS JOIN stats x " +
                "WHERE n.orcid = :orcid " +
                "AND n.sent_date IS NULL " +
                "AND (" +
                "  unix_timestamp(:effective_date) > (unix_timestamp(x.max_sent_date) + (:record_email_frequency * 86400)) " +
                "  OR (x.max_sent_date IS NULL AND unix_timestamp(:effective_date) > (unix_timestamp(:record_active_date) + (:record_email_frequency * 86400))) " +
                ");", 
        resultClass = NotificationEntity.class
    )
})
// @formatter:on
abstract public class NotificationEntity extends SourceAwareEntity<Long> implements OrcidAware {

    public static final String FIND_ORCIDS_WITH_UNSENT_NOTIFICATIONS = "findOrcidsWithUnsentNotifications";
    
    public static final String FIND_ORCIDS_WITH_UNSENT_NOTIFICATIONS_ON_EMAIL_FREQUENCIES_TABLE = "findOrcidsWithUnsentNotificationsOnEmailFrequenciesTable";
    
    public static final String FIND_NOTIFICATIONS_TO_SEND_BY_ORCID = "findNotificationsToSendByOrcid";
    
    private static final long serialVersionUID = 1L;

    private Long id;
    private String orcid;
    private String notificationType;
    private String notificationSubject;
    private String notificationIntro;
    private Date sentDate;
    private Date readDate;
    private Date archivedDate;
    private Date actionedDate;    
    private boolean sendable;
    private String notificationFamily;
    private Long retryCount;
    
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "notification_seq")
    @SequenceGenerator(name = "notification_seq", sequenceName = "notification_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "orcid", nullable = false)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "notification_type", insertable = false, updatable = false)
    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    @Column(name = "sent_date")
    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Column(name = "read_date")
    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    @Column(name = "archived_date")
    public Date getArchivedDate() {
        return archivedDate;
    }

    public void setArchivedDate(Date archivedDate) {
        this.archivedDate = archivedDate;
    }

    public boolean isSendable() {
        return sendable;
    }

    public void setSendable(boolean sendable) {
        this.sendable = sendable;
    }

    @Column(name = "actioned_date")
    public Date getActionedDate() {
        return actionedDate;
    }

    public void setActionedDate(Date actionedDate) {
        this.actionedDate = actionedDate;
    }

    @Column(name = "notification_subject")
    public String getNotificationSubject() {
        return notificationSubject;
    }

    public void setNotificationSubject(String notificationSubject) {
        this.notificationSubject = notificationSubject;
    }

    @Column(name = "notification_intro")
    public String getNotificationIntro() {
        return notificationIntro;
    }

    public void setNotificationIntro(String notificationIntro) {
        this.notificationIntro = notificationIntro;
    }

    @Column(name = "notification_family")
    public String getNotificationFamily() {
        return notificationFamily;
    }

    public void setNotificationFamily(String notificationFamily) {
        this.notificationFamily = notificationFamily;
    }

    @Column(name = "retry_count")
    public Long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Long retryCount) {
        this.retryCount = retryCount;
    }            
}