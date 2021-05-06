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

package com.rapiddweller.benerator.gui;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSUMER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NAME;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SOURCE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ATTRIBUTE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_COMMENT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_DATABASE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_EXECUTE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_GENERATE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_REFERENCE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_SETUP;

import com.rapiddweller.benerator.archetype.FolderLayout;
import com.rapiddweller.benerator.main.DBSnapshotTool;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.OrderedMap;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.accessor.GraphAccessor;
import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.common.maven.MavenUtil;
import com.rapiddweller.common.ui.I18NError;
import com.rapiddweller.common.ui.ProgressMonitor;
import com.rapiddweller.common.version.VersionInfo;
import com.rapiddweller.format.html.parser.DefaultHTMLTokenizer;
import com.rapiddweller.format.html.parser.HTMLTokenizer;
import com.rapiddweller.format.text.LFNormalizingStringBuilder;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.FeatureDetail;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.platform.db.DBSystem;
import com.rapiddweller.platform.db.DefaultDBSystem;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates benerator project archetypes.<br/>
 * Created at 30.11.2008 17:59:18
 *
 * @author Volker Bergmann
 * @since 0.5.6
 */
public class ProjectBuilder implements Runnable {

  private static final String TAB = "    ";
  private static final String DBUNIT_SNAPSHOT_FILENAME = "base.dbunit.xml";
  private static final String COMMENT_SNAPSHOT = "Create a valid predefined base data set for regression testing " +
      "by importing a snapshot file";
  private static final String COMMENT_DROP_TABLES = "Drop the current tables and sequences if they already exist";
  private static final String COMMENT_CREATE_TABLES = "Create the tables and sequences";


  private static final Set<String> DB_CONSTRAINT_NAMES = CollectionUtil.toSet("nullable", "maxLength", "type");
  private static final ToStringConverter toStringConverter = new ToStringConverter("");

  /**
   * The Setup.
   */
  protected final Setup setup;
  private final List<Exception> errors;
  private final ProgressMonitor monitor;
  private final FolderLayout folderLayout;
  private final DataModel dataModel;
  /**
   * The Descriptors.
   */
  protected TypeDescriptor[] descriptors;
  /**
   * The Db.
   */
  protected DBSystem db;

  /**
   * Instantiates a new Project builder.
   *
   * @param setup        the setup
   * @param folderLayout the folder layout
   * @param monitor      the monitor
   */
  public ProjectBuilder(Setup setup, FolderLayout folderLayout, ProgressMonitor monitor) {
    this.setup = setup;
    this.errors = new ArrayList<>();
    this.monitor = monitor;
    this.descriptors = new TypeDescriptor[0];
    this.folderLayout = folderLayout;
    this.dataModel = new DataModel();
  }

  private static Map<String, String> defineDbAttributes(Setup setup, DefaultHTMLTokenizer tokenizer) {
    Map<String, String> attributes = tokenizer.attributes();
    attributes.put("url", setup.getDbUrl());
    attributes.put("driver", setup.getDbDriver());
    if (!StringUtil.isEmpty(setup.getDbSchema())) {
      attributes.put("schema", setup.getDbSchema());
    }
	if (!StringUtil.isEmpty(setup.getDbCatalog())) {
	  attributes.put("catalog", setup.getDbCatalog());
	}
    attributes.put("user", setup.getDbUser());
    if (!StringUtil.isEmpty(setup.getDbPassword())) {
      attributes.put("password", setup.getDbPassword());
    }
    return attributes;
  }

  private static void appendStartTag(String nodeName, Map<String, String> attributes,
                                     LFNormalizingStringBuilder writer, boolean wrapAttribs) {
    writer.append('<').append(nodeName);
    writeAttributes(attributes, writer, wrapAttribs);
    writer.append('>');
  }

  private static Map<String, String> defineSetupAttributes(DefaultHTMLTokenizer tokenizer,
                                                           Setup setup) {
    Map<String, String> attributes = tokenizer.attributes();
    if (setup.getEncoding() != null) {
      attributes.put("defaultEncoding", setup.getEncoding());
    }
    if (setup.getDataset() != null) {
      attributes.put("defaultDataset", setup.getDataset());
    }
    if (setup.getLocale() != null) {
      attributes.put("defaultLocale", setup.getLocale());
    }
    if (setup.getLineSeparator() != null) {
      attributes.put("defaultLineSeparator", StringUtil.escape(setup.getLineSeparator()));
    }
    return attributes;
  }

