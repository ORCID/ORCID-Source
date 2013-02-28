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

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "keyword_view")
public class KeywordViewEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;
    private String keyword;
    private int occurences;

    public KeywordViewEntity() {
    }

    public KeywordViewEntity(String keyword) {
        this.keyword = keyword;
    }

    @Override
    @Transient
    public String getId() {
        return keyword;
    }

    @Id
    @Column(name = "keyword", length = 255)
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Column(name = "occurrences")
    public int getOccurences() {
        return occurences;
    }

    public void setOccurences(int occurences) {
        this.occurences = occurences;
    }

}
