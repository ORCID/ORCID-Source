package org.orcid.persistence.jpa.entities;

/**
 * Interface to indicate that an entity contains the owning profile entity (and
 * therefore can be used to update the last modified date of the profile).
 * 
 * @author Will Simpson
 * 
 */
public interface ProfileAware {

    ProfileEntity getProfile();

}
