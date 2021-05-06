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

import com.rapiddweller.benerator.archetype.Archetype;
import com.rapiddweller.benerator.archetype.ArchetypeManager;
import com.rapiddweller.benerator.main.DBSnapshotTool;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.bean.ObservableBean;
import com.rapiddweller.common.ui.I18NError;
import com.rapiddweller.jdbacl.JDBCDriverInfo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * Assembles all data useful for creating benerator archetypes.<br/>
 * <br/>
 * Created at 29.11.2008 22:44:12
 *
 * @author Volker Bergmann
 * @since 0.5.6
 */
public class Setup implements ObservableBean {

  private static final long serialVersionUID = 3353941855988168161L;

  private static final String DEFAULT_PROJECT_NAME = "myproject";
  private static final String DEFAULT_GROUP_ID = "com.my";
  private static final String DEFAULT_PROJECT_VERSION = "1.0";

  private static final String DEFAULT_DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

  private final PropertyChangeSupport changeSupport;

  private String projectName;
  private String groupId;
  private String version;
  private File projectFolder;

  private Archetype archetype;

  private boolean eclipseProject;
  private boolean offline;
  private boolean overwrite;

  private String encoding;
  private String lineSeparator;

  private String locale;
  private String dataset;

  private JDBCDriverInfo jdbcDriverType;
  private String dbUrl;
  private String dbDriver;
  private String dbPassword;
  private String dbSchema;
  private String dbCatalog;
  private String dbUser;

  private File dropScriptFile;
  private File createScriptFile;
  private String dbSnapshot;

  private MavenDependency[] dbDependencies;

  /**
   * Instantiates a new Setup.
   */
  public Setup() {
    this.changeSupport = new PropertyChangeSupport(this);

    projectFolder = new File(SystemInfo.getCurrentDir());
    setProjectName(DEFAULT_PROJECT_NAME);
    setGroupId(DEFAULT_GROUP_ID);
    setVersion(DEFAULT_PROJECT_VERSION);
    eclipseProject = false;
    offline = false;
    overwrite = false;

    setEncoding(SystemInfo.getFileEncoding());
    setLineSeparator(SystemInfo.getLineSeparator());
    setLocale(Locale.getDefault().toString());
    setDataset(LocaleUtil.getDefaultCountryCode());

    String url = System.getenv("DEFAULT_DATABASE");
    if (!StringUtil.isEmpty(url)) {
      for (JDBCDriverInfo candidate : JDBCDriverInfo.getInstances()) {
        String prefix = candidate.getUrlPrefix();
        if (url.startsWith(prefix)) {
          setJdbcDriverType(candidate);
          break;
        }
      }
      if (jdbcDriverType == null) {
        setJdbcDriverType(JDBCDriverInfo.HSQL);
      }
    }
    setDbUrl(url);
    setDbDriver(DEFAULT_DB_DRIVER);
    setDbUser(SystemInfo.getUserName());
    setDbSnapshot("dbunit");
    this.dbDependencies = new MavenDependency[0]; // TODO v0.8 handle maven dependencies
    if (archetype == null) {
      setArchetype(ArchetypeManager.getInstance().getDefaultArchetype());
    }
  }

  /**
   * Gets project name.
   *
   * @return the project name
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * Sets project name.
   *
   * @param projectName the project name
   */
  public void setProjectName(String projectName) {
    String oldName = this.projectName;
    this.projectName = projectName;
    changeSupport.firePropertyChange("projectName", oldName, this.projectName);
    // if user had no value or the same value before, update it
    if (this.dbSchema == null || NullSafeComparator.equals(oldName, this.dbUser)) {
      setDbUser(projectName);
    }
  }

  /**
   * Gets group id.
   *
   * @return the group id
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Sets group id.
   *
   * @param groupId the group id
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  /**
   * Gets version.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets version.
   *
   * @param version the version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Sets archetype.
   *
   * @param archetype the archetype
   */
  public void setArchetype(Archetype archetype) {
    Archetype oldValue = this.archetype;
    this.archetype = archetype;
    changeSupport.firePropertyChange("eclipseProject", oldValue, this.archetype);
  }

  /**
   * Gets archetype.
   *
   * @return the archetype
   */
  public Archetype getArchetype() {
    return archetype;
  }

  /**
   * Is eclipse project boolean.
   *
   * @return the boolean
   */
  public boolean isEclipseProject() {
    return eclipseProject;
  }

  /**
   * Sets eclipse project.
   *
   * @param eclipseProject the eclipse project
   */
  public void setEclipseProject(boolean eclipseProject) {
    boolean oldValue = this.eclipseProject;
    this.eclipseProject = eclipseProject;
    changeSupport.firePropertyChange("eclipseProject", oldValue, this.eclipseProject);
  }

