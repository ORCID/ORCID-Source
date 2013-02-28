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
package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Will Simpson
 */
@Embeddable
public class ElectronicResourceNumEntityPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String value;
    private String type;
    private Long work;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getWork() {
        return work;
    }

    public void setWork(Long work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ElectronicResourceNumEntityPk that = (ElectronicResourceNumEntityPk) o;

        if (!type.equals(that.type))
            return false;
        if (!value.equals(that.value))
            return false;
        if (!work.equals(that.work))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + work.hashCode();
        return result;
    }
}
