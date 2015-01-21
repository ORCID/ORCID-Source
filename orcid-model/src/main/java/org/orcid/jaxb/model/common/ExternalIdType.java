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
package org.orcid.jaxb.model.common;

import javax.xml.bind.annotation.XmlEnum;

/**
 * 
 * @author Will Simpson
 *
 */
@XmlEnum
public enum ExternalIdType {

    AGR, ARXIV, ASIN, ASIN_TLD, BIBCODE, CBA, CIT, CTX, DOI, EID, ETHOS, HANDLE, HIR, ISBN, ISSN, JFM, JSTOR, LCCN, MR, OCLC, OL, OSTI, OTHER_ID, PAT, PMC, PMID, RFC, SOURCE_WORK_ID, SSRN, URI, URN, WOSUID, ZBL;

}