  private static void appendElement(String nodeName, Map<String, String> attributes, LFNormalizingStringBuilder writer, boolean wrapAttribs) {
    writer.append('<').append(nodeName);
    writeAttributes(attributes, writer, wrapAttribs);
    writer.append("/>");
  }

  private static void writeAttributes(Map<String, String> attributes, LFNormalizingStringBuilder writer, boolean wrapAttribs) {
    int i = 0;
    for (Map.Entry<String, String> attribute : attributes.entrySet()) {
      if (wrapAttribs && i > 0) {
        writer.append(TAB).append(TAB);
      } else {
        writer.append(' ');
      }
      writer.append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
      if (wrapAttribs && i < attributes.size() - 1) {
        writer.append('\n');
      }
      i++;
    }
  }

  private static void copyToProject(File srcFile, File projectFolder) throws IOException {
    File dstFile = new File(projectFolder, srcFile.getName());
    FileUtil.copy(srcFile, dstFile, true);
  }

  private static void processComment(DefaultHTMLTokenizer tokenizer, Setup setup,
                                     LFNormalizingStringBuilder writer) throws IOException, ParseException {
    String startText = tokenizer.text();
    int nextToken = tokenizer.nextToken();
    if (nextToken == HTMLTokenizer.END_TAG) {
      writer.append(startText).append(tokenizer.text());
      return;
    }
    if (nextToken != HTMLTokenizer.TEXT) {
      throw new ParseException("Text expected in comment", -1);
    }
    String commentText = tokenizer.text().trim();
    if ((COMMENT_DROP_TABLES.equals(commentText) && setup.getDropScriptFile() == null)
        || (COMMENT_CREATE_TABLES.equals(commentText) && setup.getCreateScriptFile() == null)
        || (COMMENT_SNAPSHOT.equals(commentText) && setup.getDbSnapshot() == null)) {
      while (tokenizer.nextToken() != HTMLTokenizer.END_TAG) {
        // skip all elements until comment end
      }
    } else {
      // write comment start and content
      writer.append(startText).append(tokenizer.text());
    }
  }

  private static boolean isDefaultValue(Object value, String name) {
    return "nullable".equals(name) && (value == null || ((Boolean) value));
  }

  private static void appendEndElement(String nodeName, LFNormalizingStringBuilder writer) {
    writer.append("</").append(nodeName).append(">");
  }

  private static void format(FeatureDetail<?> detail, Map<String, String> attributes) {
    if (!ATT_NAME.equals(detail.getName()) && detail.getValue() != null && !isDbConstraint(detail.getName())) {
      attributes.put(detail.getName(), toStringConverter.convert(detail.getValue()));
    }
  }

  private static boolean isDbConstraint(String name) {
    return DB_CONSTRAINT_NAMES.contains(name);
  }

  @Override
  public void run() {
    try {
      // read data model
      if (setup.isDatabaseProject()) {
        parseDatabaseMetaData();
        if (monitor != null) {
          monitor.setMaximum(5 + descriptors.length);
        }
        advanceMonitor();
      } else {
        monitor.setMaximum(5);
      }

      createFolderLayout();
      applyArchetype(setup, monitor);
      createPOM();
      createProjectPropertiesFile();

      // copy import files
      copyImportFiles();

      // create db snapshot project.dbunit.xml
      Exception exception = null;
      if (setup.isDatabaseProject() && !"none".equals(setup.getDbSnapshot()) && !setup.isShopProject()) {
        try {
          createDbSnapshot();
        } catch (Exception e) {
          exception = e;
        }
      }

      // create project.ben.xml (including imports)
      createBeneratorXml();

      createEclipseProject();

      if (exception != null) {
        throw exception;
      }
    } catch (Exception e) {
      errors.add(e);
      e.printStackTrace();
    } finally {
      if (db != null) {
        db.close();
      }
      if (monitor != null) {
        monitor.setProgress(monitor.getMaximum());
      }
    }
  }

  private void parseDatabaseMetaData() {
    noteMonitor("scanning database");
    if (monitor != null) {
      monitor.setProgress(0);
    }
    db = getDBSystem(setup);
    descriptors = db.getTypeDescriptors();
  }

