/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.Statement;

import java.util.List;

/**
 * Interceptor interface which is called when certain steps
 * of an Entity generation setup are being processed.<br/><br/>
 * Created: 13.09.2021 10:42:36
 * @author Volker Bergmann
 * @since 1.2.0
 */
public interface GenerationInterceptor {
  void entityGenerationStarting(String taskName, boolean iterationMode, List<Statement> statements);
  void componentGenerationStarting(Generator<?> base, boolean iterationMode, List<Statement> statements);
  void generationComplete(Generator<?> base, boolean iterationMode, List<Statement> statements);
}
