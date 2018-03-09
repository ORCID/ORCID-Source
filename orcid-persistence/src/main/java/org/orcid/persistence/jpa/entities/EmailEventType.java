package org.orcid.persistence.jpa.entities;

/**
 * 
 * @author Will Simpson
 * 
 */
public enum EmailEventType {
    
    VERIFY_EMAIL_7_DAYS_SENT,
    VERIFY_EMAIL_7_DAYS_SENT_SKIPPED, /* we are going to skip notifying email address that where already in the system before launching this */
    VERIFY_EMAIL_TOO_OLD
    ;
}
