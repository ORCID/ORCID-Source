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