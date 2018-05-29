package org.orcid.persistence.jpa.entities;

/**
 * Interface to indicate that an entity contains the owning orcid id
 * 
 */
public interface OrcidAware {

    String getOrcid();

}
