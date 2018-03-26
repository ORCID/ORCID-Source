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
