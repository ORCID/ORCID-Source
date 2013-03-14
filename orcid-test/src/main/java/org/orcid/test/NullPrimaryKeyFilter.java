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
package org.orcid.test;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.filter.IColumnFilter;

public class NullPrimaryKeyFilter implements IColumnFilter {

    private String pseudoKey = null;

    NullPrimaryKeyFilter(String pseudoKey) {
        this.pseudoKey = pseudoKey;
    }

    public boolean accept(String tableName, Column column) {
        return column.getColumnName().equalsIgnoreCase(pseudoKey);
    }

}
