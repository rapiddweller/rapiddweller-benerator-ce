/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.format.xml.ParseContext;
import com.rapiddweller.platform.db.DatabaseParser;

/**
 * {@link ParseContext} implementation for Benerator. It defines parsers for all the descriptor XML elements.<br/><br/>
 * Created: 14.12.2010 16:29:38
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class BeneratorParseContext extends ParseContext<Statement> {

  final ResourceManager resourceManager;

  public BeneratorParseContext(ResourceManager resourceManager) {
    super(Statement.class);
    this.resourceManager = resourceManager;
    factory.addParser(new BeanParser());
    factory.addParser(new BeepParser());
    factory.addParser(new CascadeParser());
    factory.addParser(new CommentParser());
    factory.addParser(new MongoDBParser());
    factory.addParser(new DatabaseParser());
    factory.addParser(new DefaultComponentParser());
    factory.addParser(new DOMTreeParser());
    factory.addParser(new EchoParser());
    factory.addParser(new ErrorParser());
    factory.addParser(new ExecuteParser());
    factory.addParser(new EvaluateParser());
    factory.addParser(new MetaModelParser());
    factory.addParser(new PreParseGenerateParser());
    factory.addParser(new GenerateParser());
    factory.addParser(new IterateParser());
    factory.addParser(new IfParser());
    factory.addParser(new ImportParser());
    factory.addParser(new IncludeParser());
    factory.addParser(new SettingParser());
    factory.addParser(new RunTaskParser());
    factory.addParser(new SetupParser());
    factory.addParser(new TranscodeParser());
    factory.addParser(new TranscodingTaskParser());
    factory.addParser(new WaitParser());
    factory.addParser(new WhileParser());
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public BeneratorParseContext createSubContext(ResourceManager resourceManager) {
    return new BeneratorParseContext(resourceManager);
  }

}
