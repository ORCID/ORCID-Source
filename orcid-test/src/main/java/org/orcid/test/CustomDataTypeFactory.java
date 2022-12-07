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
    private static final TimestampWithTimezoneDataType TIMESTAMP_WITH_TIMEZONE_DATATYPE = new TimestampWithTimezoneDataType();

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if ("json".equals(sqlTypeName)) {
            return JSON_DATATYPE;
        } else if ("TIMESTAMP WITH TIME ZONE".equals(sqlTypeName)) {
            return TIMESTAMP_WITH_TIMEZONE_DATATYPE;
        }
        DataType dt = super.createDataType(sqlType, sqlTypeName);
        return dt;
    }

}
