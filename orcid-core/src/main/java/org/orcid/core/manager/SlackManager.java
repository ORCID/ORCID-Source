package org.orcid.core.manager;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SlackManager {

    void sendSystemAlert(String message);

    void sendAlert(String message, String customChannel, String from);

}
