package org.orcid.jaxb.model.notification.amended_rc4;

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
