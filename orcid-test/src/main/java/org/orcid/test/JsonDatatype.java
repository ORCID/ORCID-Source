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
