/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.memstore;

import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@link EntityStore} for entity types without id.<br/><br/>
 * Created: 22.11.2021 15:00:22
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class UnidentifiedEntityStore extends EntityStore {

  private final List<Entity> entities;

  public UnidentifiedEntityStore(ComplexTypeDescriptor type) {
    super(type);
    this.entities = new ArrayList<>();
  }

  @Override
  public void store(Entity entity) {
    entities.add(entity);
  }

  @Override
  public List<Entity> entities() {
    return entities;
  }

  @Override
  public int size() {
    return entities.size();
  }

  @Override
  public Map<Object, Entity> idMap() {
    return null;
  }

  @Override
  public Iterator<Entity> iterator() {
    return entities.iterator();
  }

}
