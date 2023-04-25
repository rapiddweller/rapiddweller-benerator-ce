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

import com.rapiddweller.benerator.archetype.FolderLayout;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.main.DBSnapshotTool;
import com.rapiddweller.common.CollectionUtil;
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
import com.rapiddweller.jdbacl.JDBCDriverInfo;
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
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.platform.db.DefaultDBSystem;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;

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

/**
 * Creates benerator project archetypes.<br/>
 * Created at 30.11.2008 17:59:18
 * @author Volker Bergmann
 * @since 0.5.6
 */
public class ProjectBuilder implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(ProjectBuilder.class);

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
  protected TypeDescriptor[] descriptors;
  protected AbstractDBSystem db;

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

  private static void copyToProject(File srcFile, File projectFolder) {
    File dstFile = new File(projectFolder, srcFile.getName());
    FileUtil.copy(srcFile, dstFile, true);
  }

  private static void appendDatabase(String nodeName, Setup setup, DefaultHTMLTokenizer tokenizer, LFNormalizingStringBuilder writer) {

    // create environment file "conf.env.properties"
    String envName = "environment";
    File envFile = new File(setup.getProjectFolder(), envName + ".env.properties");
    StringBuilder builder = new StringBuilder();

    // must set attributes Map first to avoid tokenizer change after call defineDbAttributes() functions
    Map<String, String> attributes = new HashMap<>(tokenizer.attributes());
    Map<String, String> elements = defineDbAttributes(setup, tokenizer);
    String DBMS = "";

    JDBCDriverInfo jdbcDriverInfo = setup.getJdbcDriverType();
    if (jdbcDriverInfo != null){
      DBMS = jdbcDriverInfo.getId().toLowerCase();
    }
    for (Map.Entry<String, String> element : elements.entrySet()){
      if (element.getKey().equals("id")){
        continue;
      }
      builder.append(String.format("%s.db.%s=%s\n", DBMS, element.getKey(), element.getValue()));
    }
    try (FileWriter envWriter = new FileWriter(envFile)){
      envWriter.write(builder.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // config database tag attributes
    attributes.put("environment", envName);
    attributes.put("system", DBMS);

    // continues to write project.ben.xml
    appendElement(nodeName, attributes, writer, true);
  }

  private static void processComment(DefaultHTMLTokenizer tokenizer, Setup setup, LFNormalizingStringBuilder writer) {
    try {
      String startText = tokenizer.text();
      int nextToken = tokenizer.nextToken();
      if (nextToken == HTMLTokenizer.END_TAG) {
        writer.append(startText).append(tokenizer.text());
        return;
      }
      if (nextToken != HTMLTokenizer.TEXT) {
        throw BeneratorExceptionFactory.getInstance().missingInfo("Text expected in comment");
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
    } catch (IOException | ParseException e) {
      throw BeneratorExceptionFactory.getInstance().internalError("Error processing comment", e);
    }
  }

  private static boolean isDefaultValue(Object value, String name) {
    return "nullable".equals(name) && (value == null || ((Boolean) value));
  }

  private static void appendEndElement(LFNormalizingStringBuilder writer) {
    writer.append("</").append(com.rapiddweller.benerator.engine.DescriptorConstants.EL_GENERATE).append(">");
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
      Exception exception = createSnapshotIfNecessary();

      // create project.ben.xml (including imports, environment)
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

  private Exception createSnapshotIfNecessary() {
    Exception exception = null;
    if (setup.isDatabaseProject() && !"none".equals(setup.getDbSnapshot()) && !setup.isShopProject() && !setup.getDbDriver().contains("h2")) {
      try {
        createDbSnapshot();
      } catch (Exception e) {
        exception = e;
      }
    }
    return exception;
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

  private void applyArchetype(Setup setup, ProgressMonitor monitor) {
    try {
      // create project files
      monitor.setNote("Creating files...");
      setup.getArchetype().copyFilesTo(setup.getProjectFolder(), folderLayout);
    } catch (IOException e) {
      throw BeneratorExceptionFactory.getInstance().internalError(
          "Error applying archetype " + setup.getArchetype().getId(), e);
    }
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
    if (importFile == null || importFile.getName().isBlank()) {
      return;
    }
    if (importFile.exists()) {
      noteMonitor("Importing " + importFile);
      File copy = setup.projectFile(importFile.getName());
      try {
        IOUtil.copyFile(importFile.getAbsolutePath(), copy.getAbsolutePath());
      } catch (Exception e) {
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
    DBSnapshotTool.export(setup.getDbUrl(), setup.getDbDriver(), setup.getDbCatalog(), setup.getDbSchema(),
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

  public File resolveVariables(File file) {
    try {
      String content = IOUtil.getContentOfURI(file.getAbsolutePath());
      content = resolveVariables(content);
      if (!file.delete()) {
        logger.error("Deletion failed for file {}", file);
      }
      IOUtil.writeTextFile(file.getAbsolutePath(), content, getXMLEncoding());
      return file;
    } catch (Exception e) {
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

  public void createBeneratorXml() {
    try {
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
    } catch (IOException e) {
      throw BeneratorExceptionFactory.getInstance().internalError(
          "Error creating benerator XML file ", e);
    } catch (ParseException e) {
      throw BeneratorExceptionFactory.getInstance().internalError(
          "Error parsing archetype XML in " + setup.getArchetype().getId(), e);
    }
  }

  private void processToken(Setup setup, DefaultHTMLTokenizer tokenizer, LFNormalizingStringBuilder writer) {
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
          // create env properties file and config database by using environment
          appendDatabase(nodeName, setup, tokenizer, writer);

          // old way to config database configuration - not using anymore
//          appendElement(nodeName, defineDbAttributes(setup, tokenizer), writer, true);
        } else if (EL_EXECUTE.equals(nodeName)) {
          processExecute(setup, tokenizer, writer, nodeName);
        } else if (EL_GENERATE.equals(nodeName)) {
          processGenerate(setup, tokenizer, writer, nodeName);
        } else {
          writer.append(tokenizer.text());
        }
        break;
      }
      default:
        writer.append(tokenizer.text());
    }
  }

  private void processExecute(Setup setup, DefaultHTMLTokenizer tokenizer, LFNormalizingStringBuilder writer, String nodeName) {
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
  }

  private void processGenerate(Setup setup, DefaultHTMLTokenizer tokenizer, LFNormalizingStringBuilder writer, String nodeName) {
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
  }

  private void generateTables(Setup setup, LFNormalizingStringBuilder writer) {
    AbstractDBSystem database = getDBSystem(setup);
    TypeDescriptor[] descs = database.getTypeDescriptors();
    for (TypeDescriptor descriptor : descs) {
      ComplexTypeDescriptor complexType = (ComplexTypeDescriptor) descriptor;
      final String name = complexType.getName();
      InstanceDescriptor iDesc = new InstanceDescriptor(name, database, complexType);
      if (setup.getDbSnapshot() != null) {
        iDesc.setCount(ExpressionUtil.constant(0L));
      } else {
        iDesc.setCount(ExpressionUtil.constant(database.countEntities(name)));
      }
      generateTable(iDesc, writer);
    }
  }

  private AbstractDBSystem getDBSystem(Setup setup) {
    AbstractDBSystem database = new DefaultDBSystem("db", setup.getDbUrl(), setup.getDbDriver(), setup.getDbUser(), setup.getDbPassword(), dataModel);
    if (setup.getDbSchema() != null) {
      database.setSchema(setup.getDbSchema());
    }
    if (setup.getDbCatalog() != null) {
      database.setCatalog(setup.getDbCatalog());
    }
    dataModel.addDescriptorProvider(database);
    return database;
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
    appendEndElement(writer);
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
    String elementName = getElementName(component);
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

  private String getElementName(ComponentDescriptor component) {
    String elementName;
    if (component instanceof PartDescriptor) {
      elementName = EL_ATTRIBUTE;
    } else if (component instanceof ReferenceDescriptor) {
      elementName = EL_REFERENCE;
    } else if (component instanceof IdDescriptor) {
      elementName = EL_ID;
    } else {
      throw BeneratorExceptionFactory.getInstance().programmerUnsupported("Component descriptor type not supported: " +
          component.getClass().getSimpleName());
    }
    return elementName;
  }

  private String replaceVariables(String text) {
    int varStart = 0;
    Context context = new DefaultContext();
    context.set("setup", setup);
    context.set("version", VersionInfo.getInfo("benerator"));
    while ((varStart = text.indexOf("${", varStart)) >= 0) {
      int varEnd = text.indexOf("}", varStart);
      if (varEnd < 0) {
        throw BeneratorExceptionFactory.getInstance().configurationError("'${' without '}'");
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
