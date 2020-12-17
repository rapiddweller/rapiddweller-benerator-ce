package com.my;

import java.util.Arrays;
import java.util.List;

import com.rapiddweller.commons.HeavyweightIterator;
import com.rapiddweller.commons.iterator.HeavyweightIteratorProxy;
import com.rapiddweller.model.data.AbstractEntitySource;
import com.rapiddweller.model.data.Entity;

public class PersonSource extends AbstractEntitySource {

    private List<Entity> list;

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
