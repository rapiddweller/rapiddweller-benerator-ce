package com.rapiddweller.platform.ftl;

import com.rapiddweller.common.exception.IllegalArgumentError;
import freemarker.template.SimpleDate;
import freemarker.template.TemplateModel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * The type Date sum method test.
 */
public class DateSumMethodTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testExec() {
    DateSumMethod dateSumMethod = new DateSumMethod();
    TemplateModel actualExecResult = dateSumMethod.exec(new ArrayList<>());
    int actualDateType = ((SimpleDate) actualExecResult).getDateType();
    assertEquals("1970-01-01", actualExecResult.toString());
    assertEquals(2, actualDateType);
  }

  @Test
  public void testExec2() {
    ArrayList<Object> objectList = new ArrayList<>();
    objectList.add("e");
    thrown.expect(IllegalArgumentError.class);
    (new DateSumMethod()).exec(objectList);
  }

  @Test
  public void testExec3() {
    ArrayList<Object> objectList = new ArrayList<>();
    objectList.add(null);
    objectList.add("e");
    thrown.expect(IllegalArgumentError.class);
    (new DateSumMethod()).exec(objectList);
  }

  @Test
  public void testExec4() {
    ArrayList<Object> objectList = new ArrayList<>();
    objectList.add(null);
    TemplateModel actualExecResult = (new DateSumMethod()).exec(objectList);
    int actualDateType = ((SimpleDate) actualExecResult).getDateType();
    assertEquals("1970-01-01", actualExecResult.toString());
    assertEquals(2, actualDateType);
  }

}

