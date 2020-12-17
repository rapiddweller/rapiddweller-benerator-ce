package com.my;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.util.SimpleGenerator;

public class NumberGenerator extends SimpleGenerator<Long> {

    private long n;

    public NumberGenerator() {
        this(0);
    }

    public NumberGenerator(long initialValue) {
        n = initialValue;
    }

    public Long generate() throws IllegalGeneratorStateException {
        return n++;
    }

    public Class<Long> getGeneratedType() {
        return Long.class;
    }

}
