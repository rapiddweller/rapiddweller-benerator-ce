package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGobject;

import javassist.*;

import java.io.IOException;

/**
 * create classes will generate handle class
 * which help writing special data type to postgres
  */
public class PGcustomtype extends ClassLoader {

    private String newClassName;

    private ClassPool classPool;

    public PGcustomtype(String className) {
        this.classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(this.getClass()));
        this.newClassName = className;
    }

    public Class<?> generateClass(String type, boolean isTypeArray) {

        // use newClassName class if already exist
        Class<?> existClass = null;
        try {
            existClass = Class.forName(newClassName);
        } catch (ClassNotFoundException e) {
            existClass = null;
        }

        if (existClass == null) {
            try {
                // check newClassName class exist again with classPool
                CtClass oldClass = classPool.getOrNull(newClassName);
                if (oldClass !=null) {
                    byte[] b = oldClass.toBytecode();
                    return defineClass(newClassName, b, 0, b.length);
                }

                CtClass newClass;
                if (isTypeArray) {
                    newClass = generateClassFromPGArrayObject(type);
                } else {
                    newClass = generateClassFromPGobject(type);
                }
                // Write the generated temporary class to disk for correction
//                newClass.writeFile("com/rapiddweller/platform/db/postgres/template");
                return newClass.toClass(this.getClass().getClassLoader(), getClass().getProtectionDomain());
            } catch (CannotCompileException | NotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return existClass;
        }
    }

    private CtClass generateClassFromPGobject(String type) throws CannotCompileException, NotFoundException {

        CtClass newClass = classPool.makeClass(newClassName, classPool.get(PGobject.class.getName()));

        // Add constructor
        CtConstructor constructor = new CtConstructor(
                new CtClass[]{classPool.get(Object.class.getName())},
                newClass);

        String body = String.format("{super(); this.setType(\"%s\"); this.setValue(String.valueOf($1));}", type);
        constructor.setBody(body);
        newClass.addConstructor(constructor);

        return newClass;
    }

    private CtClass generateClassFromPGArrayObject(String type) throws CannotCompileException, NotFoundException {

        CtClass newClass = classPool.makeClass(newClassName, classPool.get(PGArrayObject.class.getName()));

        // Add constructor
        CtConstructor[] parentConstructors = classPool.get(PGArrayObject.class.getName()).getConstructors();
        for (CtConstructor ct : parentConstructors){
            CtConstructor constructor = new CtConstructor(
                    ct.getParameterTypes(),
                    newClass);

            String body = String.format("{super($1); this.setType(\"%s\"); }", type);
            constructor.setBody(body);
            newClass.addConstructor(constructor);
        }
        return newClass;

    }
}