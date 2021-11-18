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

package com.rapiddweller.benerator.template.xmlanon;

import com.rapiddweller.benerator.template.TemplateInputReader;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.exception.ParseException;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.format.xls.XLSUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads XLS documents for a multi-file XML anonymization.<br/><br/>
 * Created: 06.03.2014 08:25:43
 * @author Volker Bergmann
 * @since 0.9.0
 */
public class XmlAnonInputReader implements TemplateInputReader {

  @Override
  public void parse(String uri, Context context) throws IOException, ParseException {
    AnonymizationSetup setup = parseXls(uri);
    verifyXMLFileSettings(setup);
    context.set("setup", setup);
  }

  private static AnonymizationSetup parseXls(String xlsUri) throws IOException {
    Workbook workbook = null;
    try (InputStream in = IOUtil.getInputStreamForURI(xlsUri)) {
      workbook = WorkbookFactory.create(in);
      Sheet sheet = workbook.getSheetAt(0);

      // parse header information
      int varnameColumnIndex = -1;
      ArrayList<String> files = new ArrayList<>();
      Row headerRow = sheet.getRow(0);
      Assert.notNull(headerRow, "header row");
      for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
        String header = headerRow.getCell(i).getStringCellValue();
        if ("varname".equals(header)) {
          varnameColumnIndex = i;
          break;
        } else {
          if (StringUtil.isEmpty(header)) {
            throw new ConfigurationError("Filename missing in column header #" + i + " of Excel document " + xlsUri);
          }
          files.add(header);
        }
      }
      if (varnameColumnIndex == -1) {
        throw new ConfigurationError("No 'varname' header defined in Excel document " + xlsUri);
      }
      if (files.size() == 0) {
        throw new ConfigurationError("No files specified in Excel document " + xlsUri);
      }

      // parse anonymization rows
      List<Anonymization> anonymizations = new ArrayList<>();
      for (int rownum = 1; rownum <= sheet.getLastRowNum(); rownum++) {
        Row row = sheet.getRow(rownum);
        if (XLSUtil.isEmpty(row)) {
          continue;
        }
        Cell varnameCell = row.getCell(varnameColumnIndex);
        if (varnameCell == null || StringUtil.isEmpty(varnameCell.getStringCellValue())) {
          throw new ConfigurationError("'varname' cell empty in table row #" + (rownum + 1));
        }
        Anonymization anon = new Anonymization(varnameCell.getStringCellValue());
        // parse locators
        for (int colnum = 0; colnum < varnameColumnIndex; colnum++) {
          Cell cell = row.getCell(colnum);
          String path = (cell != null ? cell.getStringCellValue() : null);
          if (!StringUtil.isEmpty(path)) {
            List<String> tokens = XPathTokenizer.tokenize(path);
            String entityPath = XPathTokenizer.merge(tokens, 0, tokens.size() - 2);
            String entity = normalizeXMLPath(XPathTokenizer.nodeName(tokens.get(tokens.size() - 2)));
            String attribute = normalizeXMLPath(tokens.get(tokens.size() - 1));
            anon.addLocator(new Locator(files.get(colnum), path, entityPath, entity, attribute));
          }
        }
        // parse settings
        for (int colnum = varnameColumnIndex + 1; colnum < row.getLastCellNum() - 1; colnum += 2) {
          String key = row.getCell(colnum).getStringCellValue();
          String value = row.getCell(colnum + 1).getStringCellValue();
          if (!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value)) {
            anon.addSetting(key, value);
          }
        }
        anonymizations.add(anon);
      }
      return new AnonymizationSetup(files, anonymizations);
    } finally {
      workbook.close();
    }
  }

  private static void verifyXMLFileSettings(AnonymizationSetup setup) {
    for (String file : setup.getFiles()) {
      if (StringUtil.isEmpty(System.getProperty(file))) {
        throw new ConfigurationError("No concrete file specified for file variable " + file);
      }
    }
  }

  private static String normalizeXMLPath(String path) {
    return path.replace('.', '_').replace('-', '_');
  }

}
