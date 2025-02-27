package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
@Embeddable
public class ClientResourceIdPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Column(name = "client_details_id")
    private String clientId;
    @Column(name = "resource_id")
    private String resourceId;

    public ClientResourceIdPk() {
    }

    public ClientResourceIdPk(String clientId, String resourceId) {
        this.clientId = clientId;
        this.resourceId = resourceId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientResourceIdPk that = (ClientResourceIdPk) o;

        if (!clientId.equals(that.clientId))
            return false;
        if (!resourceId.equals(that.resourceId))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + resourceId.hashCode();
        return result;
    }
}
