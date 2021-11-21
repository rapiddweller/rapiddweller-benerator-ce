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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.csv.CSVGeneratorUtil;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.script.WeightedSample;

import java.util.List;

/**
 * Sample Generator for values that are read from a CSV file.
 * The CSV file needs to be comma-separated and has to contain the values
 * in the first column. The second column optionally may have a weight value.
 * Example:
 * <pre>
 *   Alpha,1
 *   Bravo,4
 *   Charly,2
 * </pre>
 * <br/>
 * Created: 11.06.2006 20:49:33
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @see AttachedWeightSampleGenerator
 * @since 0.1
 */
public class WeightedCSVSampleGenerator<E> extends GeneratorProxy<E> {

  /** The URI to read the samples from */
  protected String uri;

  /** The Separator. */
  private char separator;

  private String encoding;

  /** The converter to create instances from the CSV cell stringsc*/
  private final Converter<String, E> converter;


  // constructors ----------------------------------------------------------------------------------------------------

  public WeightedCSVSampleGenerator(String url) {
    this(url, SystemInfo.getFileEncoding(), ',');
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public WeightedCSVSampleGenerator(String url, String encoding, char separator) {
    this((Class<E>) String.class, url, encoding, separator, new NoOpConverter());
  }

  public WeightedCSVSampleGenerator(Class<E> targetType, String uri, String encoding, char separator, Converter<String, E> converter) {
    super(targetType);
    setSource(new AttachedWeightSampleGenerator<>(targetType));
    this.converter = converter;
    this.encoding = encoding;
    this.separator = separator;
    if (uri != null && uri.trim().length() > 0) {
      this.uri = uri;
    }
  }


  // properties ------------------------------------------------------------------------------------------------------

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public char getSeparator() {
    return separator;
  }

  public void setSeparator(char separator) {
    this.separator = separator;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }


  // generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    List<WeightedSample<E>> samples = CSVGeneratorUtil.parseFile(uri, separator, encoding, converter);
    AttachedWeightSampleGenerator<E> awSource = (AttachedWeightSampleGenerator<E>) getSource();
    if (!samples.isEmpty()) {
      for (WeightedSample<E> sample : samples) {
        awSource.addSample(sample.getValue(), sample.getWeight());
      }
    } else {
      awSource.clear();
      logger.warn("CSV file is empty: {}", uri);
    }
    super.init(context);
  }

}
