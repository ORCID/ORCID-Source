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
package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.BiographyManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.record.Biography;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface BiographyManager extends BiographyManagerReadOnly {
    boolean updateBiography(String orcid, Biography bio);

    void createBiography(String orcid, Biography bio);
}
