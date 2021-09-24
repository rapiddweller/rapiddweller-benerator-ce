package com.my;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.util.SimpleGenerator;

/**
 * The type Number generator.
 */
public class NumberGenerator extends SimpleGenerator<Long> {

  private long n;

  /**
   * Instantiates a new Number generator.
   */
  public NumberGenerator() {
    this(0);
  }

  /**
   * Instantiates a new Number generator.
   *
   * @param initialValue the initial value
   */
  public NumberGenerator(long initialValue) {
    n = initialValue;
  }

  /**
   * Generate long.
   *
   * @return the long
   * @throws IllegalGeneratorStateException the illegal generator state exception
   */
  public Long generate() throws IllegalGeneratorStateException {
    return n++;
  }

  /**
   * Gets generated type.
   *
   * @return the generated type
   */
  public Class<Long> getGeneratedType() {
    return Long.class;
  }

}