  /**
   * Is offline boolean.
   *
   * @return the boolean
   */
  public boolean isOffline() {
    return offline;
  }

  /**
   * Sets offline.
   *
   * @param offline the offline
   */
  public void setOffline(boolean offline) {
    this.offline = offline;
  }

  /**
   * Is overwrite boolean.
   *
   * @return the boolean
   */
  public boolean isOverwrite() {
    return overwrite;
  }

  /**
   * Sets overwrite.
   *
   * @param overwrite the overwrite
   */
  public void setOverwrite(boolean overwrite) {
    this.overwrite = overwrite;
  }

  /**
   * Gets project folder.
   *
   * @return the project folder
   */
  public File getProjectFolder() {
    return projectFolder;
  }

  /**
   * Sets project folder.
   *
   * @param projectFolder the project folder
   */
  public void setProjectFolder(File projectFolder) {
    File oldValue = this.projectFolder;
    this.projectFolder = projectFolder;
    changeSupport.firePropertyChange("projectFolder", oldValue, this.projectFolder);
    if (DEFAULT_PROJECT_NAME.equals(projectName)) {
      setProjectName(projectFolder.getName());
    }
  }

  /**
   * Gets encoding.
   *
   * @return the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Sets encoding.
   *
   * @param encoding the encoding
   */
  public void setEncoding(String encoding) {
    String oldValue = this.encoding;
    this.encoding = encoding;
    changeSupport.firePropertyChange("encoding", oldValue, this.encoding);
  }

  /**
   * Gets line separator.
   *
   * @return the line separator
   */
  public String getLineSeparator() {
    return lineSeparator;
  }

  /**
   * Sets line separator.
   *
   * @param lineSeparator the line separator
   */
  public void setLineSeparator(String lineSeparator) {
    String oldValue = this.lineSeparator;
    this.lineSeparator = lineSeparator;
    changeSupport.firePropertyChange("lineSeparator", oldValue, this.lineSeparator);
  }

  /**
   * Gets locale.
   *
   * @return the locale
   */
  public String getLocale() {
    return locale;
  }

  /**
   * Sets locale.
   *
   * @param locale the locale
   */
  public void setLocale(String locale) {
    String oldValue = this.locale;
    this.locale = locale;
    changeSupport.firePropertyChange("locale", oldValue, this.locale);
  }

  /**
   * Gets dataset.
   *
   * @return the dataset
   */
  public String getDataset() {
    return dataset;
  }

  /**
   * Sets dataset.
   *
   * @param dataset the dataset
   */
  public void setDataset(String dataset) {
    String oldValue = this.dataset;
    this.dataset = dataset;
    changeSupport.firePropertyChange("dataset", oldValue, this.dataset);
  }

  /**
   * Is database project boolean.
   *
   * @return the boolean
   */
  public boolean isDatabaseProject() {
    return archetype.getId().endsWith("db");
  }

  /**
   * Is shop project boolean.
   *
   * @return the boolean
   */
  public boolean isShopProject() {
    return archetype.getId().endsWith("shopdb");
  }

  /**
   * Gets jdbc driver type.
   *
   * @return the jdbc driver type
   */
  public JDBCDriverInfo getJdbcDriverType() {
    return jdbcDriverType;
  }

  /**
   * Sets jdbc driver type.
   *
   * @param driver the driver
   */
  public void setJdbcDriverType(JDBCDriverInfo driver) {
    JDBCDriverInfo oldValue = this.jdbcDriverType;
    this.jdbcDriverType = driver;
    changeSupport.firePropertyChange("jdbcDriverType", oldValue, this.jdbcDriverType);
    if (!NullSafeComparator.equals(oldValue, this.jdbcDriverType)) {
      String urlPattern = driver.getUrlPattern();
      String db = driver.getDefaultDatabase();
      if (StringUtil.isEmpty(db)) {
        db = "<database>";
      }
      setDbUrl(MessageFormat.format(urlPattern,
          "<host>", driver.getDefaultPort(), db));
      setDbDriver(driver.getDriverClass());
      setDbUser(driver.getDefaultUser());
      setDbSchema(driver.getDefaultSchema());
    }
  }

  /**
   * Gets db url.
   *
   * @return the db url
   */
  public String getDbUrl() {
    return dbUrl;
  }

  /**
   * Sets db url.
   *
   * @param dbUrl the db url
   */
  public void setDbUrl(String dbUrl) {
    String oldValue = this.dbUrl;
    this.dbUrl = dbUrl;
    changeSupport.firePropertyChange("dbUrl", oldValue, this.dbUrl);
  }

  /**
   * Gets db driver.
   *
   * @return the db driver
   */
  public String getDbDriver() {
    return dbDriver;
  }

