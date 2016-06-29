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
package org.orcid.core.utils;

import org.orcid.persistence.jpa.entities.DisplayIndexInterface;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class DisplayIndexCalculatorHelper {
    public static void setDisplayIndexOnNewEntity(DisplayIndexInterface newEntity, boolean isApiRequest) {
        if(isApiRequest) {
            newEntity.setDisplayIndex(0L);
        } else {
            newEntity.setDisplayIndex(1L);
        }
    }
}
