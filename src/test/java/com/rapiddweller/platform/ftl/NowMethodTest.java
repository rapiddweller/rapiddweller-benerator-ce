package com.rapiddweller.platform.ftl;

import static org.junit.Assert.assertEquals;

import freemarker.template.SimpleDate;

import java.util.ArrayList;

import org.junit.Test;

public class NowMethodTest {
    @Test
    public void testExec() {
        NowMethod nowMethod = new NowMethod();
        assertEquals(2, ((SimpleDate) nowMethod.exec(new ArrayList<Object>())).getDateType());
    }
}

