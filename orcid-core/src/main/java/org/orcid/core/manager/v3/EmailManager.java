package org.orcid.core.manager.v3;

import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;


/**
 * 
 * @author Will Simpson
 *
 */
public interface EmailManager extends EmailManagerReadOnly {

    void updateEmails(HttpServletRequest request, String orcid, Emails emails);

    void addEmail(HttpServletRequest request, String orcid, Email email);
    
    void removeEmail(String orcid, String email);        
    
    boolean verifyEmail(String email, String orcid);
    
    boolean verifyPrimaryEmail(String orcid);
    
    boolean moveEmailToOtherAccount(String email, String origin, String destination);
    
    boolean verifySetCurrentAndPrimary(String orcid, String email);

    /***
     * Indicates if the given email address could be auto deprecated given the
     * ORCID rules. See
     * https://trello.com/c/ouHyr0mp/3144-implement-new-auto-deprecate-workflow-
     * for-members-unclaimed-ids
     * 
     * @param email
     *            Email address
     * @return true if the email exists in a non claimed record and the
     *         client source of the record allows auto deprecating records
     */
    boolean isAutoDeprecateEnableForEmail(String email);

    boolean hideAllEmails(String orcid);

    boolean updateVisibility(String orcid, String email, Visibility visibility);
    
    void setPrimary(String orcid, String email, HttpServletRequest request);
    
    void reactivatePrimaryEmail(String orcid, String email, String hash);
    
    Integer clearEmailsAfterReactivation(String orcid);
    
    void reactivateOrCreate(String orcid, String email, String emailHash, Visibility visibility);
}
