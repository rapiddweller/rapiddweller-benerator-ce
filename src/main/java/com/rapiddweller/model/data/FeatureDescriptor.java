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

import com.rapiddweller.common.Named;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.Operation;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.common.converter.ToStringConverter;

import java.util.List;

/**
 * Common parent class of all descriptors.<br/><br/>
 * Created: 17.07.2006 21:30:45
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class FeatureDescriptor implements Named {

  /**
   * The constant NAME.
   */
  public static final String NAME = "name";
  /**
   * The Details.
   */
  protected OrderedNameMap<FeatureDetail<?>> details;
  /**
   * The Provider.
   */
  protected DescriptorProvider provider;
  /**
   * The name of the feature. It is stored redundantly in the {@link #details} map and the copy in
   * this attribute is used for high-performance retrieval of the name.
   */
  private String name;

  // constructor -----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Feature descriptor.
   *
   * @param name     the name
   * @param provider the provider
   */
  public FeatureDescriptor(String name, DescriptorProvider provider) {
    if (provider == null) {
      throw new IllegalArgumentException("provider is null");
    }
    if (provider.getDataModel() == null) {
      throw new IllegalArgumentException("provider's data model is null");
    }
    this.details = new OrderedNameMap<>();
    this.provider = provider;
    this.addConstraint(NAME, String.class, null);
    this.setName(name);
  }

  // typed interface -------------------------------------------------------------------------------------------------

  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name; // name is stored redundantly for better performance
    setDetailValue(NAME, name);
  }

  /**
   * Gets provider.
   *
   * @return the provider
   */
  public DescriptorProvider getProvider() {
    return provider;
  }

  /**
   * Gets data model.
   *
   * @return the data model
   */
  public DataModel getDataModel() {
    return provider.getDataModel();
  }

  // generic detail access -------------------------------------------------------------------------------------------

  /**
   * Supports detail boolean.
   *
   * @param name the name
   * @return the boolean
   */
  public boolean supportsDetail(String name) {
    return details.containsKey(name);
  }

  /**
   * Gets declared detail value.
   *
   * @param name the name
   * @return the declared detail value
   */
  public Object getDeclaredDetailValue(
      String name) { // TODO v0.8 remove method? It does not differ from getDetailValue any more
    return getConfiguredDetail(name).getValue();
  }

  /**
   * Gets detail value.
   *
   * @param name the name
   * @return the detail value
   */
  public Object getDetailValue(
      String name) { // TODO v0.8 remove generic feature access?
    return this.getConfiguredDetail(name).getValue();
  }

  /**
   * Sets detail value.
   *
   * @param detailName  the detail name
   * @param detailValue the detail value
   */
  public void setDetailValue(String detailName, Object detailValue) {
    if ("name"
        .equals(detailName)) {
      // name is stored redundantly for better performance
      this.name = (String) detailValue;
    }
    FeatureDetail<Object> detail = getConfiguredDetail(detailName);
    Class<Object> detailType = detail.getType();
    if (detailValue != null &&
        !detailType.isAssignableFrom(detailValue.getClass())) {
      detailValue = AnyConverter.convert(detailValue, detailType);
    }
    detail.setValue(detailValue);
  }

  /**
   * Gets details.
   *
   * @return the details
   */
  public List<FeatureDetail<?>> getDetails() {
    return details.values();
  }

  // java.lang overrides ---------------------------------------------------------------------------------------------

  @Override
  public String toString() {
    String name = getName();
    if (StringUtil.isEmpty(name)) {
      name = "anonymous";
    }
    return renderDetails(new StringBuilder(name)).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final FeatureDescriptor that = (FeatureDescriptor) o;
    for (FeatureDetail<?> detail : details.values()) {
      String detailName = detail.getName();
      if (!NullSafeComparator
          .equals(detail.getValue(),
              that.getDetailValue(detailName))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode() * 29 + details.hashCode();
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  /**
   * Render details string.
   *
   * @return the string
   */
  protected String renderDetails() {
    return renderDetails(new StringBuilder()).toString();
  }

  /**
   * Render details string builder.
   *
   * @param builder the builder
   * @return the string builder
   */
  protected StringBuilder renderDetails(StringBuilder builder) {
    builder.append("[");
    boolean empty = true;
    for (FeatureDetail<?> descriptor : details.values()) {
      if (descriptor.getValue() != null &&
          !NAME.equals(descriptor.getName())) {
        if (!empty) {
          builder.append(", ");
        }
        empty = false;
        builder.append(descriptor.getName()).append("=");
        builder.append(ToStringConverter
            .convert(descriptor.getValue(), "[null]"));
      }
    }
    return builder.append("]");
  }

  /**
   * Gets detail type.
   *
   * @param detailName the detail name
   * @return the detail type
   */
  protected Class<?> getDetailType(String detailName) {
    FeatureDetail<?> detail = details.get(detailName);
    if (detail == null) {
      throw new UnsupportedOperationException(
          "Feature detail not supported: " + detailName);
    }
    return detail.getType();
  }

  /**
   * Add config.
   *
   * @param <T>  the type parameter
   * @param name the name
   * @param type the type
   */
  protected <T> void addConfig(String name, Class<T> type) {
    addConfig(name, type, false);
  }

  /**
   * Add config.
   *
   * @param <T>        the type parameter
   * @param name       the name
   * @param type       the type
   * @param deprecated the deprecated
   */
  protected <T> void addConfig(String name, Class<T> type,
                               boolean deprecated) {
    addDetail(name, type, false, deprecated, null);
  }

  /**
   * Add constraint.
   *
   * @param <T>        the type parameter
   * @param name       the name
   * @param type       the type
   * @param combinator the combinator
   */
  protected <T> void addConstraint(String name, Class<T> type,
                                   Operation<T, T> combinator) {
    addDetail(name, type, true, false, combinator);
  }

  /**
   * Add detail.
   *
   * @param <T>        the type parameter
   * @param detailName the detail name
   * @param detailType the detail type
   * @param constraint the constraint
   * @param deprecated the deprecated
   * @param combinator the combinator
   */
  protected <T> void addDetail(String detailName, Class<T> detailType,
                               boolean constraint,
                               boolean deprecated,
                               Operation<T, T> combinator) {
    this.details.put(detailName,
        new FeatureDetail<>(detailName, detailType, constraint,
            combinator));
  }

  // generic property access -----------------------------------------------------------------------------------------

  /**
   * Gets configured detail.
   *
   * @param <T>  the type parameter
   * @param name the name
   * @return the configured detail
   */
  @SuppressWarnings("unchecked")
  public <T> FeatureDetail<T> getConfiguredDetail(String name) {
    if (!supportsDetail(name)) {
      throw new UnsupportedOperationException("Feature detail '" + name +
          "' not supported in feature type: " + getClass().getName());
    }
    return (FeatureDetail<T>) details.get(name);
  }

}
