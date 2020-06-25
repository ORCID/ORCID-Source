package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * The base of all JPA entities for the ORCID JPA entities. The reason for this
 * is to force the implementor to use a {@link Serializable} as an id and to add
 * functionality to all classes , avoiding any code duplication
 * <p/>
 * orcid-entities - Dec 6, 2011 - BaseEntity
 * 
 * @author Declan Newman (declan)
 */
@MappedSuperclass
public abstract class BaseEntity<T extends Serializable> implements OrcidEntity<T> {

    private static final long serialVersionUID = 2949008720309076230L;
    private Date dateCreated;
    private Date lastModified;

    /**
     * The date that this entity was created.
     * 
     * @return the dateCreated
     */
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }    

    void setDateCreated(Date date) {
        this.dateCreated = date;
    }
    
    /**
     * The date that this entity was last updated. This will be the same as the
     * {@link #getDateCreated} only on the initial creation
     * 
     * @return the lastModified
     */
    @Column(name = "last_modified")
    public Date getLastModified() {
        return lastModified;
    }

    void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    /**
     * Package protected method that is called by the {@link EntityManager}
     * before update. This uses the {@link PreUpdate} annotations
     */
    @PreUpdate
    void preUpdate() {
        lastModified = new Date();
    }

    /**
     * Package protected method that is called by the {@link EntityManager}
     * before persist. This uses the {@link PrePersist} annotations
     */
    @PrePersist
    void prePersist() {
        Date now = new Date();
        dateCreated = now;
        lastModified = now;
    }
    
    public static <I extends Serializable, E extends OrcidEntity<I>> Map<I, E> mapById(Collection<E> entities) {
        Map<I, E> map = new HashMap<I, E>(entities.size());
        for (E entity : entities) {
            map.put(entity.getId(), entity);
        }
        return map;
    }

}
