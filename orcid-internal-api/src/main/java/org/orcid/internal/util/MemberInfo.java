package org.orcid.internal.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class MemberInfo {
    @XmlElement(name = "member_id")
    private String id;
    @XmlElement(name = "member_name")
    private String name;
    @XmlElement(name = "clients")
    private List<ClientInfo> clients;

    public MemberInfo() {
        
    }
    
    public MemberInfo(String id, String name) {
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

    public List<ClientInfo> getClients() {
        return clients;
    }

    public void setClients(List<ClientInfo> clients) {
        this.clients = clients;
    }
    
    public static MemberInfo fromMember(Member member) {
        MemberInfo response = new MemberInfo();
        if(!PojoUtil.isEmpty(member.getGroupName())) {
            response.setName(member.getGroupName().getValue());
        }
        
        if(!PojoUtil.isEmpty(member.getGroupOrcid())) {
            response.setId(member.getGroupOrcid().getValue());
        }
        
        if(member.getClients() != null) {
            List<ClientInfo> clients = new ArrayList<ClientInfo>();
            for(Client client : member.getClients()){
                clients.add(ClientInfo.fromClient(client));
            }
            response.setClients(clients);
        }
        return response;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clients == null) ? 0 : clients.hashCode());
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
        MemberInfo other = (MemberInfo) obj;
        if (clients == null) {
            if (other.clients != null)
                return false;
        } else if (!clients.equals(other.clients))
            return false;
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
