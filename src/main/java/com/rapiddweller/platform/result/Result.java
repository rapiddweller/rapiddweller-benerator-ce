package com.rapiddweller.platform.result;

import com.rapiddweller.benerator.consumer.TextFileExporter;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Set;

/**
 * ResultExporter for rapiddweller-benerator-pro ui result functionality.<br/><br/>
 * Created: 1/15/21
 *
 * @author akell
 * @since 1.1.0
 */

public class Result extends TextFileExporter {
  private static final Logger logger = LoggerFactory.getLogger(Result.class);

  // constant
  private String encoding = "UTF-8"; //Charset.defaultCharset().displayName();

  // attributes ------------------------------------------------------------------------------------------------------
  private PrintWriter csvWriter = null;
  private String[] columns;

  // configuration attributes ----------------------------------------------------------------------------------------
  private final Boolean endWithNewLine = false;
  private char separator = '|';
  private final String csvLineSeparator = System.lineSeparator();

  // state attributes ------------------------------------------------------------------------------------------------
  private Boolean lfRequired = false;

  // Callback methods for parent class functionality -----------------------------------------------------------------
  public Result() {
    // Empty public default constructor
  }

  // properties ------------------------------------------------------------------------------------------------------
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public void setSeparator(Character separator) {
    this.separator = separator;
  }

  // Callback methods for parent class functionality -----------------------------------------------------------------

  @Override
  public void startConsumingImpl(Object data) {
    Entity entity = (Entity) data;
    if ((csvWriter == null)) {
      initPrinter(data);
    }
    startCSVFile(entity);
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void startCSVFile(Entity entity) {
    if (lfRequired) {
      csvPrintln(csvWriter);
    } else {
      lfRequired = true;
    }
    int index = 0;
    for (String column : columns) {
      if (index > 0) {
        csvWriter.print(separator);
      }
      Object value = entity.getComponent(column);
      StringBuilder out;
      if (value == null) {
        new StringBuilder();
      } else {
        out = new StringBuilder(plainConverter.convert(value));
        if (out.length() == 0) {
          out = new StringBuilder("\"\"");
        } else if (out.toString().indexOf(separator) >= 0) {
          out = new StringBuilder('"' + out.toString() + '"');
        }
        csvWriter.print(out.toString().replace("\n", "\t"));
        csvWriter.flush();
      }
      index++;
    }
  }

  private void printCSVHeaderRow(PrintWriter printWriter) {
    if (!wasAppended && columns != null) {
      int index = 0;
      for (String column : columns) {
        if (index > 0) {
          printWriter.print(separator);
        }
        printWriter.print(column);
        index++;
      }
      lfRequired = true;
    } else {
      lfRequired = wasAppended && !endWithNewLine;
    }
  }

  @Override
  public void initPrinter(Object data) {
    if (data != null) {
      Entity entity = (Entity) data;
      ComplexTypeDescriptor descriptor = entity.descriptor();
      Set<String> map = ((Entity) data).getComponents().keySet();
      columns = map.toArray(new String[0]);
      String entityName = descriptor.getName().replace('.', '_');
      logger.debug("Name of Entity/Table : {}", entityName);

      String baseFolderCSV = "results/";
      String csvUri = baseFolderCSV + entityName + ".csv";

      wasAppended = IOUtil.isURIAvailable(csvUri);

      csvWriter = IOUtil.getPrinterForURI(csvUri, encoding, true, csvLineSeparator, true);
      postInitPrinter(data, csvWriter);
    } else {
      logger.info("Empty Data, do nothing ...");
    }

  }

  private void postInitPrinter(Object object, PrintWriter csvWriter) {
    Entity entity = (Entity) object;
    // determine columns from entity, if they have not been predefined
    if (columns == null && entity != null) {
      columns = CollectionUtil.toArray(entity.getComponents().keySet());
    }
    printCSVHeaderRow(csvWriter);
  }

  private void preClosePrinter(PrintWriter printWriter) {
    if (endWithNewLine) {
      println(printWriter);
    }
  }

  @Override
  public void close() {
    try {
      if (csvWriter == null) {
        initPrinter(null);
      }
      preClosePrinter(csvWriter);
    } finally {
      if (csvWriter != null) {
        csvWriter.close();
      }
    }
  }

  private void println(PrintWriter printWriter) {
    printWriter.print(lineSeparator);
  }

  private void csvPrintln(PrintWriter printWriter) {
    printWriter.print(csvLineSeparator);
  }
}
