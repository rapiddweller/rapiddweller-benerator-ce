/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.model.data.Entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Of the current product, it takes a part of name 'partName' and multiplicity greater than 1 (like array or collection)
 * and applies some generation steps to them in order to overwrite/anonymize data.<br/><br/>
 * Created: 11.09.2021 09:58:53
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class PartModifier extends AbstractGenerationStep<Entity> implements ComponentBuilder<Entity> {

  private final String partName;
  private final GenerationStepSupport support;

  public PartModifier(String partName, List<GenerationStep<Entity>> generationSteps, String scope,
                      BeneratorContext context) {
    super(scope);
    this.partName = partName;
    this.support = new GenerationStepSupport(partName, generationSteps, context);
    this.support.init(context);
  }

  @Override
  public boolean execute(BeneratorContext context) {
    ProductWrapper<?> wrapper = context.getCurrentProduct();
    if (wrapper != null) {
      Object part = ((Entity) wrapper.unwrap()).getComponent(partName);
      Iterator<Entity> iterator = null;
      if (part.getClass().isArray()) {
        iterator = ArrayUtil.iterator((Entity[]) part);
      } else if (part instanceof Collection) {
        iterator = ((Collection) part).iterator();
      }
      if (iterator != null) {
        while (iterator.hasNext()) {
          Entity entity = iterator.next();
          support.apply(entity, context);
        }
      }
    }
    return true;
  }

  @Override
  public void close() {

  }

  @Override
  public void init(BeneratorContext context) {

  }

  @Override
  public void reset() {

  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

}
