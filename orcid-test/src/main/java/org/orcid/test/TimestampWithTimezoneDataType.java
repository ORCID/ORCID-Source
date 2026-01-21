package org.orcid.test;

import java.sql.Timestamp;
import java.sql.Types;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;

public class TimestampWithTimezoneDataType extends AbstractDataType {

    public TimestampWithTimezoneDataType(String name, int sqlType, Class<?> classType, boolean isNumber) {
        super(name, sqlType, classType, isNumber);
    }

    public TimestampWithTimezoneDataType() {
        super("TIMESTAMP WITH TIME ZONE", Types.TIMESTAMP_WITH_TIMEZONE, String.class, false);
    }

    @Override
    public Object typeCast(Object value) throws TypeCastException {
        if (value == null || value == ITable.NO_VALUE) {
            return null;
        }
        System.out.println((String) value);
        return Timestamp.valueOf((String) value);
    }

}
