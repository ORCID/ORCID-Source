package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * @author Will Simpson
 */
public class WebhookEntityPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orcid;
    private String uri;

    public WebhookEntityPk() {
    }

    public WebhookEntityPk(String orcid, String uri) {
        this.orcid = orcid;
        this.uri = uri;
    }

    public String getProfile() {
        return orcid;
    }

    public void setProfile(String orcid) {
        this.orcid = orcid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebhookEntityPk other = (WebhookEntityPk) obj;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }

}
