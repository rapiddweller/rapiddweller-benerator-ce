package com.rapiddweller.platform.db.postgres;

import junit.framework.TestCase;
import org.junit.Test;
import org.postgresql.util.PGobject;

import java.lang.reflect.InvocationTargetException;

public class PGcustomtypeTest extends TestCase {

    @Test
    public void testGenerateInstanceOfPGobject() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PGcustomtype pGcustomtype = new PGcustomtype("NotArray");
        Class<?> myClass = pGcustomtype.generateClass("INT", false);
        var obj = myClass.getConstructor(Object.class).newInstance("some values");
        assertTrue(obj instanceof PGobject);
    }

    @Test
    public void testGenerateInstanceOfPGArrayObject() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PGcustomtype pGcustomtype = new PGcustomtype("IsArray");
        Class<?> myClass = pGcustomtype.generateClass("_INT", true);
        Integer[] arr = new Integer[]{1,2,3,4,5};
        var obj = myClass.getConstructor(Object[].class).newInstance((Object) arr);
        assertTrue(obj instanceof PGArrayObject);
    }

}