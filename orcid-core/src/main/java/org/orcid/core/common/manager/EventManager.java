package org.orcid.core.common.manager;

import org.orcid.core.utils.EventType;
import org.orcid.pojo.ajaxForm.RequestInfoForm;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface EventManager {

    boolean removeEvents(String orcid);

    void createEvent(String orcid, EventType eventType, HttpServletRequest request);

}
