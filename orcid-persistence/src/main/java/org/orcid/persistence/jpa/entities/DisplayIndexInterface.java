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
