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
