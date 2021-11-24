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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.composite.ComponentBuilder;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.validator.StringValidator;
import com.rapiddweller.model.data.AlternativeGroupDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.expression.ConstantExpression;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link ComponentBuilderFactory} class for all useful attribute setups.<br/><br/>
 * Created: 10.08.2007 12:40:41
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory_attributeTest extends AbstractComponentBuilderFactoryTest {

  // TODO define tests for all syntax paths

  private static final String NAMES_CSV = "com/rapiddweller/benerator/factory/names.csv";
  private static final String EMPTY_WGT_CSV = "com/rapiddweller/benerator/factory/empty.csv";

/*
    private static Log logger = LogFactory.getLog(ComponentBuilderFactory.class);
    
    private static Set<String> componentFeatures = CollectionUtil.toSet(
            "type", "unique", "nullable", "minCount", "maxCount", "count", "nullQuota");
    
*/

  // script ----------------------------------------------------------------------------------------------------------

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testScriptAttribute() {
    PartDescriptor name = createPart("name");
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setScript("'OK'");
    ComponentBuilder<?> builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      assertEquals("OK", GeneratorUtil.generateNonNull(helper));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testNullScriptAttribute() {
    PartDescriptor name = createPart("name");
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setScript("null");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      assertNull(GeneratorUtil.generateNullable(helper));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testScriptWithConverterAttribute() {
    PartDescriptor name = createPart("name");
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setScript("'abc'");
    type.setConverter("com.rapiddweller.common.converter.ToUpperCaseConverter");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      assertEquals("ABC", GeneratorUtil.generateNonNull(helper));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testEnumNameScriptAttribute() {
    PartDescriptor part = createPart("name");
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) part.getLocalType(false);
    type.setScript("myEnum.name()");
    BeneratorContext context = new DefaultBeneratorContext();
    context.set("myEnum", TestEnum.firstInstance);
    ComponentBuilder builder = createComponentBuilder(part, context);
    Generator<String> helper = new ComponentBuilderGenerator(builder, part.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      assertEquals("firstInstance", GeneratorUtil.generateNonNull(helper));
    }
  }

  // constant --------------------------------------------------------------------------------------------------------

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testEmptyConstantAttribute() {
    PartDescriptor name = createPart("name");
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setConstant("");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      String actual = GeneratorUtil.generateNonNull(helper);
      assertEquals("Invalid product: ", "", actual);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testConstantOnNullableAttribute() {
    PartDescriptor name = createPart("name");
    name.setNullable(true);
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setParentName("string");
    type.setConstant("Alice");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      String actual = GeneratorUtil.generateNonNull(helper);
      assertEquals("Invalid product: ", "Alice", actual);
    }
  }

  // values ----------------------------------------------------------------------------------------------------------

  @Test
  public void testMultiValuesAttribute() {
    String componentName = "name";
    PartDescriptor name = createPartDescriptor(componentName);
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setValues("'A','B',null");
    expectSet(name, 300, "A", "B", null);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testSingleValuesAttribute() {
    String componentName = "name";
    PartDescriptor name = createPartDescriptor(componentName);
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setValues("'A'");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      assertEquals("A", GeneratorUtil.generateNonNull(helper));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testEmptyValuesAttribute() {
    String componentName = "name";
    PartDescriptor name = createPartDescriptor(componentName);
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setValues("");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      assertEquals("", GeneratorUtil.generateNonNull(helper));
    }
  }

  // pattern ---------------------------------------------------------------------------------------------------------

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testPatternAttribute() {
    String componentName = "name";
    PartDescriptor name = createPartDescriptor(componentName);
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setPattern("\\d{2,4}");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    Validator<Character> charValidator = c -> ('0' <= c && c <= '9');
    expectGenerations(helper, 20, new StringValidator(charValidator, 2, 4));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testEmptyPatternAttribute() {
    String componentName = "name";
    PartDescriptor name = createPartDescriptor(componentName);
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
    type.setPattern("");
    ComponentBuilder builder = createComponentBuilder(name);
    Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
    helper.init(context);
    for (int i = 0; i < 10; i++) {
      assertEquals("", GeneratorUtil.generateNonNull(helper));
    }
  }

  // csv string source -----------------------------------------------------------------------------------------------

  @Test
  public void testCSVStringAttribute() {
    PartDescriptor name = createCSVStringAttributeDescriptor();
    expectUniqueSequence(name, "Alice", "Bob", "Charly");
  }

  @Test
  public void testCSVStringAttributeStep() {
    PartDescriptor name = createCSVStringAttributeDescriptor();
    SimpleTypeDescriptor localType = (SimpleTypeDescriptor) name.getLocalType(false);
    localType.setDistribution("step");
    expectUniqueSequence(name, "Alice", "Bob", "Charly");
  }

  @Test
  public void testCSVStringAttributeUnique() {
    PartDescriptor name = createCSVStringAttributeDescriptor();
    name.setUnique(true);
    expectUniqueSet(name, "Alice", "Bob", "Charly");
  }

  @Test
  public void testCSVStringAttributeRandomUnique() {
    PartDescriptor name = createCSVStringAttributeDescriptor();
    name.getLocalType().setDistribution("random");
    name.setUnique(true);
    expectUniqueSet(name, "Alice", "Bob", "Charly");
  }

  @Test
  public void testCSVStringAttributeEmptyWeighted() {
    PartDescriptor name = createCSVStringAttributeDescriptor(EMPTY_WGT_CSV, ",");
    ComponentBuilder<Entity> builder = createComponentBuilder(name);
    builder.init(context);
    setCurrentProduct(createEntity("E"), "e");
    assertFalse(builder.execute(context));
  }

  private PartDescriptor createCSVStringAttributeDescriptor() {
    return createCSVStringAttributeDescriptor(NAMES_CSV, ",");
  }

  private PartDescriptor createCSVStringAttributeDescriptor(String uri, String separator) {
    String componentName = "name";
    PartDescriptor name = createPartDescriptor(componentName);
    name.setMinCount(new ConstantExpression<>(1L));
    name.setMaxCount(new ConstantExpression<>(1L));
    name.getLocalType(false).setSource(uri);
    name.getLocalType(false).setSeparator(separator);
    return name;
  }

  // nullQuota == 1 evaluation ---------------------------------------------------------------------------------------

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testNullQuotaOneReference() {
    String componentName = "id";
    ReferenceDescriptor reference = (ReferenceDescriptor) createReference(componentName, null).withNullQuota(1);
    ComponentBuilder builder = createComponentBuilder(reference);
    ComponentBuilderGenerator<String> helper = new ComponentBuilderGenerator(builder, componentName);
    helper.init(context);
    expectNullGenerations(helper, 10);
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testNullQuotaOneAttribute() {
    String componentName = "part";
    PartDescriptor attribute = (PartDescriptor) createPartDescriptor(componentName).withNullQuota(1);
    ComponentBuilder builder = createComponentBuilder(attribute);
    ComponentBuilderGenerator<String> helper = new ComponentBuilderGenerator(builder, componentName);
    helper.init(context);
    expectNullGenerations(helper, 10);
  }

  // Id Descriptor tests ---------------------------------------------------------------------------------------------

  @Test
  @SuppressWarnings({"cast", "rawtypes"})
  public void testAlternative() {
    AlternativeGroupDescriptor alternativeType = new AlternativeGroupDescriptor(null, testDescriptorProvider);
    SimpleTypeDescriptor typeA = createSimpleType("A", "string").withValues("'1'");
    alternativeType.addComponent(createPart("a", typeA));
    SimpleTypeDescriptor typeB = createSimpleType("B", "string").withValues("'2'");
    alternativeType.addComponent(createPart("b", typeB));
    PartDescriptor part = createPart(null, alternativeType);
    ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, Uniqueness.SIMPLE, false, context);
    builder.init(context);
    Entity entity = createEntity();
    setCurrentProduct(entity, "e");
    builder.execute(context);
    assertTrue("1".equals(entity.get("a")) || "2".equals(entity.get("b")));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testMap() {
    String componentName = "flag";
    PartDescriptor part = createPartDescriptor(componentName);
    part.setMinCount(new ConstantExpression<>(1L));
    part.setMaxCount(new ConstantExpression<>(1L));
    ((SimpleTypeDescriptor) part.getLocalType(false)).setMap("1->'A',2->'B'");
    part.getLocalType(false).setGenerator("com.rapiddweller.benerator.primitive.IncrementGenerator");
    ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, Uniqueness.NONE, false, context);
    builder.init(context);
    Entity entity = createEntity();
    setCurrentProduct(entity, "e");
    builder.execute(context);
    assertEquals("A", entity.get("flag"));
    builder.execute(context);
    assertEquals("B", entity.get("flag"));
  }

  // test date and time generation -----------------------------------------------------------------------------------

  @Test
  public void testDateMinMax() {
    String componentName = "part";
    SimpleTypeDescriptor type = createSimpleType("lDate", "date").withMin("2000-03-04").withMax("2000-08-09");
    type.setGranularity("0000-00-01");
    PartDescriptor attribute = createPart(componentName, type);
    ComponentBuilder<Entity> builder = createComponentBuilder(attribute);
    ComponentBuilderGenerator<Date> helper = new ComponentBuilderGenerator<>(builder, componentName);
    helper.init(context);
    Validator<Date> validator = new Validator<>() {
      final Date minDate = TimeUtil.date(2000, 2, 4);
      final Date maxDate = TimeUtil.date(2000, 7, 9);

      @Override
      public boolean valid(Date date) {
        return !minDate.after(date) && !maxDate.before(date);
      }
    };
    expectGenerations(helper, 100, validator);
  }

/*
      @Test
      public void testGenerator() {
          createGenerator("test", "generator", BooleanGenerator.class.getName());
          createGenerator("test",
                  "generator", BooleanGenerator.class.getName(),
                  "nullQuota", "0.5");
          createGenerator("test",
                  "generator", BooleanGenerator.class.getName(),
                  "type", "string");
          createGenerator("test",
                  "generator", BooleanGenerator.class.getName(),
                  "type", "string",
                  "nullQuota", "0.5");
      }

      @Test
      public void testSamples() {
          createGenerator("test",
                  "values", "1,2,3");
          createGenerator("test",
                  "values", "1,2,3",
                  "type", "char");
          createGenerator("test",
                  "values", "1,2,3",
                  "nullQuota", "0.5");
          createGenerator("test",
                  "values", "1,2,3",
                  "type", "int");
          createGenerator("test",
                  "values", "1,2,3",
                  "type", "int",
                  "nullQuota", "0.5");
          createGenerator("test",
                  "values", "2000-01-01,2000-01-02,2000-01-03",
                  "type", "date",
                  "pattern", "yyyy-MM-dd",
                  "nullQuota", "0.5");
          // sequence
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", "cumulated");
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", "cumulated",
                  "nullQuota", "0.5");
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", "cumulated",
                  "type", "int");
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", "cumulated",
                  "type", "int",
                  "nullQuota", "0.5");
          // weight function
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", ConstantFunction.class.getName());
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", ConstantFunction.class.getName(),
                  "nullQuota", "0.5");
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", ConstantFunction.class.getName(),
                  "type", "int");
          createGenerator("test",
                  "values", "1,2,3",
                  "distribution", ConstantFunction.class.getName(),
                  "type", "int",
                  "nullQuota", "0.5");
      }

      @Test
      public void testNumbers() {
          checkNumberType("int");
          checkNumberType("byte");
          checkNumberType("short");
          checkNumberType("long");
          checkNumberType("double");
          checkNumberType("float");
          checkNumberType("big_integer");
          checkNumberType("big_decimal");
      }

      @Test
      public void testStrings() {
          createGenerator("test",
                  "type", "string");
          createGenerator("test",
                  "type", "string",
                  "maxLength", "10");
          createGenerator("test",
                  "type", "string",
                  "minLength", "5",
                  "maxLength", "10");
          createGenerator("test",
                  "type", "string",
                  "minLength", "5",
                  "maxLength", "5");
          createGenerator("test",
                  "type", "string",
                  "pattern", "[0-9]{5}",
                  "minLength", "5",
                  "maxLength", "5");
          createGenerator("test",
                  "type", "string",
                  "pattern", "[0-9]{5}",
                  "minLength", "4",
                  "maxLength", "6");
      }

      @Test
      public void testBoolean() {
          createGenerator("test",
                  "type", "boolean");
          createGenerator("test",
                  "type", "boolean",
                  "trueQuota", "0.5");
          createGenerator("test",
                  "type", "boolean",
                  "nullQuota", "0");
          createGenerator("test",
                  "type", "boolean",
                  "trueQuota", "0.5",
                  "nullQuota", "0");
      }

      @Test
      public void testDate() {
          createGenerator("test", "type", "date");
          createGenerator("test",
                  "type", "date",
                  "min", "01/01/2000",
                  "max", "01/03/2000"
          );
          createGenerator("test",
                  "type", "date",
                  "min", "01.01.2000",
                  "max", "03.01.2000",
                  "locale", "de"
          );
          createGenerator("test",
                  "type", "date",
                  "min", "2000-01-01",
                  "max", "2000-12-31",
                  "granularity", "0000-00-01",
                  "distribution", "cumulated",
                  "pattern", "yyyy-MM-dd",
                  "nullQuota", "0.1"
          );
      }

      @Test
      public void testCharacter() {
          createGenerator("test", "type", "char");
          createGenerator("test",
                  "type", "char",
                  "pattern", "\\w",
                  "locale", "de",
                  "nullQuota", "0.5");
      }

      @Test
      public void testImportToType() {
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/dates.txt",
                  "type", "string");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/booleans.txt",
                  "type", "boolean");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/chars.txt",
                  "type", "char");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "byte");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "short");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "int");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "long");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "float");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "double");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "big_integer");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/numbers.txt",
                  "type", "big_decimal");
      }

      @Test
      public void testDateImport() {
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/dates.txt",
                  "pattern", "yyyy-MM-dd",
                  "type", "date");
      }

      @Test
      public void testConvertingImport() {
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/dates.txt",
                  "converter", "com.rapiddweller.common.converter.NoOpConverter");
          createGenerator("test",
                  "source", "com/rapiddweller/benerator/composite/dates.txt",
                  "type", "date",
                  "pattern", "yyyy-MM-dd");
      }

      // private helpers -------------------------------------------------------------------------------------------------

      @Test
      private void checkNumberType(String type) {
          createGenerator("test",
                  "type", type);
          createGenerator("test",
                  "type", type,
                  "min", "1");
          createGenerator("test",
                  "type", type,
                  "max", "2");
          createGenerator("test",
                  "type", type,
                  "distribution", "cumulated");
          createGenerator("test",
                  "type", type,
                  "min", "1",
                  "max", "2");
          createGenerator("test",
                  "type", type,
                  "min", "-2",
                  "max", "1");
          createGenerator("test",
                  "type", type,
                  "min", "1",
                  "max", "2",
                  "distribution", "cumulated");
          createGenerator("test",
                  "type", type,
                  "min", "-2",
                  "max", "1",
                  "distribution", "cumulated");
      }
  */
  // private helpers -------------------------------------------------------------------------------------------------
/*
	@Test
    private ComponentBuilder createGenerator(String name, String ... featureDetails) {
        GenerationSetup setup = new SimpleGenerationSetup();
        logger.debug("Test #" + (++testCount));
        if (featureDetails.length % 2 != 0)
            throw ExceptionFactory.getInstance().configurationError("Illegal setup: need an even number of parameters (name/value pairs)");
        SimpleTypeDescriptor type = new SimpleTypeDescriptor(name, (String) null);
        PartDescriptor part = new PartDescriptor(name, type);
        for (int i = 0; i < featureDetails.length; i += 2)
            if (componentFeatures.contains(featureDetails[i]))
                if ("type".equals(featureDetails[i]))
                    part.setTypeName(featureDetails[i + 1]);
                else
                    part.setDetailValue(featureDetails[i], featureDetails[i + 1]);
            else
                type.setDetailValue(featureDetails[i], featureDetails[i + 1]);
        ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, new DefaultContext(), setup);
        Entity entity = new Entity("Entity");
        for (int i = 0; i < 10; i++) {
            builder.buildComponentFor(entity);
            logger.debug(entity.getComponent(name));
        }
        return builder;
    }
*/
  public Entity createEntity() {
    return new Entity("Entity", testDescriptorProvider);
  }

}
