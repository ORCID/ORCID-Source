/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.orcid.jaxb.model.notification.amended.AmendedSection;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@DiscriminatorValue("AMENDED")
public class NotificationAmendedEntity extends NotificationEntity {

    private static final long serialVersionUID = 1L;
    private AmendedSection amendedSection = AmendedSection.UNKNOWN;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "amended_section")
    public AmendedSection getAmendedSection() {
        return amendedSection;
    }
    public void setAmendedSection(AmendedSection amendedSection) {
        this.amendedSection = amendedSection;
    }

}
