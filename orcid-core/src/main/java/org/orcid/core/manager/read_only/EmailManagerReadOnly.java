package org.orcid.core.manager.read_only;

import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.persistence.jpa.entities.EmailEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface EmailManagerReadOnly extends ManagerReadOnlyBase {

    boolean emailExists(String email);

    String findOrcidIdByEmail(String email);
    
    Map<String, String> findOricdIdsByCommaSeparatedEmails(String email);
    
    Map<String, String> findIdsByEmails(List<String> emailList);
    
    boolean isPrimaryEmailVerified(String orcid);
    
    boolean haveAnyEmailVerified(String orcid);
    
    Emails getEmails(String orcid);
    
    Emails getPublicEmails(String orcid);
    
    boolean isPrimaryEmail(String email);
    
    boolean isPrimaryEmail(String orcid, String email);
    
    EmailEntity findCaseInsensitive(String email);
    
    EmailEntity find(String email);
    
    Email findPrimaryEmail(String orcid);
}
