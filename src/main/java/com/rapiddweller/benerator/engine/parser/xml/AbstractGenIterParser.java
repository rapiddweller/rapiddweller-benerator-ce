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
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.ConversionStatement;
import com.rapiddweller.benerator.engine.statement.GenIterStatement;
import com.rapiddweller.benerator.engine.statement.GenIterTask;
import com.rapiddweller.benerator.engine.statement.LazyStatement;
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
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.PositiveIntegerParser;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttributeInfo;
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
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

/**
 * Parses a &lt;generate&gt; or &lt;update&gt; element in a Benerator descriptor XML file.<br/><br/>
 * Created: 25.10.2009 01:05:18
 * @author Volker Bergmann
 * @since 0.6.0
 */
public abstract class AbstractGenIterParser extends AbstractBeneratorDescriptorParser {

  protected static final AttributeInfo<Expression<Integer>> THREADS = new AttributeInfo<>(
      ATT_THREADS, false, BeneratorErrorIds.SYN_GENERATE_THREADS,
      "1", new ScriptableParser<>(new PositiveIntegerParser()));

  protected static final AttributeInfo<Expression<Boolean>> STATS = new AttributeInfo<>(
      ATT_STATS, false, BeneratorErrorIds.SYN_GENERATE_STATS, "false", new ScriptableParser<>(new BooleanParser()));

  protected static final AttributeInfo<String> SENSOR = new AttributeInfo<>(
      ATT_SENSOR, false, BeneratorErrorIds.SYN_GENERATE_SENSOR, null, null);


  private static final Set<String> CONSUMER_EXPECTING_ELEMENTS = CollectionUtil.toSet(EL_GENERATE, EL_ITERATE);

  protected AbstractGenIterParser(String elementName, AttrInfoSupport attrSupport) {
    super(elementName, attrSupport);
  }

  // DescriptorParser interface --------------------------------------------------------------------------------------

  @Override
  public Statement doParse(final Element element, Element[] parentXmlPath, final Statement[] parentPath,
                           final BeneratorParseContext pContext) {
    attrSupport.validate(element);
    final boolean looped = AbstractBeneratorDescriptorParser.containsLoop(parentPath);
    final boolean nested = AbstractBeneratorDescriptorParser.containsGeneratorStatement(parentPath);
    Expression<Statement> expression = new DynamicExpression<>() {
      @Override
      public Statement evaluate(Context context) {
        return parseGenerate(
            element, parentXmlPath, parentPath, pContext, (BeneratorContext) context, !looped, nested);
      }

      @Override
      public String toString() {
        return XMLUtil.formatShort(element);
      }
    };
    return new LazyStatement(expression);
  }

  @SuppressWarnings("unchecked")
  public GenIterStatement parseGenerate(Element element, Element[] parentXmlPath, Statement[] parentPath,
                                        BeneratorParseContext parsingContext, BeneratorContext context, boolean infoLog, boolean nested) {
    // parse descriptor
    InstanceDescriptor descriptor = mapDescriptorElement(element, context);

    // parse statement
    boolean iterate = ("iterate".equals(element.getNodeName()));
    Generator<Long> countGenerator = DescriptorUtil.createDynamicCountGenerator(descriptor, 0L, 1L, false, context);
    Expression<Long> pageSize = parsePageSize(element);
    Expression<Integer> threads = THREADS.parse(element);
    Expression<PageListener> pager = (Expression<PageListener>) DatabeneScriptParser.parseBeanSpec(
        element.getAttribute(ATT_PAGER));
    String productName = getTaskName(descriptor);
    Expression<Boolean> stats = STATS.parse(element);
    String sensor = SENSOR.parse(element);

    Expression<ErrorHandler> errorHandler = parseOnErrorAttribute(element, element.getAttribute(ATT_NAME));
    Expression<Long> minCount = DescriptorUtil.getMinCount(descriptor, 0L);
    BeneratorContext childContext = context.createSubContext(productName);
    GenIterStatement statement = createStatement(parentPath, iterate, productName,
        countGenerator, minCount, threads, pageSize, pager, stats, sensor, infoLog, nested, errorHandler, context, childContext);

    // TODO avoid double parsing of the InstanceDescriptor and remove the following...
    TypeDescriptor type = descriptor.getTypeDescriptor();
    if (type instanceof ComplexTypeDescriptor) {
      ((ComplexTypeDescriptor) type).clear();
    }
    // TODO ...until this line

    // parse task and sub statements
    Statement[] statementPath = parsingContext.createSubPath(parentPath, statement);

    GenIterTask task = parseTask(element, parentXmlPath, statementPath, parsingContext, descriptor, infoLog, context, childContext);
    statement.setTask(task);
    return statement;
  }