  private void createFolderLayout() {
    String groupId = setup.getGroupId();
    String pkgFolder = "/" + (StringUtil.isEmpty(groupId) ? "" : groupId.replace('.', '/') + '/') + setup.getProjectName();
    haveSubFolder("src/main/java" + pkgFolder);
    haveSubFolder("src/main/resources" + pkgFolder);
    haveSubFolder("src/test/java" + pkgFolder);
    haveSubFolder("src/test/resources" + pkgFolder);
  }

  private void applyArchetype(Setup setup, ProgressMonitor monitor) throws IOException {
    // create project files
    monitor.setNote("Creating files...");
    setup.getArchetype().copyFilesTo(setup.getProjectFolder(), folderLayout);
  }

  private void createEclipseProject() {
    setup.projectFile(".project"); // call this for existence check and overwrite error
    if (setup.isEclipseProject()) {
      noteMonitor("Creating Eclipse project");
      MavenUtil.invoke("eclipse:eclipse", setup.getProjectFolder(), !setup.isOffline());
    }
    advanceMonitor();
  }

  private void haveSubFolder(String relativePath) {
    FileUtil.ensureDirectoryExists(setup.subDirectory(folderLayout.mapSubFolder(relativePath)));
  }

  /**
   * Get errors exception [ ].
   *
   * @return the exception [ ]
   */
  public Exception[] getErrors() {
    return CollectionUtil.toArray(errors, Exception.class);
  }

  private void advanceMonitor() {
    if (monitor != null) {
      monitor.advance();
    }
  }

  private void noteMonitor(String note) {
    if (monitor != null) {
      monitor.setNote(note);
    }
  }

  private void copyImportFiles() {
    if (!setup.isShopProject()) {
      copyImportFile(setup.getDropScriptFile());
      copyImportFile(setup.getCreateScriptFile());
    }
  }

  private void copyImportFile(File importFile) {
    if (importFile == null) {
      return;
    }
    if (importFile.exists()) {
      noteMonitor("Importing " + importFile);
      File copy = setup.projectFile(importFile.getName());
      try {
        IOUtil.copyFile(importFile.getAbsolutePath(), copy.getAbsolutePath());
      } catch (IOException e) {
        throw new I18NError("ErrorCopying", e, importFile.getAbsolutePath(), copy);
      }
    } else {
      errors.add(new I18NError("FileNotFound",
          new FileNotFoundException(importFile.getAbsolutePath()), importFile));
    }
    advanceMonitor();
  }

  private void createDbSnapshot() {
    String format = setup.getDbSnapshot();
    File file = setup.projectFile(setup.getDbSnapshotFile());
	DBSnapshotTool.export(setup.getDbUrl(), setup.getDbDriver(), setup.getDbSchema(), setup.getDbCatalog(),
        setup.getDbUser(), setup.getDbPassword(), file.getAbsolutePath(), setup.getEncoding(), format,
        null, monitor);
  }

  private void createPOM() {
    noteMonitor("creating pom.xml");
    resolveVariables(new File(setup.getProjectFolder(), "pom.xml"));
  }

  private void createProjectPropertiesFile() {
    String filename = "benerator.properties";
    File file = new File(setup.getProjectFolder(), filename);
    if (file.exists()) {
      noteMonitor("creating " + filename);
      resolveVariables(file);
    }
  }

  /**
   * Resolve variables file.
   *
   * @param file the file
   * @return the file
   */
  public File resolveVariables(File file) {
    try {
      String content = IOUtil.getContentOfURI(file.getAbsolutePath());
      content = resolveVariables(content);
      file.delete();
      IOUtil.writeTextFile(file.getAbsolutePath(), content, getXMLEncoding());
      return file;
    } catch (IOException e) {
      throw new I18NError("ErrorCreatingFile", e, file);
    } finally {
      advanceMonitor();
    }
  }

  private String getXMLEncoding() {
    String configuredEncoding = setup.getEncoding();
    return (StringUtil.isEmpty(configuredEncoding) ? Encodings.UTF_8 : configuredEncoding);
  }

  private String resolveVariables(String content) {
    return replaceVariables(content);
  }

