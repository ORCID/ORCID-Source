package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author Daniel Palafox
 *
 */
@Entity
@Table(name = "event")
public class EventEntity extends BaseEntity<Long> implements OrcidAware {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String orcid;
    private String eventType;
    private String clientId;
    private String redirectUrl;
    private String label;
    private String publicPage;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "event_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "orcid")
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "event_type")
    public String getEventType() { return eventType; }

    public void setEventType(String eventType) { this.eventType = eventType; }

    @Column(name = "client_id")
    public String getClientId() { return clientId; }

    public void setClientId(String client_id) { this.clientId = client_id; }

    @Column(name = "redirect_url")
    public String getRedirectUrl() { return redirectUrl; }

    public void setRedirectUrl(String redirect_url) { this.redirectUrl = redirect_url; }

    @Column(name = "label")
    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    @Column(name = "public_page")
    public String getPublicPage() { return publicPage; }

    public void setPublicPage(String public_page) { this.publicPage = public_page; }
}
