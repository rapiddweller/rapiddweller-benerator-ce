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

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.composite.GenerationStep;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.CurrentProductGeneration;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.expression.CachedExpression;
import com.rapiddweller.benerator.engine.expression.xml.XMLConsumerExpression;
import com.rapiddweller.benerator.engine.parser.GenerationInterceptor;
import com.rapiddweller.benerator.engine.statement.ConversionStatement;
import com.rapiddweller.benerator.engine.statement.GenerateAndConsumeTask;
import com.rapiddweller.benerator.engine.statement.GenerateOrIterateStatement;
import com.rapiddweller.benerator.engine.statement.LazyStatement;
import com.rapiddweller.benerator.engine.statement.TimedGeneratorStatement;
import com.rapiddweller.benerator.engine.statement.ValidationStatement;
import com.rapiddweller.benerator.factory.DescriptorUtil;
import com.rapiddweller.benerator.factory.GenerationStepFactory;
import com.rapiddweller.benerator.factory.MetaGeneratorFactory;
import com.rapiddweller.benerator.parser.ModelParser;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.model.data.VariableHolder;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.PrimitiveType;
import com.rapiddweller.script.expression.DynamicExpression;
import com.rapiddweller.task.PageListener;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSUMER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONTAINER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONVERTER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_COUNT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_COUNT_DISTRIBUTION;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CYCLIC;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DATASET;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DISTRIBUTION;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ENCODING;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_FILTER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_FORMAT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_GENERATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_LOCALE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_MAX_COUNT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_MIN_COUNT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NAME;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NESTING;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NULL_QUOTA;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_OFFSET;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ON_ERROR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PAGER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PAGESIZE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SEGMENT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SELECTOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SEPARATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SOURCE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_STATS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SUB_SELECTOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TEMPLATE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_THREADS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_UNIQUE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_VALIDATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.COMPONENT_TYPES;
import static com.rapiddweller.benerator.engine.DescriptorConstants.CREATE_ENTITIES_EXT_SETUP;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_CONSUMER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_GENERATE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ITERATE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_VALUE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_VARIABLE;
import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

