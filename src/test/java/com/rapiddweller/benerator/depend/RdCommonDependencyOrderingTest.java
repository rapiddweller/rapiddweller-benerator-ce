package com.rapiddweller.benerator.depend;

import com.rapiddweller.common.depend.DefaultDependent;
import com.rapiddweller.common.depend.DependencyModel;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration guard: verifies the rd-lib-common version benerator links against has the
 * DependencyModel single-pass postProcessNodes fix. The same web of required+optional cycles
 * (the shape that turns up when jdbacl's DBUtil orders a complex DB schema) strands t8 with
 * "Incomplete nodes left" on the unfixed library and resolves completely once it is fixed.
 *
 * <p>Fails against rd-lib-common &lt;= 2.0.1-jdk-11; passes from 2.1.0-jdk-11-SNAPSHOT on.</p>
 */
public class RdCommonDependencyOrderingTest {

  static class Dep extends DefaultDependent<String, Dep> {
    Dep(String name) {
      super(name);
    }
  }

  @Test
  public void resolvesCyclicSchemaWithoutStranding() {
    Dep t0 = new Dep("t0");
    Dep t1 = new Dep("t1");
    Dep t2 = new Dep("t2");
    Dep t3 = new Dep("t3");
    Dep t4 = new Dep("t4");
    Dep t5 = new Dep("t5");
    Dep t6 = new Dep("t6");
    Dep t7 = new Dep("t7");
    Dep t8 = new Dep("t8");

    t1.addOptionalProvider(t3);
    t2.addRequiredProvider(t8);
    t2.addRequiredProvider(t6);
    t3.addOptionalProvider(t6);
    t4.addOptionalProvider(t2);
    t4.addRequiredProvider(t0);
    t4.addOptionalProvider(t6);
    t4.addOptionalProvider(t3);
    t4.addOptionalProvider(t8);
    t5.addOptionalProvider(t8);
    t5.addOptionalProvider(t2);
    t6.addRequiredProvider(t2);
    t6.addRequiredProvider(t5);
    t6.addRequiredProvider(t7);
    t7.addRequiredProvider(t8);
    t7.addRequiredProvider(t1);
    t7.addRequiredProvider(t0);
    t7.addOptionalProvider(t6);
    t8.addOptionalProvider(t2);
    t8.addOptionalProvider(t5);
    t8.addRequiredProvider(t6);

    DependencyModel<Dep> model = new DependencyModel<>();
    for (Dep d : new Dep[] {t0, t1, t2, t3, t4, t5, t6, t7, t8}) {
      model.addNode(d);
    }

    List<Dep> ordered = model.dependencyOrderedObjects(true);
    assertEquals(9, ordered.size());
    for (Dep d : new Dep[] {t0, t1, t2, t3, t4, t5, t6, t7, t8}) {
      assertTrue("missing " + d, ordered.contains(d));
    }
  }
}
