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
import com.rapiddweller.benerator.distribution.function.ConstantFunction;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.primitive.BooleanGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.validator.StringValidator;
import com.rapiddweller.model.data.AlternativeGroupDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.PrimitiveDescriptorProvider;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.expression.ConstantExpression;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link ComponentBuilderFactory} class for all useful attribute setups.<br/><br/>
 * Created: 10.08.2007 12:40:41
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory_attributeTest extends AbstractComponentBuilderFactoryTest {

  private static final Set<String> componentFeatures = CollectionUtil.toSet(
      "type", "unique", "nullable", "minCount", "maxCount", "count", "nullQuota");

  private static final String NAMES_CSV = "com/rapiddweller/benerator/factory/names.csv";
  private static final String EMPTY_WGT_CSV = "com/rapiddweller/benerator/factory/empty.csv";

  public static final Date DATE_2000_01_01 = TimeUtil.date(2000, 0, 1);
  public static final Date DATE_2000_01_02 = TimeUtil.date(2000, 0, 2);
  public static final Date DATE_2000_01_03 = TimeUtil.date(2000, 0, 3);
  public static final Date DATE_2000_12_31 = TimeUtil.date(2000, 11, 31);

  private static int testCount;


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
    return createCSVStringAttributeDescriptor(NAMES_CSV, ";");
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
    assertNotNull(builder);
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
    assertNotNull(builder);
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

    @Test
    public void test_BooleanGenerator() {
        checkBuilder(
            "generator_test",
            (product) -> product instanceof Boolean,
            (products) -> !products.contains(null),
            "generator", BooleanGenerator.class.getName());
    }

    @Test
    public void test_BooleanGenerator_nullQuota() {
        checkBuilder("generator_test",
            (product) -> (product == null || product instanceof Boolean),
            new NullQuotaValidator(0.5, 0.3),
            "generator", BooleanGenerator.class.getName(),
            "nullQuota", "0.5");
    }

    @Test
    public void test_BooleanGenerator_as_string() {
        checkBuilder("generator_test",
            (product) -> ("true".equals(product) || "false".equals(product)),
            (products) -> !products.contains(null),
            "generator", BooleanGenerator.class.getName(),
            "type", "string");
    }

    @Test
    public void test_BooleanGenerator_as_string_with_nulls() {
        checkBuilder("generator_test",
            (product) -> ("true".equals(product)
                || "false".equals(product)
                || product == null),
            new NullQuotaValidator(0.5, 0.3),
            "generator", BooleanGenerator.class.getName(),
            "type", "string",
            "nullQuota", "0.5");
    }

  @Test
  public void test_samples() {
    checkBuilder("samples_test",
        new AllowedValuesValidator("1", "2", "3"),
        null,
        "values", "1,2,3");
  }

  @Test
  public void test_samples_nullQuota() {
    checkBuilder("samples_test",
        new AllowedValuesValidator(null, "1", "2", "3"),
        new NullQuotaValidator(0.5, 0.3),
        "values", "1,2,3",
        "nullQuota", "0.5");
  }

  @Test
  public void test_int_samples() {
    checkBuilder("samples_test",
        new AllowedValuesValidator(1, 2, 3),
        new NullQuotaValidator(0, 0),
        "values", "1,2,3",
        "type", "int");
  }

  @Test
  public void test_int_samples_nullQuota() {
    checkBuilder("samples_test",
        new AllowedValuesValidator(null, 1, 2, 3),
        new NullQuotaValidator(0.5, 0.3),
        "values", "1,2,3",
        "type", "int",
        "nullQuota", "0.5");
  }

  @Test @Ignore("This fails") // TODO v3.0.0 make this work
  public void test_date_samples_nullQuota() {
    checkBuilder("samples_test",
        new AllowedValuesValidator(null, DATE_2000_01_01, DATE_2000_01_02, DATE_2000_01_03),
        new NullQuotaValidator(0.5, 0.3),
      "values", "2000-01-01,2000-01-02,2000-01-03",
      "type", "date",
      "pattern", "yyyy-MM-dd",
      "nullQuota", "0.5");
  }

  @Test
  public void test_samples_cumulated() {
    // sequence
    checkBuilder("samples_test",
        new AllowedValuesValidator("1", "2", "3"),
        new NullQuotaValidator(0, 0),
        "values", "1,2,3",
        "distribution", "cumulated");
  }

  @Test
  public void test_samples_cumulated_nullQuota() {
    checkBuilder("samples_test",
        new AllowedValuesValidator(null, "1", "2", "3"),
        new NullQuotaValidator(0.5, 0.3),
        "values", "1,2,3",
        "distribution", "cumulated",
        "nullQuota", "0.5");
  }

  @Test
  public void test_samples_constant_distribution() {
    // weight function
    checkBuilder("samples_test",
        new AllowedValuesValidator("1", "2", "3"),
        new NullQuotaValidator(0, 0),
        "values", "1,2,3",
        "distribution", ConstantFunction.class.getName());
  }

  @Test
  public void test_int_samples_constant_distribution() {
    checkBuilder("samples_test",
        new AllowedValuesValidator(1, 2, 3),
        new NullQuotaValidator(0, 0),
        "values", "1,2,3",
        "distribution", ConstantFunction.class.getName(),
        "type", "int");
  }

  @Test
  public void test_int_samples_const_dist_nullQuota() {
    checkBuilder("samples_test",
        new AllowedValuesValidator(null, 1, 2, 3),
        new NullQuotaValidator(0.5, 0.3),
        "values", "1,2,3",
        "distribution", ConstantFunction.class.getName(),
        "type", "int",
        "nullQuota", "0.5");
  }

  @Test
  public void test_int_type() {
    checkNumberType("int", Integer.class);
  }

  @Test
  public void test_byte_type() {
    checkNumberType("byte", Byte.class);
  }

  @Test
  public void test_short_type() {
    checkNumberType("short", Short.class);
  }

  @Test
  public void test_long_type() {
    checkNumberType("long", Long.class);
  }

  @Test
  public void test_double_type() {
    checkNumberType("double", Double.class);
  }

  @Test
  public void test_float_type() {
    checkNumberType("float", Float.class);
  }

  @Test
  public void test_big_integer_type() {
    checkNumberType("big_integer", BigInteger.class);
  }

  @Test
  public void test_big_decimal_type() {
    checkNumberType("big_decimal", BigDecimal.class);
  }

  @Test
  public void test_string() {
    checkBuilder("string_test",
        (product) -> product instanceof String,
        null,
        "type", "string");
  }

  @Test
  public void test_string_maxLength() {
    checkBuilder("string_test",
        (product) -> ((String) product).length() <= 10,
        null,
        "type", "string",
        "maxLength", "10");
  }

  @Test
  public void test_string_minMaxLength() {
    checkBuilder("string_test",
        (product) -> ((String) product).length() >= 5 && ((String) product).length() <= 10,
        null,
        "type", "string",
        "minLength", "5",
        "maxLength", "10");
  }

  @Test
  public void test_string_fixedLength() {
    checkBuilder("string_test",
        (product) -> ((String) product).length() == 5,
        null,
        "type", "string",
        "minLength", "5",
        "maxLength", "5");
  }

  @Test
  public void test_string_pattern() {
    checkBuilder("string_test",
        (product) -> ((String) product).length() == 5,
        null,
        "type", "string",
        "pattern", "[0-9]{5}",
        "minLength", "5",
        "maxLength", "5");
  }

  @Test
  public void test_string_pattern_length() {
    checkBuilder("string_test",
        (product) -> ((String) product).length() == 5,
        null,
        "type", "string",
        "pattern", "[0-9]{5}",
        "minLength", "4",
        "maxLength", "6");
  }

  @Test
  public void test_boolean() {
    checkBuilder("bool_test",
        (product) -> product instanceof Boolean,
        new NullQuotaValidator(0., 0.),
        "type", "boolean");
  }

  @Test
  public void test_boolean_trueQuota() {
    checkBuilder("bool_test",
        (product) -> product instanceof Boolean,
        new TrueQuotaValidator(0.7, 0.299),
        "type", "boolean",
        "trueQuota", "0.7");
  }

  @Test
  public void test_boolean_nullQuota_0() {
    checkBuilder("bool_test",
        (product) -> product instanceof Boolean,
        new NullQuotaValidator(0., 0.),
        "type", "boolean",
        "nullQuota", "0");
  }

  @Test
  public void test_boolean_nullQuota_half() {
    checkBuilder("bool_test",
        (product) -> product instanceof Boolean,
        new TrueQuotaValidator(0.7, 0.299),
        "type", "boolean",
        "trueQuota", "0.7",
        "nullQuota", "0");
  }

  @Test
  public void test_date() {
    checkBuilder("date_test",
        (product) -> product instanceof Date,
        null,
        "type", "date");
  }

  @Test @Ignore("This fails") // TODO v3.0.0 make this work
  public void test_date_min_max() {
    checkBuilder("date_test",
        (product) -> !((Date) product).before(DATE_2000_01_01) && !((Date) product).after(DATE_2000_01_03),
        null,
        "type", "date",
        "min", "01/01/2000",
        "max", "01/03/2000"
    );
  }

  @Test @Ignore("This fails") // TODO v3.0.0 make this work
  public void test_date_min_max_locale() {
    checkBuilder("date_test",
        (product) -> !((Date) product).before(DATE_2000_01_01) && !((Date) product).after(DATE_2000_01_03),
        null,
        "type", "date",
        "min", "01.01.2000",
        "max", "03.01.2000",
        "locale", "de"
    );
  }

  @Test @Ignore("This fails") // TODO v3.0.0 make this work
  public void test_date_distribution() {
    checkBuilder("date_test",
        (product) -> !((Date) product).before(DATE_2000_01_01) && !((Date) product).after(DATE_2000_12_31),
        null,
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
  public void test_source_string() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator("2000-01-01", "2000-01-02", "2000-01-03"),
        (products) -> products.size() == 12,
        "source", "com/rapiddweller/benerator/composite/dates.txt",
        "type", "string");
  }

  @Test
  public void test_source_boolean() {
    checkBuilder("import_test", 10,
        new AllowedValuesValidator(true, false),
        null,
        "source", "com/rapiddweller/benerator/composite/booleans.txt",
        "type", "boolean");
  }

  @Test
  public void test_source_byte() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator((byte) -2, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4,
            (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "byte");
  }

  @Test
  public void test_source_short() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator((short) -2, (short) -1, (short) 0, (short) 1, (short) 2, (short) 3, (short) 4,
            (short) 5, (short) 6, (short) 7, (short) 8, (short) 9),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "short");
  }

  @Test
  public void test_source_int() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator(-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "int");
  }

  @Test
  public void test_source_long() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator(-2L, -1L, 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "long");
  }

  @Test
  public void test_source_float() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator(-2f, -1f, 0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "float");
  }

  @Test
  public void test_source_double() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator(-2., -1., 0., 1., 2., 3., 4., 5., 6., 7., 8., 9.),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "double");
  }

  @Test
  public void test_source_big_integer() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator(new BigInteger("-2"), new BigInteger("-1"), BigInteger.ZERO,
            BigInteger.ONE, BigInteger.TWO, new BigInteger("3"), new BigInteger("4"),
            new BigInteger("5"), new BigInteger("6"), new BigInteger("7"), new BigInteger("8"),
            new BigInteger("9")),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "big_integer");
  }

  @Test
  public void test_source_big_decimal() {
    checkBuilder("import_test", 12,
        new AllowedValuesValidator(new BigDecimal("-2"), new BigDecimal("-1"), new BigDecimal("0"),
            new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4"),
            new BigDecimal("5"), new BigDecimal("6"), new BigDecimal("7"), new BigDecimal("8"),
            new BigDecimal("9")),
        null,
        "source", "com/rapiddweller/benerator/composite/numbers.txt",
        "type", "big_decimal");
  }

  @Test
  public void test_source_date_pattern() {
    checkBuilder("date_import_test", 12,
        new AllowedValuesValidator(DATE_2000_01_01, DATE_2000_01_02, DATE_2000_01_03),
        null,
        "source", "com/rapiddweller/benerator/composite/dates.txt",
        "pattern", "yyyy-MM-dd",
        "type", "date");
  }

  @Test
  public void test_source_date_converter() {
    checkBuilder("converting_import_test", 12,
        new AllowedValuesValidator("2000-01-01", "2000-01-02", "2000-01-03"),
        null,
        "source", "com/rapiddweller/benerator/composite/dates.txt",
        "converter", "com.rapiddweller.common.converter.NoOpConverter");
  }

  // private helpers -------------------------------------------------------------------------------------------------

	private void checkNumberType(String type, Class<? extends Number> expectedClass) {
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass()),
			null,
			"type", type);
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass()) && ((Number) product).doubleValue() >= 1.,
			null,
			"type", type,
			"min", "1");
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass()) && ((Number) product).doubleValue() <= 2.,
			null,
			"type", type,
			"max", "2");
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass()),
			null,
			"type", type,
			"distribution", "cumulated");
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass())
				&& ((Number) product).doubleValue() >= 1.
				&& ((Number) product).doubleValue() <= 2.,
			null,
			"type", type,
			"min", "1",
			"max", "2");
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass())
				&& ((Number) product).doubleValue() >= -2.
				&& ((Number) product).doubleValue() <= 1.,
			null,
			"type", type,
			"min", "-2",
			"max", "1");
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass())
				&& ((Number) product).doubleValue() >= 1.
				&& ((Number) product).doubleValue() <= 2.,
			null,
			"type", type,
			"min", "1",
			"max", "2",
			"distribution", "cumulated");
		checkBuilder("number_test",
			(product) -> expectedClass.isAssignableFrom(product.getClass())
				&& ((Number) product).doubleValue() >= -2.
				&& ((Number) product).doubleValue() <= 1.,
			null,
			"type", type,
			"min", "-2",
			"max", "1",
			"distribution", "cumulated");
	}

  // private helpers -------------------------------------------------------------------------------------------------

  private void checkBuilder(String componentName, Validator<Object> singleValidator,
                            Validator<Collection<Object>> setValidator, String... featureDetails) {
    checkBuilder(componentName, 1000, singleValidator, setValidator, featureDetails);
  }

  private void checkBuilder(String componentName, int n, Validator<Object> singleValidator,
                            Validator<Collection<Object>> setValidator, String... featureDetails) {
    ComponentBuilder<?> builder = createComponentBuilder(componentName, featureDetails);
    ComponentBuilderGenerator<Object> gen = new ComponentBuilderGenerator<>(builder, componentName);
    gen.init(context);
    ProductWrapper<Object> wrapper = new ProductWrapper<>();
    List<Object> products = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      ProductWrapper<Object> productWrapper = gen.generate(wrapper);
      assertNotNull("Generator unavailable in generation #" + i, productWrapper);
      Object product = productWrapper.unwrap();
      String errMsg = "Unexpected product: " + product;
      if (product != null) {
        errMsg += " (of " + product.getClass() + ")";
      }
      assertTrue(errMsg, singleValidator.valid(product));
      products.add(product);
    }
    if (setValidator != null) {
      assertTrue(setValidator.valid(products));
    }
  }

  private ComponentBuilder<?> createComponentBuilder(String name, String... featureDetails) {
    logger.debug("Test #" + (++testCount));
    // check consistency
    if (featureDetails.length % 2 != 0) {
      throw BeneratorExceptionFactory.getInstance().configurationError(
          "Illegal setup: need an even number of parameters (name/value pairs)");
    }
    // check type
    String typeName = null;
    for (int i = 0; i < featureDetails.length; i+= 2) {
      if ("type".equals(featureDetails[i])) {
        typeName = featureDetails[i + 1];
      }
    }
    // create descriptors
    PrimitiveDescriptorProvider pdp = new PrimitiveDescriptorProvider(dataModel);
    SimpleTypeDescriptor parentType = (SimpleTypeDescriptor) pdp.getTypeDescriptor(typeName);
    TypeDescriptor type;
    if (parentType != null) {
      type = new SimpleTypeDescriptor(name + "_type", testDescriptorProvider, parentType);
    } else {
      assertNull("Not a primitive type: " + typeName, typeName);
      type = new SimpleTypeDescriptor(name + "_type", testDescriptorProvider);
    }
    PartDescriptor part = new PartDescriptor(name, testDescriptorProvider, type);
    for (int i = 0; i < featureDetails.length; i += 2) {
      if (componentFeatures.contains(featureDetails[i])) {
        if (!"type".equals(featureDetails[i])) {
          part.setDetailValue(featureDetails[i], featureDetails[i + 1]);
        }
      } else {
        type.setDetailValue(featureDetails[i], featureDetails[i + 1]);
      }
    }
    // create and init the component builder
    this.context = new DefaultBeneratorContext();
    ComponentBuilder<?> builder = ComponentBuilderFactory.createComponentBuilder(part, Uniqueness.NONE, false, context);
    assertNotNull(builder);
    builder.init(this.context);
    return builder;
  }

  public Entity createEntity() {
    return new Entity("Entity", testDescriptorProvider);
  }

}
