package org.orcid.core.manager;

import java.util.Collection;

import org.orcid.jaxb.model.message.Email;

/**
 * 
 * @author Will Simpson
 *
 */
public interface EmailManager {

    boolean emailExists(String email);

    void updateEmails(String orcid, Collection<Email> emails);

    void addEmail(String orcid, Email email);

    void removeEmail(String orcid, String email);

}
