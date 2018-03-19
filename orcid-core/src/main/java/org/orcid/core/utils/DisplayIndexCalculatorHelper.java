package org.orcid.core.utils;

import org.orcid.persistence.jpa.entities.DisplayIndexInterface;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class DisplayIndexCalculatorHelper {
    public static void setDisplayIndexOnNewEntity(DisplayIndexInterface newEntity, boolean isApiRequest) {
        if(isApiRequest) {
            newEntity.setDisplayIndex(0L);
        } else {
            newEntity.setDisplayIndex(1L);
        }
    }
}
