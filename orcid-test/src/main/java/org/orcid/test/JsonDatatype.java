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

import java.sql.Types;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JsonDatatype extends AbstractDataType {

    public JsonDatatype(String name, int sqlType, Class<?> classType, boolean isNumber) {
        super(name, sqlType, classType, isNumber);
    }

    public JsonDatatype() {
        super("json", Types.OTHER, String.class, false);
    }

    @Override
    public Object typeCast(Object value) throws TypeCastException {
        if (value == null || value == ITable.NO_VALUE) {
            return null;
        }
        return value.toString();
    }

}
