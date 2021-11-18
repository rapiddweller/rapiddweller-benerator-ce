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

package com.rapiddweller.platform.dbunit;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

/**
 * Reads the nested form of a DbUnit XML dataset file and provides its content as Entities
 * through the {@link DataIterator} interface.<br/><br/>
 * Created: 20.09.2011 07:55:49
 * @author Volker Bergmann
 * @since 0.7.2
 */
public class NestedDbUnitEntityIterator extends AbstractDbUnitEntityIterator {

  private Table currentTable;

  public NestedDbUnitEntityIterator(String uri, BeneratorContext context) {
    super(uri, context);
    DbUnitUtil.skipRootElement(reader);
    this.currentTable = null;
  }

  // DataIterator interface implementation ---------------------------------------------------------------------------

  @Override
  public DataContainer<Entity> next(DataContainer<Entity> container) {
    try {
      DbUnitUtil.skipNonStartTags(reader);
      if (reader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
        return null;
      }
      String elementName = reader.getLocalName();
      Row row;
      if ("table".equals(elementName)) {
        row = parseTableAndFirstRow();
      } else if ("row".equals(elementName) || "column".equals(elementName)) {
        row = parseRow();
      } else {
        throw new SyntaxError("Not an allowed element", "<" + elementName + ">");
      }
      if (row == null) {
        return null;
      }
      ComplexTypeDescriptor descriptor = getType(row);
      Entity result = new Entity(descriptor);
      String[] cells = row.getValues();
      for (int i = 0; i < cells.length; i++) {
        String rowValue = String.valueOf(ScriptUtil.evaluate(cells[i], context));
        result.setComponent(row.getColumnName(i), rowValue);
      }
      return container.setData(result);
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private Row parseTableAndFirstRow() throws XMLStreamException {
    String tableName = reader.getAttributeValue(null, "name");
    currentTable = new Table(tableName);
    parseColumns();
    return parseRow();
  }

  protected void parseColumns() throws XMLStreamException {
    String column;
    while ((column = parseColumn()) != null) {
      currentTable.addColumn(column);
    }
  }

  private String parseColumn() throws XMLStreamException {
    // format: <column>column_name</column>
    // parse <column>
    reader.nextTag();
    if (!"column".equals(reader.getLocalName())) {
      return null;
    }
    reader.next();
    // parse column_name
    String columnName = reader.getText();
    // parse </column>
    reader.next();
    return columnName;
  }

  private Row parseRow() throws XMLStreamException {
    if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      return null;
    }
    if ("row".equals(reader.getLocalName())) {
      reader.next();
    }
    return parseValues();
  }

  private Row parseValues() throws XMLStreamException {
    String value;
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    while ((value = parseValue()) != null) {
      builder.add(value);
    }
    return new Row(currentTable.name, currentTable.getColumnNames(), builder.toArray());
  }

  private String parseValue() throws XMLStreamException {
    // <value>cell_value</value>
    // parse <value>
    reader.nextTag();
    if (!"value".equals(reader.getLocalName())) {
      return null;
    }
    reader.next();
    // parse cell_value
    String columnName = reader.getText();
    // parse </value>
    reader.next();
    return columnName;
  }

  private static class Table {
    protected final String name;
    private String[] columnNames;

    public Table(String name) {
      this.name = name;
      this.columnNames = null;
    }

    public void addColumn(String column) {
      this.columnNames = ArrayUtil.append(column, this.columnNames);
    }

    @Override
    public String toString() {
      return name + '[' + ArrayFormat.format(columnNames) + ']';
    }

    public String[] getColumnNames() {
      return columnNames;
    }
  }

}
