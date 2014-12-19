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
package org.orcid.core.manager;

/**
 * 
 * @author Will Simpson
 * 
 */
public enum ValidationBehaviour {

    IGNORE, LOG_INFO, LOG_WARNING, LOG_ERROR, THROW_VALIDATION_EXCEPTION, LOG_INFO_WITH_XML, LOG_WARNING_WITH_XML, LOG_ERROR_WITH_XML;

}
