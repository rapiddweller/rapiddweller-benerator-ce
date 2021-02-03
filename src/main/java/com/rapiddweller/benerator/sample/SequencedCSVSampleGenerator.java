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
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.csv.CSVLineIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample Generator for values that are read from a CSV file.
 * The CSV file needs to be comma-separated and has to contain the values
 * in the first column. The remaining columns are ignored.
 * Example:
 * <pre>
 *   Alpha,sdlkvn,piac
 *   Bravo,lsdknac
 *   Charly,fuv
 * </pre>
 * <p>
 * <br/>
 * Created: 26.07.2007 18:10:33
 *
 * @param <E> the type parameter
 * @see AttachedWeightSampleGenerator
 */
public class SequencedCSVSampleGenerator<E> extends GeneratorProxy<E> {

  /**
   * The URI to read the samples from
   */
  private String uri;

  /**
   * The converter to create instances from the CSV cell strings
   */
  private final Converter<String, E> converter;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Sequenced csv sample generator.
   */
  public SequencedCSVSampleGenerator() {
    this((String) null);
  }

  /**
   * Instantiates a new Sequenced csv sample generator.
   *
   * @param uri the uri
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public SequencedCSVSampleGenerator(String uri) {
    this(uri, new NoOpConverter());
  }

  /**
   * Instantiates a new Sequenced csv sample generator.
   *
   * @param converter the converter
   */
  public SequencedCSVSampleGenerator(Converter<String, E> converter) {
    this(null, converter);
  }

  /**
   * Instantiates a new Sequenced csv sample generator.
   *
   * @param uri       the uri
   * @param converter the converter
   */
  public SequencedCSVSampleGenerator(String uri, Converter<String, E> converter) {
    super(new SampleGenerator<>(converter.getTargetType()));
    this.converter = converter;
    if (uri != null && uri.trim().length() > 0) {
      setUri(uri);
    }
  }

  // configuration properties ----------------------------------------------------------------------------------------

  /**
   * Gets uri.
   *
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Sets uri.
   *
   * @param uri the uri
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * test support method
   *
   * @param value the value
   */
  void addValue(E value) {
    ((SampleGenerator<E>) getSource()).addValue(value);
    // do not set dirty flag, otherwise this value would be c�eared
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    try {
      if (uri == null) {
        throw new InvalidGeneratorSetupException("uri is not set");
      }
      CSVLineIterator parser = new CSVLineIterator(uri);
      List<E> samples = new ArrayList<>();
      DataContainer<String[]> container = new DataContainer<>();
      while ((container = parser.next(container)) != null) {
        String[] tokens = container.getData();
        if (tokens.length > 0) {
          samples.add(converter.convert(tokens[0]));
        }
      }
      ((SampleGenerator<E>) getSource()).setValues(samples);
      super.init(context);
    } catch (FileNotFoundException e) {
      throw new InvalidGeneratorSetupException("uri", "not found: " + uri);
    } catch (IOException e) {
      throw new IllegalGeneratorStateException(e); // file access was interrupted, no fail-over
    } catch (ConversionException e) {
      throw new InvalidGeneratorSetupException("URI content not valid", e);
    }
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[source=" + getSource() + ", converter=" + converter + ']';
  }
}
