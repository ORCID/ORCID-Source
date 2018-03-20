package org.orcid.persistence.jpa.entities;

/**
 * 
 * @author Will Simpson
 *
 */
public interface ActionableNotificationEntity {

    String getAuthorizationUrl();

    ProfileEntity getProfile();

}
