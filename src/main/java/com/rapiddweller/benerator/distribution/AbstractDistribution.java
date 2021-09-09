/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.distribution;

/**
 * Abstract implementation of the {@link Distribution} interface.
 * Implementors of the Distribution interface are recommended to inherit from this class
 * for optimal forward compatibility.
 * In order to migrate implementors of the {@link Distribution} interface before version 1.2.0,
 * their <code>implements Distribution</code> directive should be changed to
 * <code>extends AbstractDistribution</code>.<br/><br/>
 * Created: 09.09.2021 13:51:17
 * @author Volker Bergmann
 * @since 1.2.0
 */
public abstract class AbstractDistribution implements Distribution {

  @Override
  public boolean isApplicationDetached() {
    return false;
  }

}
