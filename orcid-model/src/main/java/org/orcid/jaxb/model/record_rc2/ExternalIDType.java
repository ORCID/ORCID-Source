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

package org.orcid.jaxb.model.record_rc2;

import java.io.Serializable;

/**
 * Temporary class - basic enum to help move away from XML enums.

 * 
 */
public enum ExternalIDType implements Serializable {

    // @formatter:off
    
    OTHER_ID("other-id"),
    ASIN("asin"),
    ASIN_TLD("asin-tld"),
    ARXIV("arxiv"),
    BIBCODE("bibcode"),
    DOI("doi"),
    EID("eid"),
    ISBN("isbn"),
    ISSN("issn"),
    JFM("jfm"), JSTOR("jstor"),
    LCCN("lccn"),
    MR("mr"),
    OCLC("oclc"),
    OL("ol"),
    OSTI("osti"),
    PMC("pmc"),
    PMID("pmid"),
    RFC("rfc"),
    SSRN("ssrn"),
    XBL("zbl"),
    AGR("agr"), // Agricola
    CBA("cba"), // Chinese Biological Abstracts
    CIT("cit"), // CiteSeer
    CTX("ctx"), // CiteXplore submission
    ETHOS("ethos"), // EThOS Peristent ID
    HANDLE("handle"),
    HIR("hir"), // NHS Evidence
    PAT("pat"), // Patent number prefixed with country code
    SOURCE_WORK_ID("source-work-id"),
    URI("uri"),
    URN("urn"),
    WOSUID("wosuid"),
    
    GRANT_NUMBER("grant_number");
    
    private final String value;

    ExternalIDType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ExternalIDType fromValue(String value) {
        for (ExternalIDType wit : ExternalIDType.values()) {
            if (wit.value.equals(value)) {
                return wit;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public boolean equals(String stringVersion){
        return (this.value.equalsIgnoreCase(stringVersion));
    }
    
}
