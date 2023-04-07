package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGobject;

import javassist.*;

import java.io.IOException;

public class PGcustomtype {

    private String newClassName;

    public PGcustomtype(String className) {
        this.newClassName = className;
    }

    // create classes to handle postgres custom class
    public Class<?> generateNewClass(String type){

        Class<?> existClass = null;

        try {
            existClass = Class.forName(newClassName);
        } catch (ClassNotFoundException e) {
            // do nothing
        }

        if (existClass == null) {
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass newClass = classPool.makeClass(newClassName, classPool.get(PGobject.class.getName()));

                // Add constructor
                CtConstructor constructor = new CtConstructor(
                        new CtClass[]{classPool.get(Object.class.getName())},
                        newClass);

                String body = String.format("{super(); this.setType(\"%s\"); this.setValue($1.toString());}", type);
                constructor.setBody(body);
                newClass.addConstructor(constructor);

                // Write the generated class to disk
                newClass.writeFile("com/rapiddweller/platform/db/postgres/template");
                return newClass.toClass();

            } catch (NotFoundException | IOException | CannotCompileException e) {
                throw new RuntimeException(e);
            }

        } else {
            return existClass;
        }
    }
}