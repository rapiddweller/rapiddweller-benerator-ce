/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.test_with_desc;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;
import com.rapiddweller.platform.test_with_desc.sub.ImpPkgSimpleBean;
import com.rapiddweller.platform.test_with_desc.sub2.ImpClassSimpleBean;

/**
 * {@link com.rapiddweller.benerator.PlatformDescriptor} for testing.<br/><br/>
 * Created: 01.12.2021 16:29:51
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {

  public PlatformDescriptor() {
    super(PlatformDescriptor.class.getPackageName());
  }

  @Override
  public String[] getPackagesToImport() {
    return new String[] { ImpPkgSimpleBean.class.getPackageName() };
  }

  @Override
  public String[] getClassesToImport() {
    return new String[] {ImpClassSimpleBean.class.getName()};
  }

  @Override
  public XMLStatementParser[] getParsers() {
    return new XMLStatementParser[] { new TWDParser() };
  }

}
