/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.file;

import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the {@link FileNameGenerator}.<br/><br/>
 * Created: 29.09.2021 09:09:54
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class FileNameGeneratorTest extends GeneratorTest {

  @Test
  public void testFilesAndFolders() {
    FileNameGenerator g = new FileNameGenerator("src/main/java", null, FileNameGenerator.PathType.local, true, true, true);
    initialize(g);
    assertEquals(String.class, g.getGeneratedType());
    List<String> products = generate(g, 10000);
    assertTrue(products.contains("rapiddweller"));
    assertTrue(products.contains("Benerator.java"));
    assertTrue(products.contains("Generator.java"));
  }

  @Test
  public void testFoldersOnly() {
    FileNameGenerator g = new FileNameGenerator("src/main/java", null, FileNameGenerator.PathType.local, true, false, true);
    initialize(g);
    assertEquals(String.class, g.getGeneratedType());
    List<String> products = generate(g, 10000);
    assertTrue(products.contains("rapiddweller"));
    assertTrue(products.contains("platform"));
    for (String file : products) {
      assertFalse(file.endsWith(".java"));
      assertFalse(file.endsWith(".html"));
    }
  }

  @Test
  public void testFilesOnly() {
    FileNameGenerator g = new FileNameGenerator("src/main/java", null, FileNameGenerator.PathType.local, true, true, false);
    initialize(g);
    assertEquals(String.class, g.getGeneratedType());
    List<String> products = generate(g, 10000);
    assertFalse(products.contains("rapiddweller"));
    assertFalse(products.contains("platform"));
  }

  @Test
  public void testFilenameFilter() {
    FileNameGenerator g = new FileNameGenerator("src/main/java", ".*Generator.java", FileNameGenerator.PathType.local, true, true, false);
    initialize(g);
    assertEquals(String.class, g.getGeneratedType());
    List<String> products = generate(g, 10000);
    assertFalse(products.contains("rapiddweller"));
    assertFalse(products.contains("platform"));
    assertTrue(products.contains("RandomLongGenerator.java"));
    for (String file : products) {
      assertTrue(file.endsWith("Generator.java"));
    }
  }

  @Test
  public void testFlat() {
    FileNameGenerator g = new FileNameGenerator("src/main/java", null, FileNameGenerator.PathType.local, false, false, true);
    initialize(g);
    assertEquals(String.class, g.getGeneratedType());
    List<String> products = generate(g, 100);
    for (String file : products) {
      assertEquals("com", file);
    }
  }

  @Test
  public void testAbsolute() {
    FileNameGenerator g = new FileNameGenerator("src/main/java", null, FileNameGenerator.PathType.absolute, false, false, true);
    initialize(g);
    assertEquals(String.class, g.getGeneratedType());
    List<String> products = generate(g, 100);
    for (String file : products) {
      assertTrue(file, file.endsWith("com"));
      assertNotEquals("com", file);
    }
  }

  @Test
  public void testCanonical() {
    FileNameGenerator g = new FileNameGenerator("src/main/java", null, FileNameGenerator.PathType.canonical, false, false, true);
    initialize(g);
    assertEquals(String.class, g.getGeneratedType());
    List<String> products = generate(g, 100);
    for (String file : products) {
      assertTrue(file, file.endsWith("com"));
      assertNotEquals("com", file);
    }
  }


}
