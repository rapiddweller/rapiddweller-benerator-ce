package com.rapiddweller.platform;

import org.junit.Assert;
import org.junit.Test;

public class ABCTestTest extends ABCTest {


  @Test
  public void testSetUpABC() {
    this.setUpABC();
    Assert.assertEquals("Person[name=Alice, age=23, notes=]", this.aliceEntity.toString());
    Assert.assertEquals("Person[name=Bob, age=34, notes=[null]]", this.bobEntity.toString());
    Assert.assertEquals("Car[maker=Audi]", this.carEntity.toString());
    Assert.assertEquals("Person[name=Charly, age=45, notes=[null], car=[maker=Audi]]", this.charlyEntity.toString());
  }

}