  /**
   * Create benerator xml.
   *
   * @throws IOException    the io exception
   * @throws ParseException the parse exception
   */
  public void createBeneratorXml() throws IOException, ParseException {
    File descriptorFile = new File(setup.getProjectFolder(), "benerator.xml");
    if (descriptorFile.exists()) { // not applicable for XML schema based generation
      BufferedReader reader = IOUtil.getReaderForURI(descriptorFile.getAbsolutePath());
      DefaultHTMLTokenizer tokenizer = new DefaultHTMLTokenizer(reader);
      String lineSeparator = setup.getLineSeparator();
      if (StringUtil.isEmpty(lineSeparator)) {
        lineSeparator = SystemInfo.getLineSeparator();
      }
      LFNormalizingStringBuilder writer = new LFNormalizingStringBuilder(lineSeparator);
      while (tokenizer.nextToken() != HTMLTokenizer.END) {
        processToken(setup, tokenizer, writer);
      }
      String xml = writer.toString();
      xml = resolveVariables(xml);
      IOUtil.writeTextFile(descriptorFile.getAbsolutePath(), xml, "UTF-8");
    }
    monitor.advance();
  }

  private void processToken(Setup setup,
                            DefaultHTMLTokenizer tokenizer, LFNormalizingStringBuilder writer)
      throws IOException, ParseException {

    switch (tokenizer.tokenType()) {
      case HTMLTokenizer.START_TAG: {
        String nodeName = tokenizer.name();
        if (EL_SETUP.equals(nodeName)) {
          appendStartTag(nodeName, defineSetupAttributes(tokenizer, setup), writer, true);
        } else if (EL_COMMENT.equals(nodeName)) {
          processComment(tokenizer, setup, writer);
        } else {
          writer.append(tokenizer.text());
        }
        break;
      }
      case HTMLTokenizer.CLOSED_TAG: {
        String nodeName = tokenizer.name();
        if (EL_DATABASE.equals(nodeName) && setup.isDatabaseProject()) {
          appendElement(nodeName, defineDbAttributes(setup, tokenizer), writer, true);
        } else if (EL_EXECUTE.equals(nodeName)) {
          Map<String, String> attributes = tokenizer.attributes();
          String uri = attributes.get("uri");
          if ("{drop_tables.sql}".equals(uri)) {
            if (setup.getDropScriptFile() != null) {
              File dropScriptFile = setup.getDropScriptFile();
              copyToProject(dropScriptFile, setup.getProjectFolder());
              attributes.put("uri", dropScriptFile.getName());
              appendElement(nodeName, attributes, writer, false);
            }
          } else if ("{create_tables.sql}".equals(uri)) {
            if (setup.getCreateScriptFile() != null) {
              File createScriptFile = setup.getCreateScriptFile();
              copyToProject(createScriptFile, setup.getProjectFolder());
              attributes.put("uri", createScriptFile.getName());
              appendElement(nodeName, attributes, writer, false);
            }
          } else {
            writer.append(tokenizer.text());
          }
        } else if (EL_GENERATE.equals(nodeName)) {
          Map<String, String> attributes = tokenizer.attributes();
          if (DBUNIT_SNAPSHOT_FILENAME.equals(attributes.get(ATT_SOURCE))) {
            if (setup.getDbSnapshot() != null) {
              appendElement(nodeName, attributes, writer, false);
            }
          } else if ("tables".equals(attributes.get(ATT_TYPE))) {
            generateTables(setup, writer);
          } else {
            writer.append(tokenizer.text());
          }
        } else {
          writer.append(tokenizer.text());
        }
        break;
      }
      default:
        writer.append(tokenizer.text());
    }
  }

  private void generateTables(Setup setup, LFNormalizingStringBuilder writer) {
    DBSystem db = getDBSystem(setup);
    TypeDescriptor[] descriptors = db.getTypeDescriptors();
    for (TypeDescriptor descriptor : descriptors) {
      ComplexTypeDescriptor complexType = (ComplexTypeDescriptor) descriptor;
      final String name = complexType.getName();
      InstanceDescriptor iDesc = new InstanceDescriptor(name, db, complexType);
      if (setup.getDbSnapshot() != null) {
        iDesc.setCount(ExpressionUtil.constant(0L));
      } else {
        iDesc.setCount(ExpressionUtil.constant(db.countEntities(name)));
      }
      generateTable(iDesc, writer);
    }
  }

