/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.model.data;

import com.rapiddweller.common.Escalator;
import com.rapiddweller.common.LoggerEscalator;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.Operation;
import com.rapiddweller.common.operation.FirstArgSelector;

/**
 * A FeatureDescriptor is composed og FeatureDetails, which have name, value and type.<br/>
 * <br/>
 * Created: 03.08.2007 06:57:42
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 */
public class FeatureDetail<E> {

  private static final Escalator escalator = new LoggerEscalator();

  // properties ------------------------------------------------------------------------------------------------------

  private final String name;
  private final Class<E> type;
  private final Operation<E, E> combinator;
  private final boolean constraint;
  private final boolean deprecated;
  private E value;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Feature detail.
   *
   * @param name       the name
   * @param type       the type
   * @param constraint the constraint
   */
  public FeatureDetail(String name, Class<E> type, boolean constraint) {
    this(name, type, constraint, new FirstArgSelector<>());
  }

  /**
   * Instantiates a new Feature detail.
   *
   * @param name       the name
   * @param type       the type
   * @param constraint the constraint
   * @param combinator the combinator
   */
  public FeatureDetail(String name, Class<E> type, boolean constraint,
                       Operation<E, E> combinator) {
    this(name, type, constraint, combinator, false);
  }

  /**
   * Instantiates a new Feature detail.
   *
   * @param name       the name
   * @param type       the type
   * @param constraint the constraint
   * @param combinator the combinator
   * @param deprecated the deprecated
   */
  public FeatureDetail(String name, Class<E> type, boolean constraint,
                       Operation<E, E> combinator, boolean deprecated) {
    this.name = name;
    this.type = type;
    this.value = null;
    this.constraint = constraint;
    this.combinator = combinator;
    this.deprecated = deprecated;
  }

  // interface -------------------------------------------------------------------------------------------------------

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public Class<E> getType() {
    return type;
  }

  /**
   * Gets value.
   *
   * @return the value
   */
  public E getValue() {
    return value;
  }

  /**
   * Sets value.
   *
   * @param value the value
   */
  public void setValue(E value) {
    if (deprecated && value != null) {
      escalator.escalate("Feature '" + name + "' is deprecated",
          getClass(), value);
    }
    if (value != null && !(type.isAssignableFrom(value.getClass()))) {
      throw new IllegalArgumentException(
          "Tried to assign a value of type '" +
              value.getClass().getName()
              + "'to detail '" + name + "' of type '" + type +
              "'");
    }
    this.value = value;
  }

  /**
   * Combine with e.
   *
   * @param otherValue the other value
   * @return the e
   */
  @SuppressWarnings("unchecked")
  public E combineWith(E otherValue) {
    return combinator.perform(this.value, otherValue);
  }

  /**
   * Is constraint boolean.
   *
   * @return the boolean
   */
  public boolean isConstraint() {
    return constraint;
  }

  /**
   * Gets description.
   *
   * @return the description
   */
  public String getDescription() {
    return name + '=' + value + " (" + type + ')';
  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  /**
   * Is deprecated boolean.
   *
   * @return the boolean
   */
  public boolean isDeprecated() {
    return deprecated;
  }

  @Override
  public String toString() {
    return name + '=' + value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final FeatureDetail<E> that = (FeatureDetail<E>) o;
    return (name.equals(that.name)
        && NullSafeComparator.equals(this.value, that.value));
  }

  @Override
  public int hashCode() {
    int result;
    result = name.hashCode();
    result = 29 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

}
