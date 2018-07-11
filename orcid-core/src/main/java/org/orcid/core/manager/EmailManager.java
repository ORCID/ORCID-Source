package org.orcid.core.manager;

import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Emails;


/**
 * 
 * @author Will Simpson
 *
 */
public interface EmailManager extends EmailManagerReadOnly {

    void updateEmails(HttpServletRequest request, String orcid, Emails emails);

    void removeEmail(String orcid, String email);

    void removeEmail(String orcid, String email, boolean removeIfPrimary);        

    void addSourceToEmail(String email, String sourceId);
    
    boolean verifyEmail(String email);
    
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
}
