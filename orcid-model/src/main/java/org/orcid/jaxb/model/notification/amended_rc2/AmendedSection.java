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
package org.orcid.jaxb.model.notification.amended_rc2;

import javax.xml.bind.annotation.XmlEnum;

/**
 * 
 * @author Will Simpson
 *
 */
@XmlEnum
public enum AmendedSection {
    AFFILIATION, BIO, EDUCATION, EMPLOYMENT, EXTERNAL_IDENTIFIERS, FUNDING, PEER_REVIEW, PREFERENCES, WORK, UNKNOWN;
}
