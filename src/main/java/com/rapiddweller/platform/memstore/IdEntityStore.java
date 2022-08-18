/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.memstore;

import com.rapiddweller.common.OrderedMap;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@link EntityStore} for entities with an id.<br/><br/>
 * Created: 22.11.2021 15:00:30
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class IdEntityStore extends EntityStore {

  private final OrderedMap<Object, Entity> entities;

  public IdEntityStore(ComplexTypeDescriptor type) {
    super(type);
    this.entities = new OrderedMap<>();
  }

  @Override
  public void store(Entity entity) {
    // TODO the current implementation of Entity.idComponentValues() uses an array for composite ids,
    // thus map.get() fails for entity types with more than 1 id component
    Object idComponentValues = entity.idComponentValues();
    if (idComponentValues == null) {
      idComponentValues = entity.getComponents().values();
    }
    entities.put(idComponentValues, entity);
  }

  @Override
  public List<Entity> entities() {
    return entities.values();
  }

  @Override
  public int size() {
    return entities.size();
  }

  @Override
  public Map<Object, Entity> idMap() {
    return entities;
  }

  @Override
  public Iterator<Entity> iterator() {
    return entities.values().iterator();
  }

}
