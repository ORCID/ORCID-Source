package org.orcid.core.manager;

import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Member;

public interface MembersManager {

    Member createMember(Member newMember);
    
    Member updateMemeber(Member member);
    
    Member getMember(String memberId);      
    
    Client getClient(String clientId);
    
    Client updateClient(Client client);

}
