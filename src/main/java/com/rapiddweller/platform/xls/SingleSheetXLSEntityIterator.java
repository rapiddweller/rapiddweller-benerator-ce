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

package com.rapiddweller.platform.xls;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.util.OrthogonalArrayIterator;
import com.rapiddweller.format.util.ThreadLocalDataContainer;
import com.rapiddweller.format.xls.XLSLineIterator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.array.Array2EntityConverter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Iterates a single sheet of an XLS document and maps its rows to entities.<br/><br/>
 * Created: 23.06.2014 17:20:19
 *
 * @author Volker Bergmann
 * @since 0.9.5
 */
public class SingleSheetXLSEntityIterator implements DataIterator<Entity> {

  private String uri;
  private final Workbook workbook;
  private final boolean rowBased;
  private final boolean formatted;
  private final String emptyMarker;
  private DataIterator<Object[]> source;
  private final Converter<String, ?> preprocessor;
  private Array2EntityConverter converter;
  private Object[] buffer;
  private final ThreadLocalDataContainer<Object[]> sourceContainer =
      new ThreadLocalDataContainer<>();
  private ComplexTypeDescriptor entityDescriptor;
  private final BeneratorContext context;
  private String[] headers;


  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Single sheet xls entity iterator.
   *
   * @param uri          the uri
   * @param sheetName    the sheet name
   * @param preprocessor the preprocessor
   * @param entityType   the entity type
   * @param context      the context
   * @param rowBased     the row based
   * @param formatted    the formatted
   * @param emptyMarker  the empty marker
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException            the io exception
   */
  public SingleSheetXLSEntityIterator(String uri, String sheetName,
                                      Converter<String, ?> preprocessor,
                                      ComplexTypeDescriptor entityType,
                                      BeneratorContext context,
                                      boolean rowBased, boolean formatted,
                                      String emptyMarker)
      throws InvalidFormatException, IOException {
    this(loadSheet(uri, sheetName), preprocessor, entityType, context,
        rowBased, formatted, emptyMarker);
    this.uri = uri;
  }

  /**
   * Instantiates a new Single sheet xls entity iterator.
   *
   * @param sheet            the sheet
   * @param preprocessor     the preprocessor
   * @param entityDescriptor the entity descriptor
   * @param context          the context
   * @param rowBased         the row based
   * @param formatted        the formatted
   * @param emptyMarker      the empty marker
   */
  public SingleSheetXLSEntityIterator(Sheet sheet,
                                      Converter<String, ?> preprocessor,
                                      ComplexTypeDescriptor entityDescriptor,
                                      BeneratorContext context,
                                      boolean rowBased, boolean formatted,
                                      String emptyMarker) {
    this.workbook = sheet.getWorkbook();
    this.preprocessor = preprocessor;
    this.context = context;
    this.rowBased = rowBased;
    this.formatted = formatted;
    this.emptyMarker = emptyMarker;
    this.source = createRawIterator(sheet, rowBased, preprocessor);

    // if not specified explicitly, determine entity type by sheet name
    this.entityDescriptor = entityDescriptor;
    if (this.entityDescriptor == null) {
      String entityTypeName = sheet.getSheetName();
      if (context != null) {
        DataModel dataModel = context.getDataModel();
        this.entityDescriptor = (ComplexTypeDescriptor) dataModel
            .getTypeDescriptor(entityTypeName);
        if (this.entityDescriptor != null) {
          this.entityDescriptor =
              new ComplexTypeDescriptor(entityTypeName + "_",
                  context.getLocalDescriptorProvider());
        } else {
          this.entityDescriptor = createDescriptor(entityTypeName);
        }
      } else {
        this.entityDescriptor = createDescriptor(entityTypeName);
      }
    }

    // parse headers
    parseHeaders();
    if (headers == null) {
      this.source = null; // empty sheet
      return;
    }

    // parse first data row
    DataContainer<Object[]> tmp = this.source.next(sourceContainer.get());
    if (tmp == null) {
      this.source = null; // no data in sheet
      return;
    }
    this.buffer = tmp.getData();
    converter = new Array2EntityConverter(this.entityDescriptor, headers,
        false);
  }


  // DataIterator interface implementation ---------------------------------------------------------------------------

  /**
   * Parse all list.
   *
   * @param uri          the uri
   * @param sheetName    the sheet name
   * @param preprocessor the preprocessor
   * @param type         the type
   * @param context      the context
   * @param rowBased     the row based
   * @param formatted    the formatted
   * @param emptyMarker  the empty marker
   * @return the list
   * @throws IOException            the io exception
   * @throws InvalidFormatException the invalid format exception
   */
  public static List<Entity> parseAll(String uri, String sheetName,
                                      Converter<String, ?> preprocessor,
                                      ComplexTypeDescriptor type,
                                      BeneratorContext context,
                                      boolean rowBased, boolean formatted,
                                      String emptyMarker)
      throws IOException, InvalidFormatException {
    Sheet sheet = loadSheet(uri, sheetName);
    return parseAll(sheet, preprocessor, type, context, rowBased, formatted,
        emptyMarker);
  }

