/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.io.Serializable;
import java.util.Date;

/**
 * <p/>
 * A base interface for all entities to implement to enable a loose coupling and
 * allow a greater use of generics
 * <p/>
 * orcid-entities - Dec 6, 2011 - Entity
 * 
 * @author Declan Newman (declan)
 */

public interface OrcidEntity<T> extends Serializable {

    /**
     * This should be implemented by all entity classes to return the id of the
     * entity represented by the &lt;T&gt; generic argument
     * 
     * @return the id of the entity
     */
    T getId();

    /**
     * @return When was the entity created
     */
    Date getDateCreated();

    /**
     * @return When was the entity last updated
     */
    Date getLastModified();

}
