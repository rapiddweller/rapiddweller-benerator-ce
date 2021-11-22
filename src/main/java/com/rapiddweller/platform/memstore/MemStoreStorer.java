/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.memstore;

import com.rapiddweller.benerator.composite.EntityTypeChanger;
import com.rapiddweller.benerator.consumer.AbstractConsumer;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

/**
 * {@link com.rapiddweller.benerator.Consumer} implementation which receives Entity
 * objects and stores them in a {@link MemStore}. If the entity has an id and the
 * store has an entity of this type and id, then this one will be replaced,
 * otherwise inserted.<br/><br/>
 * Created: 22.11.2021 14:27:39
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class MemStoreStorer extends AbstractConsumer {

  protected final MemStore targetSystem;
  protected final String targetTypeName;
  protected ComplexTypeDescriptor targetTypeDescriptor;

  public MemStoreStorer(MemStore targetSystem) {
    this(targetSystem, null);
  }

  public MemStoreStorer(MemStore targetSystem, String targetTypeName) {
    this.targetSystem = targetSystem;
    this.targetTypeName = targetTypeName;
    this.targetTypeDescriptor = null;
  }

  @Override
  public void startProductConsumption(Object object) {
    Entity entity = (Entity) object;
    if (targetTypeName != null) {
      haveDescriptor(entity);
      targetSystem.store(EntityTypeChanger.changeType(entity, targetTypeDescriptor));
    } else {
      targetSystem.store(entity);
    }
  }

  @Override
  public void flush() {
    targetSystem.flush();
  }

  @Override
  public void close() {
    targetSystem.close();
  }

  protected void haveDescriptor(Entity entity) {
    if (targetTypeDescriptor == null) {
      targetTypeDescriptor = new ComplexTypeDescriptor(targetTypeName, targetSystem, entity.descriptor());
    }
  }

}