  /**
   * Parse all list.
   *
   * @param sheet        the sheet
   * @param preprocessor the preprocessor
   * @param type         the type
   * @param context      the context
   * @param rowBased     the row based
   * @param formatted    the formatted
   * @param emptyMarker  the empty marker
   * @return the list
   */
  public static List<Entity> parseAll(Sheet sheet,
                                      Converter<String, ?> preprocessor,
                                      ComplexTypeDescriptor type,
                                      BeneratorContext context,
                                      boolean rowBased, boolean formatted,
                                      String emptyMarker) {
    List<Entity> list = new ArrayList<>();
    SingleSheetXLSEntityIterator iterator =
        new SingleSheetXLSEntityIterator(sheet, preprocessor, type,
            context, rowBased, formatted, emptyMarker);
    DataContainer<Entity> container = new DataContainer<>();
    while ((container = iterator.next(container)) != null) {
      list.add(container.getData());
    }
    return list;
  }

  private static Sheet loadSheet(String uri, String sheetName)
      throws IOException {
    Workbook workbook =
        WorkbookFactory.create(IOUtil.getInputStreamForURI(uri));
    Sheet sheet = workbook.getSheet(sheetName);
    if (sheet == null) {
      throw new ConfigurationError(
          "Sheet '" + sheetName + "' not found in file " + uri);
    }
    return sheet;
  }


  // convenience methods ---------------------------------------------------------------------------------------------

  private static String[] normalizeHeaders(Object[] rawHeaders) {
    String[] headers = (String[]) ConverterManager
        .convertAll(rawHeaders, new ToStringConverter(), String.class);
    StringUtil.trimAll(headers);
    int lastNonEmptyIndex = headers.length - 1;
    while (lastNonEmptyIndex >= 0 &&
        StringUtil.isEmpty(headers[lastNonEmptyIndex])) {
      lastNonEmptyIndex--;
    }
    if (lastNonEmptyIndex < headers.length - 1) {
      headers = ArrayUtil.copyOfRange(headers, 0, lastNonEmptyIndex + 1);
    }
    return headers;
  }

  @Override
  public Class<Entity> getType() {
    return Entity.class;
  }


  // private helper methods --------------------------------------------------

  @Override
  public DataContainer<Entity> next(DataContainer<Entity> container) {
    if (source == null) {
      return null;
    }
    Object[] rawData;
    if (buffer != null) {
      rawData = buffer;
      buffer = null;
    } else {
      DataContainer<Object[]> tmp = source.next(sourceContainer.get());
      if (tmp == null) {
        return null;
      }
      rawData = tmp.getData();
    }
    resolveCollections(rawData);
    Entity result = converter.convert(rawData);
    return container.setData(result);
  }

  @Override
  public void close() {
    IOUtil.close(source);
  }

  private void parseHeaders() {
    DataContainer<Object[]> tmp = this.source.next(sourceContainer.get());
    this.headers = (tmp != null ? normalizeHeaders(tmp.getData()) : null);
  }

  private DataIterator<Object[]> createRawIterator(Sheet sheet,
                                                   boolean rowBased,
                                                   Converter<String, ?> preprocessor) {
    XLSLineIterator iterator =
        new XLSLineIterator(sheet, false, formatted, preprocessor);
    if (emptyMarker != null) {
      iterator.setEmptyMarker(emptyMarker);
    }
    if (!rowBased) {
      return new OrthogonalArrayIterator<>(iterator);
    }
    return iterator;
  }

  private ComplexTypeDescriptor createDescriptor(String entityTypeName) {
    ComplexTypeDescriptor descriptor;
    descriptor = new ComplexTypeDescriptor(entityTypeName,
        context.getLocalDescriptorProvider());
    context.addLocalType(descriptor);
    return descriptor;
  }

  private void resolveCollections(Object[] rawData) {
    String colRefPrefix = PlatformDescriptor.getCollectionReferencePrefix();
    for (int i = 0; i < rawData.length; i++) {
      Object cellValue = rawData[i];
      if (cellValue instanceof String &&
          ((String) cellValue).startsWith(colRefPrefix)) {
        String tabName =
            ((String) cellValue).substring(colRefPrefix.length());
        ComponentDescriptor component =
            entityDescriptor.getComponent(headers[i]);
        ComplexTypeDescriptor componentType = (component != null ?
            (ComplexTypeDescriptor) component.getTypeDescriptor() :
            null);
        rawData[i] = mapTabToArray(tabName, componentType);
      }
    }
  }

  private Entity[] mapTabToArray(String tabName, ComplexTypeDescriptor type) {
    Sheet sheet = getSheet(tabName);
    List<Entity> elements =
        parseAll(sheet, preprocessor, type, context, rowBased,
            formatted, emptyMarker);
    return CollectionUtil.toArray(elements, Entity.class);
  }

  private Sheet getSheet(String tabName) {
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet candidate = workbook.getSheetAt(i);
      if (candidate.getSheetName().trim()
          .equalsIgnoreCase(tabName.trim())) {
        return candidate;
      }
    }
    // tab not found
    throw new ConfigurationError("Tab '" + tabName + "' not found" +
        (uri != null ? " in " + uri : ""));
  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + source + "]";
  }

}