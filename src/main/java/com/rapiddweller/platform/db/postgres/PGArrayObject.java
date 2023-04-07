package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PGArrayObject extends PGobject {

    private Object objectValue;
    public PGArrayObject(String str) throws SQLException {
        setValue(String.format("{%s}",str));
    }

    public PGArrayObject(Object[] objects) throws SQLException {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            if(i==0){
                str.append(objects[i]);
            } else {
                str.append(","+objects[i]);
            }
        }
        List<Object> arr = Arrays.asList(objects);
        String value = arr.toString().replaceAll("[\\[\\]]", "");
        setValue(String.format("{%s}",value));
    }

    public Object getObjectValue(){
        return objectValue;
    }
}
