/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.model.data.DescriptorProvider;

/**
 * Benerator CE implementation of the XMLModuke interface.<br/><br/>
 * Created: 04.10.2021 07:58:52
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class DefaultXMLModule implements XMLModule {

  @Override
  public DescriptorProvider createSchemaDescriptorProvider(String uri, BeneratorContext context) {
    return new XMLSchemaDescriptorProvider(uri, context);
  }
}
