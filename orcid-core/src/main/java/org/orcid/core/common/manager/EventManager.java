package org.orcid.core.common.manager;

import javax.servlet.http.HttpServletRequest;

import org.orcid.core.utils.EventType;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface EventManager {

    void createEvent(String orcid, EventType eventType, HttpServletRequest request);

}
