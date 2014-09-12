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
package org.orcid.persistence.jpa.entities;

/**
 * Interface to indicate that an entity should be sorted, higher the number
 * the more sooner it should be displayed (much link Z-index in html)
 * 
 * @author rcpeters
 * 
 */
public interface DisplayIndexInterface {

    public Long getDisplayIndex();
    
    public void setDisplayIndex(Long displayIndex);

}
