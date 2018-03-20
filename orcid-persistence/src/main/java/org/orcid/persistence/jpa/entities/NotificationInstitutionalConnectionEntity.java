package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("INSTITUTIONAL_CONNECTION")
public class NotificationInstitutionalConnectionEntity extends NotificationEntity implements ActionableNotificationEntity {

    private static final long serialVersionUID = 1L;

    private String authorizationUrl;
    private String authenticationProviderId;

    @Column(name = "authorization_url")
    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    @Column(name = "authentication_provider_id")
    public String getAuthenticationProviderId() {
        return authenticationProviderId;
    }

    public void setAuthenticationProviderId(String authenticationProviderId) {
        this.authenticationProviderId = authenticationProviderId;
    }

}
