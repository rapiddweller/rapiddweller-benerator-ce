package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGobject;

import javassist.*;

import java.io.IOException;

/**
 * create classes to handle postgres custom type
  */
public class PGcustomtype {

    private String newClassName;

    private ClassPool classPool;

    public PGcustomtype(String className) {
        this.classPool = ClassPool.getDefault();
        this.newClassName = className;
    }

    public Class<?> generateClass(String type, boolean isTypeArray) {

        Class<?> existClass = null;
        try {
            existClass = Class.forName(newClassName);
        } catch (ClassNotFoundException e) {
            // do nothing
        }

        if (existClass == null) {
            try {
                CtClass newClass;
                if (isTypeArray) {
                    newClass = generateClassFromPGArrayObject(type);
                } else {
                    newClass = generateClassFromPGobject(type);
                }

                // Write the generated class to disk
                newClass.writeFile("com/rapiddweller/platform/db/postgres/template");
                return newClass.toClass();
            } catch (IOException | CannotCompileException | NotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return existClass;
        }
    }

    public CtClass generateClassFromPGobject(String type) throws CannotCompileException, NotFoundException {

        CtClass newClass = classPool.makeClass(newClassName, classPool.get(PGobject.class.getName()));

        // Add constructor
        CtConstructor constructor = new CtConstructor(
                new CtClass[]{classPool.get(Object.class.getName())},
                newClass);

        String body = String.format("{super(); this.setType(\"%s\"); this.setValue($1.toString());}", type);
        constructor.setBody(body);
        newClass.addConstructor(constructor);

        return newClass;
    }

    public CtClass generateClassFromPGArrayObject(String type) throws CannotCompileException, NotFoundException {

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