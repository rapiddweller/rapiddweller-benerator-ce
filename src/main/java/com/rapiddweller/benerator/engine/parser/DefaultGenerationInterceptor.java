/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.Statement;

import java.util.List;

/**
 * Implements the {@link GenerationInterceptor} interface without features.<br/><br/>
 * Created: 13.09.2021 10:45:11
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class DefaultGenerationInterceptor implements GenerationInterceptor {

  @Override
  public void entityGenerationStarting(String taskName, boolean iterationMode, List<Statement> statements) {
    // Nothing to do for this implementor
  }

  @Override
  public void componentGenerationStarting(Generator<?> base, boolean iterationMode, List<Statement> statements) {
    // Nothing to do for this implementor
  }

  @Override
  public void generationComplete(Generator<?> base, boolean iterationMode, List<Statement> statements) {
    // Nothing to do for this implementor
  }

}
