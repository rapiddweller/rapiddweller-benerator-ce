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

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;

/**
 * Generates {@link Long} values from a database sequence.<br/>
 * <br/>
 * Created at 07.07.2009 18:54:53
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DBSequenceGenerator extends NonNullGeneratorProxy<Long> {

  private String name;
  private DBSystem database;
  private boolean cached;

  /**
   * Instantiates a new Db sequence generator.
   *
   * @param name   the name
   * @param source the source
   */
  public DBSequenceGenerator(String name, DBSystem source) {
    this(name, source, false);
  }

  /**
   * Instantiates a new Db sequence generator.
   *
   * @param name     the name
   * @param database the database
   * @param cached   the cached
   */
  public DBSequenceGenerator(String name, DBSystem database, boolean cached) {
    super(Long.class);
    this.name = name;
    this.database = database;
    this.cached = cached;
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets database.
   *
   * @return the database
   */
  public DBSystem getDatabase() {
    return database;
  }

  /**
   * Sets database.
   *
   * @param database the database
   */
  public void setDatabase(DBSystem database) {
    this.database = database;
  }

  /**
   * Is cached boolean.
   *
   * @return the boolean
   */
  public boolean isCached() {
    return cached;
  }

  /**
   * Sets cached.
   *
   * @param cached the cached
   */
  public void setCached(boolean cached) {
    this.cached = cached;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public synchronized void init(GeneratorContext context) {
    setSource(cached ?
        new CachedSequenceGenerator(name, database) :
        new PlainSequenceGenerator(name, database));
    super.init(context);
  }

}
