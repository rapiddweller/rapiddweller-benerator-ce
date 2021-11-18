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

import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.expression.context.DefaultPageSizeExpression;
import com.rapiddweller.benerator.engine.statement.GenerateOrIterateStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.RunTaskStatement;
import com.rapiddweller.benerator.engine.statement.WhileStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.Expression;
import com.rapiddweller.task.PageListener;
import com.rapiddweller.task.Task;
import org.w3c.dom.Element;

import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CLASS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_COUNT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ON_ERROR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PAGER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PAGESIZE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SPEC;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_STATS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_THREADS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_RUN_TASK;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseBooleanExpressionAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseIntAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseLongAttribute;

/**
 * Parses a run-task descriptor.<br/><br/>
 * Created: 25.10.2009 00:55:16
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class RunTaskParser extends AbstractBeneratorDescriptorParser {

  private static final Set<String> OPTIONAL_ATTRIBUTES =
      CollectionUtil.toSet(ATT_ID, ATT_CLASS, ATT_SPEC, ATT_COUNT, ATT_PAGESIZE, ATT_THREADS, ATT_PAGER, ATT_STATS, ATT_ON_ERROR);
  private static final DefaultPageSizeExpression DEFAULT_PAGE_SIZE = new DefaultPageSizeExpression();

  public RunTaskParser() {
    super(EL_RUN_TASK, null, OPTIONAL_ATTRIBUTES,
        BeneratorRootStatement.class, IfStatement.class, WhileStatement.class, GenerateOrIterateStatement.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public RunTaskStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
    try {
      Expression<Task> taskProvider = (Expression<Task>) BeanParser.parseBeanExpression(element);
      Expression<Long> count = parseLongAttribute(ATT_COUNT, element, 1);
      Expression<Long> pageSize = parseLongAttribute(ATT_PAGESIZE, element, DEFAULT_PAGE_SIZE);
      Expression<Integer> threads = parseIntAttribute(ATT_THREADS, element, 1);
      Expression<PageListener> pager = parsePager(element);
      Expression<Boolean> stats = parseBooleanExpressionAttribute(ATT_STATS, element, false);
      Expression<ErrorHandler> errorHandler = parseOnErrorAttribute(element, element.getAttribute(ATT_ID));
      boolean infoLog = containsLoop(parentPath);
      return new RunTaskStatement(taskProvider, count, pageSize, pager, threads,
          stats, errorHandler, infoLog);
    } catch (ConversionException e) {
      throw new ConfigurationError("Error parsing run-task element", e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Expression<PageListener> parsePager(Element element) {
    String pagerSpec = element.getAttribute(ATT_PAGER);
    return (Expression<PageListener>) DatabeneScriptParser.parseBeanSpec(pagerSpec);
  }

}
