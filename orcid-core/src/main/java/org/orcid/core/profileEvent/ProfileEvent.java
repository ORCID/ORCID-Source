package org.orcid.core.profileEvent;

import java.util.List;
import java.util.concurrent.Callable;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.springframework.context.ApplicationContext;

public interface ProfileEvent extends Callable<ProfileEventType> {
    
    public List <ProfileEventType> outcomes();
    
}
