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

package com.rapiddweller.benerator.csv;

import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.csv.CSVLineIterator;
import com.rapiddweller.script.WeightedSample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides CSV-related utility methods.<br/><br/>
 * Created: 17.02.2010 23:20:35
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CSVGeneratorUtil {

  /**
   * Parse dataset files list.
   *
   * @param <T>             the type parameter
   * @param datasetName     the dataset name
   * @param separator       the separator
   * @param nesting         the nesting
   * @param filenamePattern the filename pattern
   * @param encoding        the encoding
   * @param converter       the converter
   * @return the list
   */
  public static <T> List<WeightedSample<T>> parseDatasetFiles(
      String datasetName, char separator, String nesting, String filenamePattern,
      String encoding, Converter<String, T> converter) {
    String[] dataFilenames;
    if (nesting == null || datasetName == null) {
      dataFilenames = new String[] {filenamePattern};
    } else {
      dataFilenames = DatasetUtil.getDataFiles(filenamePattern, datasetName, nesting);
    }
    List<WeightedSample<T>> samples = new ArrayList<>();
    for (String dataFilename : dataFilenames) {
      parseFile(dataFilename, separator, encoding, converter, samples);
    }
    return samples;
  }

  /**
   * Parse file list.
   *
   * @param <T>       the type parameter
   * @param filename  the filename
   * @param separator the separator
   * @param encoding  the encoding
   * @param converter the converter
   * @return the list
   */
  public static <T> List<WeightedSample<T>> parseFile(String filename, char separator, String encoding,
                                                      Converter<String, T> converter) {
    return parseFile(filename, separator, encoding, converter, new ArrayList<>());
  }

  /**
   * Parse file list.
   *
   * @param <T>       the type parameter
   * @param filename  the filename
   * @param separator the separator
   * @param encoding  the encoding
   * @param converter the converter
   * @param samples   the samples
   * @return the list
   */
  public static <T> List<WeightedSample<T>> parseFile(String filename, char separator, String encoding,
                                                      Converter<String, T> converter, List<WeightedSample<T>> samples) {
    try {
      CSVLineIterator iterator = new CSVLineIterator(filename, separator, encoding);
      DataContainer<String[]> container = new DataContainer<>();
      while ((container = iterator.next(container)) != null) {
        String[] tokens = container.getData();
        if (tokens.length == 0) {
          continue;
        }
        double weight = (tokens.length < 2 || tokens[1] == null || tokens[1].trim().length() == 0 ? 1. : Double.parseDouble(tokens[1].trim()));
        T value = converter.convert(tokens[0]);
        WeightedSample<T> sample = new WeightedSample<>(value, weight);
        samples.add(sample);
      }
      return samples;
    } catch (IOException e) {
      throw new ConfigurationError(e);
    }
  }

}
