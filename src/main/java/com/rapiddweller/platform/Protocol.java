package com.rapiddweller.platform;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.DataSourceProvider;
import com.rapiddweller.common.Named;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.util.List;

public interface Protocol extends Named {

  boolean matchesUri(String sourceSpec);
  DataSourceProvider<Entity> provider(String uri, String segment, ComplexTypeDescriptor descriptor,
                                      BeneratorContext context);
  EntityEncoder entityEncoder();
  EntityDecoder entityDecoder(ComplexTypeDescriptor descriptor);
}
