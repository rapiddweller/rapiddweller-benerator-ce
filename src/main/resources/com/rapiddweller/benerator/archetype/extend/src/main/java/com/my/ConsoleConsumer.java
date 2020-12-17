package com.my;

import com.rapiddweller.model.consumer.AbstractConsumer;
import com.rapiddweller.model.data.Entity;

public class ConsoleConsumer extends AbstractConsumer<Entity> {

    public void startConsuming(Entity entity) {
        System.out.println("Transaction #" + entity.get("id") + " charged $"
                + entity.get("amount") + " from credit card " + entity.get("creditcard")
                + " of " + entity.get("owner"));
    }

    ;
}
