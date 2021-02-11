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

import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.operation.FirstNonNullSelector;

import java.util.Locale;

/**
 * Describes a type.<br/><br/>
 * Created: 03.03.2008 08:37:30
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public abstract class TypeDescriptor extends FeatureDescriptor {

  /**
   * The constant VALIDATOR.
   */

  // constraint names

  public static final String VALIDATOR = "validator";
  /**
   * The constant FILTER.
   */
  public static final String FILTER = "filter";
  /**
   * The constant CONDITION.
   */
  public static final String CONDITION = "condition";

  /**
   * The constant GENERATOR.
   */

  // config names

  public static final String GENERATOR = "generator";
  /**
   * The constant CONVERTER.
   */
  public static final String CONVERTER = "converter";
  /**
   * The constant PATTERN.
   */
  public static final String PATTERN = "pattern";
  /**
   * The constant SCRIPT.
   */
  public static final String SCRIPT = "script";

  /**
   * The constant SOURCE.
   */
  public static final String SOURCE = "source";
  /**
   * The constant FORMAT.
   */
  public static final String FORMAT = "format";
  /**
   * The constant ROW_BASED.
   */
  public static final String ROW_BASED = "rowBased";
  /**
   * The constant SEGMENT.
   */
  public static final String SEGMENT = "segment";
  /**
   * The constant OFFSET.
   */
  public static final String OFFSET = "offset";
  /**
   * The constant SELECTOR.
   */
  public static final String SELECTOR = "selector";
  /**
   * The constant SUB_SELECTOR.
   */
  public static final String SUB_SELECTOR = "subSelector";
  /**
   * The constant ENCODING.
   */
  public static final String ENCODING = "encoding";
  /**
   * The constant SEPARATOR.
   */
  public static final String SEPARATOR = "separator";
  /**
   * The constant EMPTY_MARKER.
   */
  public static final String EMPTY_MARKER = "emptyMarker";
  /**
   * The constant NULL_MARKER.
   */
  public static final String NULL_MARKER = "nullMarker";

  /**
   * The constant CYCLIC.
   */
  public static final String CYCLIC = "cyclic";
  /**
   * The constant SCOPE.
   */
  public static final String SCOPE = "scope";

  /**
   * The constant LOCALE.
   */
  public static final String LOCALE = "locale";
  /**
   * The constant DATASET.
   */
  public static final String DATASET = "dataset";
  /**
   * The constant NESTING.
   */
  public static final String NESTING = "nesting";

  /**
   * The constant DISTRIBUTION.
   */
  public static final String DISTRIBUTION = "distribution";

  // attributes ------------------------------------------------------------------------------------------------------

  /**
   * The Parent name.
   */
  protected String parentName;
  /**
   * The Parent.
   */
  protected TypeDescriptor parent;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Type descriptor.
   *
   * @param name     the name
   * @param provider the provider
   * @param parent   the parent
   */
  public TypeDescriptor(String name, DescriptorProvider provider,
                        TypeDescriptor parent) {
    this(name, provider, (parent != null ? parent.getName() : null));
    this.parent = parent;
  }

  /**
   * Instantiates a new Type descriptor.
   *
   * @param name       the name
   * @param provider   the provider
   * @param parentName the parent name
   */
  public TypeDescriptor(String name, DescriptorProvider provider,
                        String parentName) {
    super(name, provider);
    this.parentName = parentName;
    init();
  }

  /**
   * Init.
   */
  protected void init() {
    // constraints
    addConstraint(VALIDATOR, String.class, new FirstNonNullSelector<>());
    addConstraint(FILTER, String.class, new FirstNonNullSelector<>());
    addConstraint(CONDITION, String.class, new FirstNonNullSelector<>());

    // config
    addConfig(GENERATOR, String.class);
    addConfig(CONVERTER, String.class);
    addConfig(PATTERN, String.class);
    addConfig(SCRIPT, String.class);

    addConfig(SOURCE, String.class);
    addConfig(FORMAT, Format.class);
    addConfig(ROW_BASED, Boolean.class);
    addConfig(SEGMENT, String.class);
    addConfig(OFFSET, Integer.class);
    addConfig(SELECTOR, String.class);
    addConfig(SUB_SELECTOR, String.class);
    addConfig(SEPARATOR, String.class);
    addConfig(EMPTY_MARKER, String.class);
    addConfig(NULL_MARKER, String.class);
    addConfig(ENCODING, String.class);
    addConfig(SCOPE, String.class);
    addConfig(CYCLIC, Boolean.class);
    // i18n config
    addConfig(LOCALE, Locale.class);
    addConfig(DATASET, String.class);
    addConfig(NESTING, String.class);
    // distribution
    addConfig(DISTRIBUTION, String.class);
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets parent name.
   *
   * @return the parent name
   */
  public String getParentName() {
    return parentName;
  }

  /**
   * Sets parent name.
   *
   * @param parentName the parent name
   */
  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  /**
   * Is row based boolean.
   *
   * @return the boolean
   */
  public Boolean isRowBased() {
    return (Boolean) getDetailValue(ROW_BASED);
  }

  /**
   * Sets row based.
   *
   * @param rowBased the row based
   */
  public void setRowBased(Boolean rowBased) {
    setDetailValue(ROW_BASED, rowBased);
  }

  /**
   * Gets validator.
   *
   * @return the validator
   */
  public String getValidator() {
    return (String) getDetailValue(VALIDATOR);
  }

  /**
   * Sets validator.
   *
   * @param filter the filter
   */
  public void setValidator(String filter) {
    setDetailValue(VALIDATOR, filter);
  }

  /**
   * Gets filter.
   *
   * @return the filter
   */
  public String getFilter() {
    return (String) getDetailValue(FILTER);
  }

  /**
   * Sets filter.
   *
   * @param filter the filter
   */
  public void setFilter(String filter) {
    setDetailValue(FILTER, filter);
  }

  /**
   * Gets condition.
   *
   * @return the condition
   */
  public String getCondition() {
    return (String) getDetailValue(CONDITION);
  }

  /**
   * Sets condition.
   *
   * @param condition the condition
   */
  public void setCondition(String condition) {
    setDetailValue(CONDITION, condition);
  }

  /**
   * Gets generator.
   *
   * @return the generator
   */
  public String getGenerator() {
    return (String) getDetailValue(GENERATOR);
  }

  /**
   * Sets generator.
   *
   * @param generatorName the generator name
   */
  public void setGenerator(String generatorName) {
    setDetailValue(GENERATOR, generatorName);
  }

  /**
   * Gets converter.
   *
   * @return the converter
   */
  public String getConverter() {
    return (String) getDetailValue(CONVERTER);
  }

  /**
   * Sets converter.
   *
   * @param converter the converter
   */
  public void setConverter(String converter) {
    setDetailValue(CONVERTER, converter);
  }

  /**
   * Gets pattern.
   *
   * @return the pattern
   */
  public String getPattern() {
    return (String) getDetailValue(PATTERN);
  }

  /**
   * Sets pattern.
   *
   * @param pattern the pattern
   */
  public void setPattern(String pattern) {
    setDetailValue(PATTERN, pattern);
  }

  /**
   * Gets script.
   *
   * @return the script
   */
  public String getScript() {
    return (String) getDetailValue(SCRIPT);
  }

  /**
   * Sets script.
   *
   * @param script the script
   */
  public void setScript(String script) {
    setDetailValue(SCRIPT, script);
  }

  /**
   * Gets source.
   *
   * @return the source
   */
  public String getSource() {
    return (String) getDetailValue(SOURCE);
  }

  /**
   * Sets source.
   *
   * @param source the source
   */
  public void setSource(String source) {
    setDetailValue(SOURCE, source);
  }

  /**
   * Gets format.
   *
   * @return the format
   */
  public Format getFormat() {
    return (Format) getDetailValue(FORMAT);
  }

  /**
   * Sets format.
   *
   * @param format the format
   */
  public void setFormat(Format format) {
    setDetailValue(FORMAT, format);
  }

  /**
   * Gets segment.
   *
   * @return the segment
   */
  public String getSegment() {
    return (String) getDetailValue(SEGMENT);
  }

  /**
   * Sets segment.
   *
   * @param segment the segment
   */
  public void setSegment(String segment) {
    setDetailValue(SEGMENT, segment);
  }

  /**
   * Gets offset.
   *
   * @return the offset
   */
  public Integer getOffset() {
    return (Integer) getDetailValue(OFFSET);
  }

  /**
   * Sets offset.
   *
   * @param offset the offset
   */
  public void setOffset(Integer offset) {
    setDetailValue(OFFSET, offset);
  }

  /**
   * Gets selector.
   *
   * @return the selector
   */
  public String getSelector() {
    return (String) getDetailValue(SELECTOR);
  }

  /**
   * Sets selector.
   *
   * @param selector the selector
   */
  public void setSelector(String selector) {
    setDetailValue(SELECTOR, selector);
  }

  /**
   * Gets sub selector.
   *
   * @return the sub selector
   */
  public String getSubSelector() {
    return (String) getDetailValue(SUB_SELECTOR);
  }

  /**
   * Sets sub selector.
   *
   * @param selector the selector
   */
  public void setSubSelector(String selector) {
    setDetailValue(SUB_SELECTOR, selector);
  }

  /**
   * Gets separator.
   *
   * @return the separator
   */
  public String getSeparator() {
    return (String) getDetailValue(SEPARATOR);
  }

  /**
   * Sets separator.
   *
   * @param separator the separator
   */
  public void setSeparator(String separator) {
    setDetailValue(SEPARATOR, separator);
  }

  /**
   * Gets empty marker.
   *
   * @return the empty marker
   */
  public String getEmptyMarker() {
    return (String) getDetailValue(EMPTY_MARKER);
  }

  /**
   * Sets empty marker.
   *
   * @param emptyMarker the empty marker
   */
  public void setEmptyMarker(String emptyMarker) {
    setDetailValue(EMPTY_MARKER, emptyMarker);
  }

  /**
   * Gets null marker.
   *
   * @return the null marker
   */
  public String getNullMarker() {
    return (String) getDetailValue(NULL_MARKER);
  }

  /**
   * Sets null marker.
   *
   * @param nullMarker the null marker
   */
  public void setNullMarker(String nullMarker) {
    setDetailValue(NULL_MARKER, nullMarker);
  }

  /**
   * Gets encoding.
   *
   * @return the encoding
   */
  public String getEncoding() {
    return (String) getDetailValue(ENCODING);
  }

  /**
   * Sets encoding.
   *
   * @param encoding the encoding
   */
  public void setEncoding(String encoding) {
    setDetailValue(ENCODING, encoding);
  }

  /**
   * Gets scope.
   *
   * @return the scope
   */
  public String getScope() {
    return (String) getDetailValue(SCOPE);
  }

  /**
   * Sets scope.
   *
   * @param scope the scope
   */
  public void setScope(String scope) {
    setDetailValue(SCOPE, scope);
  }

  /**
   * Is cyclic boolean.
   *
   * @return the boolean
   */
  public Boolean isCyclic() {
    return (Boolean) getDetailValue(CYCLIC);
  }

  /**
   * Sets cyclic.
   *
   * @param cyclic the cyclic
   */
  public void setCyclic(boolean cyclic) {
    setDetailValue(CYCLIC, cyclic);
  }

  /**
   * Gets dataset.
   *
   * @return the dataset
   */
  public String getDataset() {
    return (String) getDetailValue(DATASET);
  }

  /**
   * Sets dataset.
   *
   * @param dataset the dataset
   */
  public void setDataset(String dataset) {
    setDetailValue(DATASET, dataset);
  }

  /**
   * Gets nesting.
   *
   * @return the nesting
   */
  public String getNesting() {
    return (String) getDetailValue(NESTING);
  }

  /**
   * Sets nesting.
   *
   * @param nesting the nesting
   */
  public void setNesting(String nesting) {
    setDetailValue(NESTING, nesting);
  }

  /**
   * Gets locale.
   *
   * @return the locale
   */
  public Locale getLocale() {
    return (Locale) getDetailValue(LOCALE);
  }

  /**
   * Sets locale id.
   *
   * @param localeId the locale id
   */
  public void setLocaleId(String localeId) {
    setDetailValue(LOCALE, LocaleUtil.getLocale(localeId));
  }

  /**
   * Gets distribution.
   *
   * @return the distribution
   */
  public String getDistribution() {
    return (String) getDetailValue(DISTRIBUTION);
  }

  /**
   * Sets distribution.
   *
   * @param distribution the distribution
   */
  public void setDistribution(String distribution) {
    setDetailValue(DISTRIBUTION, distribution);
  }

  // literal construction helpers ------------------------------------------------------------------------------------

  /**
   * With source type descriptor.
   *
   * @param source the source
   * @return the type descriptor
   */
  public TypeDescriptor withSource(String source) {
    setSource(source);
    return this;
  }

  /**
   * With separator type descriptor.
   *
   * @param separator the separator
   * @return the type descriptor
   */
  public TypeDescriptor withSeparator(String separator) {
    setSeparator(separator);
    return this;
  }

  /**
   * With generator type descriptor.
   *
   * @param generator the generator
   * @return the type descriptor
   */
  public TypeDescriptor withGenerator(String generator) {
    setGenerator(generator);
    return this;
  }

  // generic functionality -------------------------------------------------------------------------------------------

  /**
   * Gets parent.
   *
   * @return the parent
   */
  public TypeDescriptor getParent() {
    if (parent != null) {
      return parent;
    }
    if (parentName == null) {
      return null;
    }
    // TODO v0.7.1 the following is a workaround for name conflicts with types of same name in different name spaces, e.g. xs:string <-> ben.string
    TypeDescriptor candidate = getDataModel().getTypeDescriptor(parentName);
    if (candidate != this) {
      parent = candidate;
    }
    return parent;
  }

  /**
   * Sets parent.
   *
   * @param parent the parent
   */
  public void setParent(TypeDescriptor parent) {
    this.parent = parent;
  }

}
