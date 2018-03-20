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