  /**
   * Sets db driver.
   *
   * @param dbDriver the db driver
   */
  public void setDbDriver(String dbDriver) {
    String oldValue = this.dbDriver;
    this.dbDriver = dbDriver;
    changeSupport.firePropertyChange("dbDriver", oldValue, this.dbDriver);
  }

  /**
   * Gets db password.
   *
   * @return the db password
   */
  public String getDbPassword() {
    return dbPassword;
  }

  /**
   * Sets db password.
   *
   * @param dbPassword the db password
   */
  public void setDbPassword(String dbPassword) {
    String oldValue = this.dbPassword;
    this.dbPassword = dbPassword;
    changeSupport.firePropertyChange("dbPassword", oldValue, this.dbPassword);
  }

  /**
   * Gets db schema.
   *
   * @return the db schema
   */
  public String getDbSchema() {
    return dbSchema;
  }

  /**
   * Sets db schema.
   *
   * @param dbSchema the db schema
   */
  public void setDbSchema(String dbSchema) {
    String oldValue = this.dbSchema;
    this.dbSchema = dbSchema;
    changeSupport.firePropertyChange("dbSchema", oldValue, this.dbSchema);
  }

  /**
   * Gets db catalog.
   * 
   * @return
   */
  public String getDbCatalog() {
	return dbCatalog;
  }

  /**
   * Sets db catalog.
   * 
   * @param dbCatalog the db catalog
   */
  public void setDbCatalog(String dbCatalog) {
	String oldValue = this.dbCatalog;
	this.dbCatalog = dbCatalog;
	changeSupport.firePropertyChange("dbCatalog", oldValue, this.dbCatalog);
  }

  /**
   * Gets db user.
   *
   * @return the db user
   */
  public String getDbUser() {
    return dbUser;
  }

  /**
   * Sets db user.
   *
   * @param dbUser the db user
   */
  public void setDbUser(String dbUser) {
    String oldValue = this.dbUser;
    this.dbUser = dbUser;
    changeSupport.firePropertyChange("dbUser", oldValue, this.dbUser);
    // if schema or password had no value or the same value before, update it
    if (this.dbSchema == null || NullSafeComparator.equals(oldValue, this.dbSchema)) {
      setDbSchema(dbUser);
    }
    if (this.dbPassword == null || NullSafeComparator.equals(oldValue, this.dbPassword)) {
      setDbPassword(dbUser);
    }
  }

  /**
   * Gets drop script file.
   *
   * @return the drop script file
   */
  public File getDropScriptFile() {
    return dropScriptFile;
  }

  /**
   * Sets drop script file.
   *
   * @param dropScriptFile the drop script file
   */
  public void setDropScriptFile(File dropScriptFile) {
    this.dropScriptFile = dropScriptFile;
  }

  /**
   * Gets create script file.
   *
   * @return the create script file
   */
  public File getCreateScriptFile() {
    return createScriptFile;
  }

  /**
   * Sets create script file.
   *
   * @param createScriptFile the create script file
   */
  public void setCreateScriptFile(File createScriptFile) {
    this.createScriptFile = createScriptFile;
  }

  /**
   * Get db dependencies maven dependency [ ].
   *
   * @return the maven dependency [ ]
   */
  public MavenDependency[] getDbDependencies() {
    return dbDependencies;
  }

  /**
   * Sets db dependencies.
   *
   * @param dbDependencies the db dependencies
   */
  public void setDbDependencies(MavenDependency[] dbDependencies) {
    this.dbDependencies = dbDependencies;
  }

  /**
   * Gets db snapshot.
   *
   * @return the db snapshot
   */
  public String getDbSnapshot() {
    return dbSnapshot;
  }

  /**
   * Sets db snapshot.
   *
   * @param dbSnapshot the db snapshot
   */
  public void setDbSnapshot(String dbSnapshot) {
    this.dbSnapshot = dbSnapshot;
  }

  /**
   * Gets db snapshot file.
   *
   * @return the db snapshot file
   */
  public String getDbSnapshotFile() {
    if (DBSnapshotTool.DBUNIT_FORMAT.equals(dbSnapshot)) {
      return "base.dbunit.xml";
    } else if (DBSnapshotTool.SQL_FORMAT.equals(dbSnapshot)) {
      return "base.sql";
    } else if (DBSnapshotTool.XLS_FORMAT.equals(dbSnapshot)) {
      return "base.xls";
    } else {
      return null;
    }
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  @Override
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * Project file file.
   *
   * @param filename the filename
   * @return the file
   */
  public File projectFile(String filename) {
    File file = new File(getProjectFolder(), filename);
    if (!overwrite && file.exists()) {
      throw new I18NError("FileAlreadyExists", null, filename);
    }
    return file;
  }

  /**
   * Sub directory file.
   *
   * @param relativePath the relative path
   * @return the file
   */
  public File subDirectory(String relativePath) {
    return new File(projectFolder, FileUtil.nativePath(relativePath));
  }

}
