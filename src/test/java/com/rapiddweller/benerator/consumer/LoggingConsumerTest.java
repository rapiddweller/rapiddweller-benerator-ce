/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.consumer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link LoggingConsumer}.<br/><br/>
 * Created: 09.11.2021 12:38:56
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class LoggingConsumerTest {

  private static final Logger logger = LoggerFactory.getLogger(LoggingConsumerTest.class);

  @Test
  public void testDefault() {
    logger.info("testDefault");
    LoggingConsumer consumer = new LoggingConsumer();
    assertTrue(consumer.shouldLog());
    consume(1, consumer);
    assertTrue(consumer.shouldLog());
    consume(2, consumer);
    assertTrue(consumer.shouldLog());
    consume(3, consumer);
    assertTrue(consumer.shouldLog());
    consume(4, consumer);
    assertTrue(consumer.shouldLog());
    consume(5, consumer);
    assertTrue(consumer.shouldLog());
  }

  @Test
  public void test_3_4() {
    logger.info("testDefault");
    LoggingConsumer consumer = new LoggingConsumer(2, 3);
    assertFalse(consumer.shouldLog());
    consume(1, consumer);
    assertFalse(consumer.shouldLog());
    consume(2, consumer);
    assertTrue(consumer.shouldLog());
    consume(3, consumer);
    assertTrue(consumer.shouldLog());
    consume(4, consumer);
    assertTrue(consumer.shouldLog());
    consume(5, consumer);
    assertFalse(consumer.shouldLog());
    consume(6, consumer);
    assertFalse(consumer.shouldLog());
    consume(7, consumer);
  }

  private void consume(int i, LoggingConsumer consumer) {
    consumer.startProductConsumption(i);
    consumer.finishProductConsumption(i);
  }

}
