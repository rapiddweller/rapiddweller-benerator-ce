/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.Converter;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Of the current product, it takes a part of name 'partName' and multiplicity greater than 1 (like array or collection)
 * and applies some generation steps to them in order to overwrite/anonymize data.<br/><br/>
 * Created: 11.09.2021 09:58:53
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class PartModifier extends AbstractGenerationStep<Entity> implements ComponentBuilder<Entity> {

  private final String partName;
  private final GenerationStepSupport<Entity> steps;
  private final Converter converter;

  public PartModifier(String partName, List<GenerationStep<Entity>> generationSteps, String scope) {
    this(partName, generationSteps, scope, null);
  }
  
  public PartModifier(String partName, List<GenerationStep<Entity>> generationSteps, String scope, Converter converter) {
    super(scope);
    this.partName = partName;
    this.steps = new GenerationStepSupport<>(partName, generationSteps);
    this.converter = converter;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public String getPartName() {
    return partName;
  }

  @Override
  public boolean isParallelizable() {
    return steps.isParallelizable();
  }

  @Override
  public boolean isThreadSafe() {
    return steps.isThreadSafe();
  }

  // interface -------------------------------------------------------------------------------------------------------

  @Override
  public void init(BeneratorContext context) {
    steps.init(context);
  }

  @Override
  public boolean execute(BeneratorContext context) {
    ProductWrapper<?> wrapper = context.getCurrentProduct();
    if (wrapper != null) {
      Object part = ((Entity) wrapper.unwrap()).getComponent(partName);
      // Init part and add into currentProduct
      if (part == null) {
        part = new Entity((ComplexTypeDescriptor) null, null);
        ((Entity)context.getCurrentProduct().unwrap()).setComponent(partName, part);
      }
      applyToPart(part, context);
      // Convert part (this.converter consist context itself)
      if (converter != null) {
        converter.convert(part);
      }
    }
    return true;
  }

  @Override
  public void reset() {
    steps.reset();
  }

  @Override
  public void close() {
    steps.close();
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void applyToPart(Object part, BeneratorContext context) {
    if (part instanceof Entity) {
      steps.apply((Entity) part, context);
    } else {
      Iterator<Entity> iterator = containerIterator(part);
      while (iterator.hasNext()) {
        steps.apply(iterator.next(), context);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Iterator<Entity> containerIterator(Object part) {
    if (part.getClass().isArray()) {
      return ArrayUtil.iterator((Entity[]) part);
    } else if (part instanceof Collection) {
      return ((Collection<Entity>) part).iterator();
    } else {
      throw BeneratorExceptionFactory.getInstance().programmerUnsupported(
          "Don't know how to modify " + part.getClass());
    }
  }

}
