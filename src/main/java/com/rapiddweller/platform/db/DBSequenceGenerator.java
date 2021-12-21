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
 * Generates {@link Long} values from a database sequence.<br/><br/>
 * Created at 07.07.2009 18:54:53
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DBSequenceGenerator extends NonNullGeneratorProxy<Long> {

  private String name;
  private AbstractDBSystem database;
  private boolean cached;

  public DBSequenceGenerator(String name, AbstractDBSystem source) {
    this(name, source, false);
  }

  public DBSequenceGenerator(String name, AbstractDBSystem database, boolean cached) {
    super(Long.class);
    this.name = name;
    this.database = database;
    this.cached = cached;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AbstractDBSystem getDatabase() {
    return database;
  }

  public void setDatabase(AbstractDBSystem database) {
    this.database = database;
  }

  public boolean isCached() {
    return cached;
  }

  public void setCached(boolean cached) {
    this.cached = cached;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    setSource(cached ?
        new CachedSequenceGenerator(name, database) :
        new PlainSequenceGenerator(name, database));
    super.init(context);
  }

}
