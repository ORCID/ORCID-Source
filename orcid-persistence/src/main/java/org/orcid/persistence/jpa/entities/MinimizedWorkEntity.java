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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * An object that will contain the minimum work information needed to display
 * work in the UI.
 * 
 * @author Angel Montenegro (amontenegro)
 */
@Entity
@Table(name = "work")
public class MinimizedWorkEntity extends WorkBaseEntity {
    
    private static final long serialVersionUID = 1L;

}