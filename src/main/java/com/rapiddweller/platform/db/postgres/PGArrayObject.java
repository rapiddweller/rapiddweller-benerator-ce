package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * This class using as model for generate class
 * that help writing array data to postgres
 */
public class PGArrayObject extends PGobject {

    private Object objectValue;
    public PGArrayObject(String str) throws SQLException {
        setValue(str);
    }

    public PGArrayObject(Object[] objects) throws SQLException { StringBuilder str = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            String obj = "\"" + String.valueOf(objects[i]) + "\""; // each array element should have surrounded by ""
            if(i==0){
                str.append(obj);
            } else {
                str.append("," + obj);
            }
        }
        setValue(String.format("{%s}", str)); // string surrounded by {} tell PGobject that this value is ARRAY
    }

    public Object getObjectValue(){
        return objectValue;
    }
}
