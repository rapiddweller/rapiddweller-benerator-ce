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

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.attr.ErrorHandlerAttribute;
import com.rapiddweller.benerator.engine.parser.attr.PageSizeAttribute;
import com.rapiddweller.benerator.engine.parser.string.BeanSpecParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.GenIterStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.RunTaskStatement;
import com.rapiddweller.benerator.engine.statement.WhileStatement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.FullyQualifiedClassNameParser;
import com.rapiddweller.common.parser.NonNegativeIntegerParser;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.common.Expression;
import com.rapiddweller.task.PageListener;
import com.rapiddweller.task.Task;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses a run-task descriptor.<br/><br/>
 * Created: 25.10.2009 00:55:16
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class RunTaskParser extends AbstractBeneratorDescriptorParser {

  // format spec -----------------------------------------------------------------------------------------------------

  private static final AttrInfo<String> CLASS = new AttrInfo<>(
      ATT_CLASS, false, BeneratorErrorIds.SYN_RUN_TASK_CLASS,
      new FullyQualifiedClassNameParser(false), null);

  private static final AttrInfo<String> SPEC = new AttrInfo<>(
      ATT_SPEC, false, BeneratorErrorIds.SYN_RUN_TASK_SPEC, null, null);

  private static final AttrInfo<Expression<Long>> COUNT = new AttrInfo<>(
      ATT_COUNT, false, BeneratorErrorIds.SYN_RUN_TASK_COUNT,
      new ScriptableParser<>(new NonNegativeLongParser()), null);

  private static final AttrInfo<Expression<Long>> PAGESIZE =
      new PageSizeAttribute(BeneratorErrorIds.SYN_RUN_TASK_PAGE_SIZE);

  private static final AttrInfo<Expression> PAGER = new AttrInfo<>(
      ATT_PAGER, false, BeneratorErrorIds.SYN_RUN_TASK_PAGER, new BeanSpecParser(), null);

  private static final AttrInfo<Expression<Integer>> THREADS = new AttrInfo<>(
      ATT_THREADS, false, BeneratorErrorIds.SYN_RUN_TASK_THREADS,
      new ScriptableParser<>(new NonNegativeIntegerParser()), "1"
  );

  private static final AttrInfo<Expression<Boolean>> STATS = new AttrInfo<>(
      ATT_STATS, false, BeneratorErrorIds.SYN_RUN_TASK_STATS,
      new ScriptableParser<>(new BooleanParser()), "false"
  );

  private static final AttrInfo<Expression<ErrorHandler>> ON_ERROR =
      new ErrorHandlerAttribute(BeneratorErrorIds.SYN_RUN_TASK_ON_ERROR);

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(
      BeneratorErrorIds.SYN_RUN_TASK_ILLEGAL_ATTR, CLASS, SPEC, COUNT, PAGESIZE, PAGER, THREADS, STATS, ON_ERROR);

  // constructor & interface -----------------------------------------------------------------------------------------

  public RunTaskParser() {
    super(EL_RUN_TASK, ATTR_INFO,
        BeneratorRootStatement.class, IfStatement.class, WhileStatement.class, GenIterStatement.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public RunTaskStatement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    attrSupport.validate(element);
    try {
      Expression<Task> taskProvider = (Expression<Task>) BeanParser.parseBeanExpression(element, false);
      Expression<Long> count = COUNT.parse(element);
      Expression<Long> pageSize = PAGESIZE.parse(element);
      Expression<Integer> threads = THREADS.parse(element);
      Expression<PageListener> pager = (Expression<PageListener>) PAGER.parse(element);
      Expression<Boolean> stats = STATS.parse(element);
      Expression<ErrorHandler> errorHandler = ON_ERROR.parse(element);
      boolean infoLog = containsLoop(parentComponentPath);
      return new RunTaskStatement(taskProvider, count, pageSize, pager, threads,
          stats, errorHandler, infoLog);
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().configurationError(
          "Error parsing run-task element", e);
    }
  }

}
