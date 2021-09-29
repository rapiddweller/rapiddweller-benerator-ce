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

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.platform.memstore.MemStore;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * {@link Statement} that instantiates a {@link MemStore}
 * and registers it in the {@link BeneratorContext}.<br/><br/>
 * Created: 08.03.2011 13:30:45
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class MemStoreStatement implements Statement {

  private static final Logger logger = LoggerFactory.getLogger(DefineDatabaseStatement.class);

  private final String id;
  ResourceManager resourceManager;

  public MemStoreStatement(String id, ResourceManager resourceManager) {
    if (id == null) {
      throw new ConfigurationError("No store id defined");
    }
    this.id = id;
    this.resourceManager = resourceManager;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    logger.debug("Instantiating store with id '{}'", id);
    MemStore store = new MemStore(id, context.getDataModel());
    // register this object on all relevant managers and in the context
    context.setGlobal(id, store);
    context.getDataModel().addDescriptorProvider(store);
    resourceManager.addResource(store);
    return true;
  }

}