  private DBSystem getDBSystem(Setup setup) {
    DBSystem db = new DefaultDBSystem("db", setup.getDbUrl(), setup.getDbDriver(), setup.getDbUser(), setup.getDbPassword(), dataModel);
    if (setup.getDbSchema() != null) {
      db.setSchema(setup.getDbSchema());
    }
	if (setup.getDbCatalog() != null) {
	  db.setCatalog(setup.getDbCatalog());
	}
    dataModel.addDescriptorProvider(db);
    return db;
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private void generateTable(InstanceDescriptor descriptor, LFNormalizingStringBuilder writer) {
    ComplexTypeDescriptor type = (ComplexTypeDescriptor) descriptor.getTypeDescriptor();
    Map<String, String> attributes = new OrderedMap<>();
    for (FeatureDetail<?> detail : descriptor.getDetails()) {
      Object value = detail.getValue();
      if (value != null && !isDefaultValue(value, detail.getName())) {
        if (value instanceof Expression) {
          value = ((Expression<?>) value).evaluate(null);
        }
        attributes.put(detail.getName(), toStringConverter.convert(value));
      }
    }
    attributes.put(ATT_CONSUMER, "db");
    appendStartTag(EL_GENERATE, attributes, writer, false);
    writer.append('\n');

    for (ComponentDescriptor cd : type.getComponents()) {
      addAttribute(cd, writer);
    }
    writer.append(TAB);
    appendEndElement(EL_GENERATE, writer);
    writer.append("\n\n").append(TAB);
  }

  private void addAttribute(ComponentDescriptor component, LFNormalizingStringBuilder writer) {
    // normalize
    boolean nullable = (component.isNullable() == null || component.isNullable());
    if (component.getMaxCount() != null && component.getMaxCount().evaluate(null) == 1) {
      component.setMaxCount(null);
    }
    if (component.getMinCount() != null && component.getMinCount().evaluate(null) == 1) {
      component.setMinCount(null);
    }
    if (nullable) {
      component.setNullable(null);
    }

    String elementName = null;
    if (component instanceof PartDescriptor) {
      elementName = EL_ATTRIBUTE;
    } else if (component instanceof ReferenceDescriptor) {
      elementName = EL_REFERENCE;
    } else if (component instanceof IdDescriptor) {
      elementName = EL_ID;
    } else {
      throw new UnsupportedOperationException("Component descriptor type not supported: " +
          component.getClass().getSimpleName());
    }

    Map<String, String> attributes = new OrderedMap<>();
    attributes.put(ATT_NAME, component.getName());
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) (component.getType() != null ?
        dataModel.getTypeDescriptor(component.getType()) :
        component.getLocalType());
    if (type != null) {
      for (FeatureDetail<?> detail : type.getDetails()) {
        format(detail, attributes);
      }
    }
    for (FeatureDetail<?> detail : component.getDetails()) {
      format(detail, attributes);
    }
    if (nullable) {
      attributes.put("nullQuota", "1");
    }

    writer.append(TAB).append(TAB).append("<!--").append(elementName);
    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      writer.append(' ').append(entry.getKey()).append("=\"").append(entry.getValue()).append('"');
    }
    writer.append(" /-->");
    writer.append('\n');
  }

  private String replaceVariables(String text) {
    int varStart = 0;
    Context context = new DefaultContext();
    context.set("setup", setup);
    context.set("version", VersionInfo.getInfo("benerator"));
    while ((varStart = text.indexOf("${", varStart)) >= 0) {
      int varEnd = text.indexOf("}", varStart);
      if (varEnd < 0) {
        throw new ConfigurationError("'${' without '}'");
      }
      String template = text.substring(varStart, varEnd + 1);
      String path = template.substring(2, template.length() - 1).trim();
      GraphAccessor accessor = new GraphAccessor(path);
      Object varValue = accessor.getValue(context);
      String varString = toStringConverter.convert(varValue);
      if (!StringUtil.isEmpty(varString)) {
        text = text.replace(template, varString);
      }
      varStart = varEnd;
    }
    text = text.replace("\n        defaultEncoding=\"\"", "");
    text = text.replace("\n        defaultDataset=\"\"", "");
    text = text.replace("\n        defaultLocale=\"\"", "");
    text = text.replace("\n        defaultLineSeparator=\"\"", "");
    return text;
  }

}