/**
 * Parses a &lt;generate&gt; or &lt;update&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 01:05:18
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class GenerateOrIterateParser extends AbstractBeneratorDescriptorParser {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(
      ATT_COUNT, ATT_MIN_COUNT, ATT_MAX_COUNT, ATT_COUNT_DISTRIBUTION,
      ATT_THREADS,
      ATT_PAGESIZE, ATT_STATS, ATT_ON_ERROR,
      ATT_TEMPLATE, ATT_CONSUMER,
      ATT_NAME, ATT_TYPE, ATT_CONTAINER, ATT_GENERATOR, ATT_VALIDATOR,
      ATT_CONVERTER, ATT_NULL_QUOTA, ATT_UNIQUE, ATT_DISTRIBUTION, ATT_CYCLIC,
      ATT_SOURCE, ATT_SEGMENT, ATT_FORMAT, ATT_OFFSET, ATT_SEPARATOR, ATT_ENCODING, ATT_SELECTOR, ATT_SUB_SELECTOR,
      ATT_DATASET, ATT_NESTING, ATT_LOCALE, ATT_FILTER
  );


  private static final Set<String> CONSUMER_EXPECTING_ELEMENTS = CollectionUtil.toSet(EL_GENERATE, EL_ITERATE);

  // DescriptorParser interface --------------------------------------------------------------------------------------

  public GenerateOrIterateParser() {
    super("", null, OPTIONAL_ATTRIBUTES);
  }

  private static List<String> createProfilerPath(Statement[] parentPath, Statement currentElement) {
    List<String> path = new ArrayList<>(parentPath != null ? parentPath.length + 1 : 1);
    if (parentPath != null) {
      for (Statement statement : parentPath) {
        path.add(statement.toString());
      }
    }
    path.add(currentElement.toString());
    return path;
  }

  private static String getNameOrType(Element element) {
    String result = element.getAttribute(ATT_NAME);
    if (StringUtil.isEmpty(result)) {
      result = element.getAttribute(ATT_TYPE);
    }
    if (StringUtil.isEmpty(result)) {
      result = "anonymous";
    }
    return result;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private static Expression<Consumer> parseConsumers(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
    return new CachedExpression<>(new XMLConsumerExpression(entityElement, consumersExpected, resourceManager));
  }

  private static InstanceDescriptor mapDescriptorElement(Element element, BeneratorContext context) {
    // TODO v0.7.1 Make Descriptors an abstraction of the XML file content and convert XML -> Descriptors -> Statements

    // evaluate type
    String type = parseStringAttribute(element, ATT_TYPE, context, false);
    TypeDescriptor localType;
    DescriptorProvider localDescriptorProvider = context.getLocalDescriptorProvider();
    if (PrimitiveType.ARRAY.getName().equals(type)
        || XMLUtil.getChildElements(element, false, EL_VALUE).length > 0) {
      localType = new ArrayTypeDescriptor(element.getAttribute(ATT_NAME), localDescriptorProvider);
    } else {
      TypeDescriptor parentType = context.getDataModel().getTypeDescriptor(type);
      if (parentType != null) {
        type = parentType.getName(); // take over capitalization of the parent
        localType = new ComplexTypeDescriptor(parentType.getName(), localDescriptorProvider, (ComplexTypeDescriptor) parentType);
      } else {
        localType = new ComplexTypeDescriptor(type, localDescriptorProvider, "entity");
      }
    }

    // assemble instance descriptor
    InstanceDescriptor instance = new InstanceDescriptor(type, localDescriptorProvider, type);
    instance.setLocalType(localType);

    // map element attributes
    for (Map.Entry<String, String> attribute : XMLUtil.getAttributes(element).entrySet()) {
      String attributeName = attribute.getKey();
      if (!CREATE_ENTITIES_EXT_SETUP.contains(attributeName)) {
        Object attributeValue = attribute.getValue();
        if (instance.supportsDetail(attributeName)) {
          instance.setDetailValue(attributeName, attributeValue);
        } else {
          localType.setDetailValue(attributeName, attributeValue);
        }
      }
    }

    DescriptorUtil.parseComponentConfig(element, instance.getLocalType(), context);
    return instance;
  }

  @Override
  public boolean supports(Element element, Statement[] parentPath) {
    String name = element.getNodeName();
    return EL_GENERATE.equals(name) || EL_ITERATE.equals(name);
  }

  @Override
  public Statement doParse(final Element element, final Statement[] parentPath,
                           final BeneratorParseContext pContext) {
    final boolean looped = AbstractBeneratorDescriptorParser.containsLoop(parentPath);
    final boolean nested = AbstractBeneratorDescriptorParser.containsGeneratorStatement(parentPath);
    Expression<Statement> expression = new DynamicExpression<>() {
      @Override
      public Statement evaluate(Context context) {
        return parseGenerate(
            element, parentPath, pContext, (BeneratorContext) context, !looped, nested);
      }

      @Override
      public String toString() {
        return XMLUtil.formatShort(element);
      }
    };
    Statement statement = new LazyStatement(expression);
    statement = new TimedGeneratorStatement(getNameOrType(element), statement, createProfilerPath(parentPath, statement), !looped);
    return statement;
  }

  @SuppressWarnings("unchecked")
  public GenerateOrIterateStatement parseGenerate(Element element, Statement[] parentPath,
                                                  BeneratorParseContext parsingContext, BeneratorContext context, boolean infoLog, boolean nested) {
    // parse descriptor
    InstanceDescriptor descriptor = mapDescriptorElement(element, context);

    // parse statement
    Generator<Long> countGenerator = DescriptorUtil.createDynamicCountGenerator(descriptor, 0L, 1L, false, context);
    Expression<Long> pageSize = parsePageSize(element);
    Expression<PageListener> pager = (Expression<PageListener>) DatabeneScriptParser.parseBeanSpec(
        element.getAttribute(ATT_PAGER));
    Expression<ErrorHandler> errorHandler = parseOnErrorAttribute(element, element.getAttribute(ATT_NAME));
    Expression<Long> minCount = DescriptorUtil.getMinCount(descriptor, 0L);
    GenerateOrIterateStatement statement = createStatement(getTaskName(descriptor), countGenerator, minCount, pageSize, pager, infoLog,
        nested, element, errorHandler, context);

    // parse task and sub statements
    GenerateAndConsumeTask task = parseTask(element, parentPath, statement, parsingContext, descriptor, infoLog);
    statement.setTask(task);
    return statement;
  }

  protected GenerateOrIterateStatement createStatement(String productName, Generator<Long> countGenerator,
                                                       Expression<Long> minCount, Expression<Long> pageSize,
                                                       Expression<PageListener> pager, boolean infoLog,
                                                       boolean nested, Element element,
                                                       Expression<ErrorHandler> errorHandler, BeneratorContext context) {
    return new GenerateOrIterateStatement(productName, countGenerator, minCount, pageSize, pager,
        errorHandler, infoLog, nested, context);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private GenerateAndConsumeTask parseTask(Element element, Statement[] parentPath, GenerateOrIterateStatement statement,
                                           BeneratorParseContext parseContext, InstanceDescriptor descriptor, boolean infoLog) {
    // log
    if (infoLog) {
      logger.debug("{}", descriptor);
    }

    // check preconditions
    boolean iterationMode = (EL_ITERATE.equals(element.getNodeName()));
    if (iterationMode) {
      // make sure the <iterate> has a 'source'
      if (StringUtil.isEmpty(element.getAttribute(ATT_SOURCE))) {
        syntaxError("'source' missing in <iterate>", element);
      }
    }

    checkThreads(element, parentPath);

    // get core date
    descriptor.setNullable(false);
    String taskName = getTaskName(descriptor);
    BeneratorContext context = statement.getContext();
    BeneratorContext childContext = statement.getChildContext();
    String productName = getNameOrType(element);

    // create base generator
    GenerationInterceptor interceptor = BeneratorFactory.getInstance().getGenerationInterceptor();
    List<Statement> statements = new ArrayList<>();
    interceptor.entityGenerationStarting(taskName, iterationMode, statements);
    Generator<?> base = MetaGeneratorFactory.createBaseGenerator(descriptor, Uniqueness.NONE, context);
    statements.add(new CurrentProductGeneration(productName, base));
    interceptor.componentGenerationStarting(base, iterationMode, statements);

    // handle sub elements
    boolean completionReported = false; // checks if the interceptor.generationComplete() has been called
    ModelParser parser = new ModelParser(childContext);
    TypeDescriptor type = descriptor.getTypeDescriptor();
    int arrayIndex = 0;
    Element[] childElements = XMLUtil.getChildElements(element);
    Set<String> handledMembers = new HashSet<>();
    for (Element child : childElements) {

      // first parse the component descriptor...
      String childName = XMLUtil.localName(child);
      InstanceDescriptor componentDescriptor = null;
      if (EL_VARIABLE.equals(childName)) {
        componentDescriptor = parser.parseVariable(child, (VariableHolder) type);
      } else if (COMPONENT_TYPES.contains(childName)) {
        componentDescriptor = parser.parseComponentGeneration(child, (ComplexTypeDescriptor) type);
        handledMembers.add(componentDescriptor.getName().toLowerCase());
      } else if (EL_VALUE.equals(childName)) {
        componentDescriptor = parser.parseSimpleTypeArrayElement(child, (ArrayTypeDescriptor) type, arrayIndex++);
      }

      // ...handle non-member/variable child elements
      if (componentDescriptor != null) {
        GenerationStep<?> componentGenerator = GenerationStepFactory.createGenerationStep(
            componentDescriptor, Uniqueness.NONE, iterationMode, childContext);
        if (componentGenerator != null) {
          statements.add(componentGenerator);
        }
      } else if (!EL_CONSUMER.equals(childName)) {
        // parse and set up consumer definition
        interceptor.generationComplete(base, iterationMode, statements);
        completionReported = true;
        Statement[] subPath = parseContext.createSubPath(parentPath, statement);
        Statement subStatement = parseContext.parseChildElement(child, subPath);
        statements.add(subStatement);
      }
    }
    if (!completionReported) {
      // if there is no consumer, completion has not yet been reported
      interceptor.generationComplete(base, iterationMode, statements);
      completionReported = true;
    }

    if (!iterationMode) {
      // on <generate>, add missing members defined in parent descriptors
      if (!StringUtil.isEmpty(element.getAttribute(ATT_SOURCE))) {
        syntaxError("'source' not allowed in <generate>", element);
      }
      TypeDescriptor pType = type.getParent();
      if (pType instanceof ComplexTypeDescriptor) {
        // calculate insertion index
        int insertionIndex = statements.size() - 1;
        for (; insertionIndex >= 0; insertionIndex--) {
          Statement tmp = statements.get(insertionIndex);
          if (tmp instanceof GenerationStep || tmp instanceof CurrentProductGeneration) {
            break;
          }
        }
        insertionIndex++;
        // insert generators from parent
        ComplexTypeDescriptor parentType = (ComplexTypeDescriptor) pType;
        for (ComponentDescriptor component : parentType.getComponents()) {
          String componentName = component.getName();
          if (handledMembers.contains(componentName.toLowerCase())) {
            continue;
          }
          GenerationStep<?> componentGenerator = GenerationStepFactory.createGenerationStep(
              component, Uniqueness.NONE, iterationMode, childContext);
          statements.add(insertionIndex++, componentGenerator);
        }
      }
    }

    // create task
    GenerateAndConsumeTask task = createTask(taskName, productName);
    task.setStatements(statements);

    // parse converter
    Converter converter = DescriptorUtil.getConverter(element.getAttribute(ATT_CONVERTER), context);
    if (converter != null) {
      task.addStatement(new ConversionStatement(BeneratorFactory.getInstance().configureConverter(converter, context)));
    }

    // parse validator
    Validator validator = DescriptorUtil.getValidator(element.getAttribute(ATT_VALIDATOR), context);
    if (validator != null) {
      task.addStatement(new ValidationStatement(BeneratorFactory.getInstance().configureValidator(validator, context)));
    }

    // parse consumers
    boolean consumerExpected = CONSUMER_EXPECTING_ELEMENTS.contains(element.getNodeName());
    Expression consumer = parseConsumers(element, consumerExpected, task.getResourceManager());
    task.setConsumer(consumer);

    return task;
  }

  protected void checkThreads(Element element, Statement[] parentPath) {
    String threads = DescriptorParserUtil.getAttribute(ATT_THREADS, element);
    if (threads != null) {
      boolean warn = false;
      try {
        int n = Integer.parseInt(threads);
        warn = (n != 1);
      } catch (NumberFormatException e) {
        warn = true;
      }
      if (warn) {
        logger.warn("Benerator CE does not support multithreaded generation or iteration: " +
            "Ignoring threads='" + threads + "'.");
      }
    }
  }

  protected String getTaskName(InstanceDescriptor descriptor) {
    String taskName = descriptor.getName();
    if (taskName == null) {
      taskName = descriptor.getLocalType().getSource();
    }
    return taskName;
  }

  protected GenerateAndConsumeTask createTask(String taskName, String productName) {
    return new GenerateAndConsumeTask(taskName, productName);
  }

}
