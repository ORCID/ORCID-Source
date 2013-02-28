/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.SequenceType;

/**
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "work_contributor")
public class WorkContributorEntity extends BaseContributorEntity {

    private static final long serialVersionUID = -371826957062237679L;
    private Long id;
    private WorkEntity work;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "work_contributor_seq")
    @SequenceGenerator(name = "work_contributor_seq", sequenceName = "work_contributor_seq")
    @Column(name = "work_contributor_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "work_id", nullable = false)
    public WorkEntity getWork() {
        return work;
    }

    public void setWork(WorkEntity work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkContributorEntity)) {
            return false;
        }

        WorkContributorEntity that = (WorkContributorEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (work != null ? !work.equals(that.work) : that.work != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (work != null ? work.hashCode() : 0);
        return result;
    }
}
