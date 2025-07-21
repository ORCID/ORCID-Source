package org.orcid.core.manager.v3;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;


/**
 * 
 * @author Will Simpson
 *
 */
public interface EmailManager extends EmailManagerReadOnly {
    public static final String FILTERED_EMAIL = "filtered_email";
    public static final String HASH = "hash";

    /**
     * TODO: Returns a map with the old primary email and the new primary email
     * when the primary email is verified, this because the email sender is on
     * the orcid-web project until we finish migrating the jersey libs, so, the
     * calling function should know that the primary email have changed
     */
    Map<String, String> addEmail(String orcid, Email email);
    
    void removeEmail(String orcid, String email);        
    
    boolean verifyEmail(String orcid, String email);
    
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
    
    /**
     * TODO: Returns a map with the new primary email and the old primary email
     * when the primary email is updated, this because the email sender is on
     * the orcid-web project until we finish migrating the jersey libs, so, the
     * calling function should know that the primary email have changed
     */
    Map<String, String> setPrimary(String orcid, String email, HttpServletRequest request);
    
    void reactivatePrimaryEmail(String orcid, String email);
    
    Integer clearEmailsAfterReactivation(String orcid);
    
    /**
     * Reactivates or creates an email address
     * @param orcid
     * @param email
     * @param visibility
     * 
     * @return true if the email is new or it exists but have not been verified
     * */
    boolean reactivateOrCreate(String orcid, String email, Visibility visibility);

    /**
     * TODO: Returns a map with the new primary email and the old primary email
     * when the primary email is updated, this because the email sender is on
     * the orcid-web project until we finish migrating the jersey libs, so, the
     * calling function should know that the primary email have changed
     */
    Map<String, String> editEmail(String orcid, String original, String edited, HttpServletRequest request);

    Map<String, String> getEmailKeys(String email);

    void removeUnclaimedEmail(String orcid, String emailAddress);

    List<String> removeEmails(String orcid, List<String> emailsToRemove);
}
