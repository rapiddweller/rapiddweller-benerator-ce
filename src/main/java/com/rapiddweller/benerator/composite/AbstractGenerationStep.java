/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.engine.AbstractScopedLifeCycleHolder;
import com.rapiddweller.benerator.engine.BeneratorContext;

/**
 * Abstract implementation of the {@link GenerationStep} interface.<br/><br/>
 * Created: 11.09.2021 13:24:37
 * @author Volker Bergmann
 * @since 2.0.0
 */
public abstract class AbstractGenerationStep<E> extends AbstractScopedLifeCycleHolder implements GenerationStep<E> {

  protected String message;
  protected GeneratorContext context;

  protected AbstractGenerationStep(String scope) {
    super(scope);
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public void init(BeneratorContext context) {
    this.context = context;
  }

}
