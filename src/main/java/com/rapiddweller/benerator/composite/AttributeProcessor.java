/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Resettable;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.model.data.Entity;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.List;

/**
 * Applies a {@link Converter} to an attribute.<br/><br/>
 * Created: 12.10.2021 17:22:38
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class AttributeProcessor extends AbstractGenerationStep<Entity> implements ComponentBuilder<Entity> {

  private final String attributeName;
  private final Converter<Object, Object> converter;

  public AttributeProcessor(String attributeName, Converter<Object, Object> converter, String scope) {
    super(scope);
    this.attributeName = attributeName;
    this.converter = converter;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public String getAttributeName() {
    return attributeName;
  }

  @Override
  public boolean isParallelizable() {
    return converter.isParallelizable();
  }

  @Override
  public boolean isThreadSafe() {
    return converter.isThreadSafe();
  }

  // interface -------------------------------------------------------------------------------------------------------

  @Override
  public void init(BeneratorContext context) {
    super.init(context);
    if (converter instanceof ContextAware) {
      ((ContextAware) converter).setContext(context);
    }
  }

  @Override
  public boolean execute(BeneratorContext context) {
    ProductWrapper<?> wrapper = context.getCurrentProduct();
    if (wrapper != null) {
      Entity entity = (Entity) wrapper.unwrap();
      Object attribute = entity.getComponent(attributeName);
      if (attribute == null) {
        // nothing to do
      } else if (attribute instanceof List) {
        List list = (List) attribute;
        for (int i = 0; i < list.size(); i++) {
          list.set(i, applyToValue(list.get(i)));
        }
      } else if (attribute.getClass().isArray()) {
        int length = Array.getLength(attribute);
        for (int i = 0; i < length; i++) {
          Array.set(attribute, i, applyToValue(Array.get(attribute, i)));
        }
      } else {
        entity.setComponent(attributeName, applyToValue(attribute));
      }
    }
    return true;
  }

  @Override
  public void reset() {
    if (converter instanceof Resettable) {
      ((Resettable) converter).reset();
    }
  }

  @Override
  public void close() {
    if (converter instanceof Closeable) {
      IOUtil.close((Closeable) converter);
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private Object applyToValue(Object value) {
    return converter.convert(value);
  }

}
