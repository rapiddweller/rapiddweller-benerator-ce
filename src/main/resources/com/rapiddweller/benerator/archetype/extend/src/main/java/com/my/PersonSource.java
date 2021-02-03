package com.my;

import com.rapiddweller.common.HeavyweightIterator;
import com.rapiddweller.common.iterator.HeavyweightIteratorProxy;
import com.rapiddweller.model.data.AbstractEntitySource;
import com.rapiddweller.model.data.Entity;

import java.util.Arrays;
import java.util.List;

/**
 * The type Person source.
 */
public class PersonSource extends AbstractEntitySource {

  private List<Entity> list;

  /**
   * Instantiates a new Person source.
   */
  public PersonSource() {
    list = Arrays.asList(
        new Entity("Person", "firstname", "Alice"),
        new Entity("Person", "firstname", "Bob"),
        new Entity("Person", "firstname", "Charly")
    );
  }

  public HeavyweightIterator<Entity> iterator() {
    return new HeavyweightIteratorProxy<Entity>(list.iterator());
  }

}
