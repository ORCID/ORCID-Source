package org.orcid.core.manager;

/**
 * Interface to generate a password (can be used for reset or simply as a way to create a user password without them providing one at registration)
 * Should return a password that conforms to the constants defined by  
 * @See 
 * @author jamesb
 *
 */
public interface PasswordGenerationManager {

    char[] createNewPassword();

}
