package org.orcid.utils.alerting;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SlackManager {

    void sendSystemAlert(String message);

    void sendAlert(String message, String customChannel, String from);
    
    void sendAlert(String message, String customChannel, String from, String webhookUrl);

}
