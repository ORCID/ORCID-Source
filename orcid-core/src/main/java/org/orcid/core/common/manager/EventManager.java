package org.orcid.core.common.manager;

import javax.servlet.http.HttpServletRequest;

import org.orcid.persistence.jpa.entities.EventType;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface EventManager {

    void createEvent(EventType eventType, HttpServletRequest request);

    void createPapiEvent(String clientId, String ip, boolean anonymous);

}
