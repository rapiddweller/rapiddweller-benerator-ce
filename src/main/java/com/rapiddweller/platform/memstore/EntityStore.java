/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.memstore;

import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.util.List;
import java.util.Map;

/**
 * Abstract parent class for in-memory entity storage.<br/><br/>
 * Created: 22.11.2021 15:00:07
 * @author Volker Bergmann
 * @since 2.1.0
 */
public abstract class EntityStore implements Iterable<Entity> {

  private final ComplexTypeDescriptor type;

  protected EntityStore(ComplexTypeDescriptor type) {
    this.type = type;
  }

  public ComplexTypeDescriptor getType() {
    return type;
  }

  public abstract void store(Entity entity);
  public abstract List<Entity> entities();
  public abstract int size();
  public abstract Map<Object, Entity> idMap();

}
