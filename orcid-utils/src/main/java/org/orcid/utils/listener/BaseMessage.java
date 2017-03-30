/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.utils.listener;

import java.util.Map;

public abstract class BaseMessage {
    /**
     * immutable map ready for transport
     * 
     */
    public final Map<String, String> map;

    protected BaseMessage(Map<String, String> map) {
        this.map = map;
    }

    public String getOrcid() {
        return map.get(MessageConstants.ORCID.value);
    }

    /**
     * The map that is sent over the wire
     * 
     * @return
     */
    public Map<String, String> getMap() {
        return map;
    }
}
