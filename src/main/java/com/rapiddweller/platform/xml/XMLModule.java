/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.model.data.DescriptorProvider;

/**
 * Central configuration point for XML processing.<br/><br/>
 * Created: 04.10.2021 07:53:55
 * @author Volker Bergmann
 * @since 2.1.0
 */
public interface XMLModule {
  DescriptorProvider createSchemaDescriptorProvider(String uri, BeneratorContext context);
}
