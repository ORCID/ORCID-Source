package org.orcid.internal.util;

import javax.xml.bind.annotation.XmlElement;

import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class ClientInfo {
    @XmlElement(name = "client_id")
    private String id;
    @XmlElement(name = "client_name")
    private String name;

    public ClientInfo() {
        
    }
    
    public ClientInfo(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ClientInfo fromClient(Client client) {
        ClientInfo response = new ClientInfo();
        if(!PojoUtil.isEmpty(client.getClientId())) {
            response.setId(client.getClientId().getValue());
        }
        
        if(!PojoUtil.isEmpty(client.getDisplayName())){
            response.setName(client.getDisplayName().getValue());
        }
        return response;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        ClientInfo other = (ClientInfo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }        
}
