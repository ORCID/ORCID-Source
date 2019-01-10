package org.orcid.core.utils;

import org.orcid.persistence.jpa.entities.DisplayIndexInterface;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class DisplayIndexCalculatorHelper {
    public static void setDisplayIndexOnNewEntity(DisplayIndexInterface newEntity, boolean isApiRequest) {
        setDisplayIndex(newEntity, isApiRequest);
    }
    
    public static void setDisplayIndexOnExistingEntity(DisplayIndexInterface entity, boolean isApiRequest) {
        if(entity.getDisplayIndex() == null) {
            setDisplayIndex(entity, isApiRequest);
        }
    }
    
    private static void setDisplayIndex(DisplayIndexInterface e, boolean isApiRequest) {
        if(isApiRequest) {
            e.setDisplayIndex(0L);
        } else {
            e.setDisplayIndex(1L);
        }
    }
}
