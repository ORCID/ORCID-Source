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
package org.orcid.core.adapter.impl.jsonidentifiers;

import java.io.Serializable;

public interface JSONIdentifierAdapter<T, S> {

    public String toDBJSONString();

    public T toMessagePojo();

    public S toRecordPojo();

    public class Url implements Serializable {
        private static final long serialVersionUID = 1L;
        public String value;

        public Url() {
        }

        public Url(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