  // private helpers -------------------------------------------------------------------------------------------------

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

  private static Expression<Consumer> parseConsumers(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
    return new CachedExpression<>(new XMLConsumerExpression(entityElement, consumersExpected, resourceManager));
  }

  private static InstanceDescriptor mapDescriptorElement(Element element, BeneratorContext context) {
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

  protected GenIterStatement createStatement(
      Statement[] parentPath, boolean iterate, String productName, Generator<Long> countGenerator, Expression<Long> minCount, Expression<Integer> threads,
      Expression<Long> pageSize, Expression<PageListener> pager, Expression<Boolean> stats, String sensor,
      boolean infoLog, boolean nested,
      Expression<ErrorHandler> errorHandler, BeneratorContext context, BeneratorContext childContext) {
    return new GenIterStatement(parentPath, iterate, productName, countGenerator, minCount, threads, pageSize, pager, stats, sensor,
        errorHandler, infoLog, nested, context, childContext);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private GenIterTask parseTask(
      Element element, Element[] parentXmlPath, Statement[] statementPath, BeneratorParseContext parseContext,
      InstanceDescriptor descriptor, boolean infoLog, BeneratorContext context, BeneratorContext childContext) {
    // log
    if (infoLog) {
      logger.debug("{}", descriptor);
    }

    // check preconditions
    boolean iterationMode = (EL_ITERATE.equals(element.getNodeName()));
    if (iterationMode && StringUtil.isEmpty(element.getAttribute(ATT_SOURCE))) { // make sure the <iterate> has a 'source'
      throw ExceptionFactory.getInstance().syntaxErrorForXmlElement("'source' missing in <iterate>", element);
    }

    // get core date
    descriptor.setNullable(false);
    String taskName = getTaskName(descriptor);
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
      InstanceDescriptor instanceDescriptor = null;
      if (EL_VARIABLE.equals(childName)) {
        instanceDescriptor = parser.parseVariable(child, (VariableHolder) type);
      } else if (COMPONENT_TYPES.contains(childName)) {
        instanceDescriptor = parser.parseComponentGeneration(child, (ComplexTypeDescriptor) type);
        handledMembers.add(instanceDescriptor.getName().toLowerCase());
      } else if (EL_VALUE.equals(childName)) {
        instanceDescriptor = parser.parseSimpleTypeArrayElement(child, (ArrayTypeDescriptor) type, arrayIndex++);
      }

      // ...handle non-member/variable child elements
      if (instanceDescriptor != null) {
        GenerationStep<?> componentGenerator = GenerationStepFactory.createGenerationStep(
            instanceDescriptor, Uniqueness.NONE, iterationMode, childContext);
        if (componentGenerator != null) {
          statements.add(componentGenerator);
        }
      } else if (!EL_CONSUMER.equals(childName)) {
        // parse and set up consumer definition
        interceptor.generationComplete(base, iterationMode, statements);
        completionReported = true;
        Statement subStatement = parseContext.parseChildElement(child, parentXmlPath, statementPath);
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
        throw ExceptionFactory.getInstance().syntaxErrorForXmlElement("'source' not allowed in <generate>", element);
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
    GenIterTask task = createTask(taskName, productName);
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

  protected String getTaskName(InstanceDescriptor descriptor) {
    String taskName = descriptor.getName();
    if (taskName == null) {
      taskName = descriptor.getLocalType().getSource();
    }
    return taskName;
  }

  protected GenIterTask createTask(String taskName, String productName) {
    return new GenIterTask(taskName, productName);
  }

}
