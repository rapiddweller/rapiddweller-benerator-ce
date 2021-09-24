package com.my;

import com.rapiddweller.model.consumer.AbstractConsumer;
import com.rapiddweller.model.data.Entity;

/**
 * The type Console consumer.
 */
public class ConsoleConsumer extends AbstractConsumer<Entity> {

  /**
   * Start consuming.
   *
   * @param entity the entity
   */
  public void startConsuming(Entity entity) {
    System.out.println("Transaction #" + entity.get("id") + " charged $"
        + entity.get("amount") + " from credit card " + entity.get("creditcard")
        + " of " + entity.get("owner"));
  }

  ;
}
