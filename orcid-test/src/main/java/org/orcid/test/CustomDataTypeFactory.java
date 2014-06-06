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

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CustomDataTypeFactory extends HsqldbDataTypeFactory {

    private static final JsonDatatype JSON_DATATYPE = new JsonDatatype();

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if ("json".equals(sqlTypeName)) {
            return JSON_DATATYPE;
        }
        DataType dt = super.createDataType(sqlType, sqlTypeName);
        return dt;
    }

}
