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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@Table(name="notification_activity")
public class NotificationActivityEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;
    
    private Long id;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "notification_activity_seq")
    @SequenceGenerator(name = "notification_activity_seq", sequenceName = "notification_activity_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
