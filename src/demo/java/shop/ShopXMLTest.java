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

package shop;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.factory.MetaGeneratorFactory;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.validator.StringLengthValidator;
import com.rapiddweller.domain.product.EAN13Validator;
import com.rapiddweller.domain.product.EAN8Validator;
import com.rapiddweller.domain.product.EANValidator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.xml.XMLSchemaDescriptorProvider;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Tests processing of the shop.xsd file.<br/>
 * <br/>
 * Created: 28.02.2008 15:17:13
 *
 * @author Volker Bergmann
 */
public class ShopXMLTest extends GeneratorTest {

  private static final Logger logger =
      LogManager.getLogger(ShopXMLTest.class);

  /**
   * The Schema uri.
   */
  String schemaUri = "demo/shop/schema/shop.xsd";
  /**
   * The Context uri.
   */
  String contextUri = IOUtil.getParentUri(schemaUri);

  /**
   * Test.
   */
  @Test
  public void test() {
    System.setProperty("stage", "test");
    DataModel dataModel = new DataModel();
    XMLSchemaDescriptorProvider provider =
        new XMLSchemaDescriptorProvider(schemaUri, context);
    dataModel.validate();

    logger.debug("Supported types:");
    logger.debug("----------------");
    for (TypeDescriptor descriptor : provider.getTypeDescriptors()) {
      logger.debug(descriptor.toString());
    }

    checkSimpleType("category-id", provider, new CategoryIdValidator());
    checkSimpleType("string30", provider, new StringLengthValidator(30));
    checkSimpleType("ean-type", provider, new EANValidator());
    checkSimpleType("ean8-type", provider, new EAN8Validator());
    checkSimpleType("ean13-type", provider, new EAN13Validator());
    checkSimpleType("price-type", provider, new PriceValidator());
    checkSimpleType("surrogate-id", provider, new LongValidator());
    checkSimpleType("string16", provider, new StringLengthValidator(16));

    checkComplexType("audited", provider, new AuditedValidator());
    checkComplexType("audited-updateable", provider,
        new AuditedUpdateableValidator());
    checkComplexType("category", provider, new CategoryValidator());
    checkComplexType("product", provider, new ProductValidator());
    checkComplexType("admin", provider, new UserValidator("admin"));
    checkComplexType("clerk", provider, new UserValidator("clerk"));
    checkComplexType("customer", provider, new CustomerValidator());
  }

  @SuppressWarnings("unchecked")
  private <T> void checkSimpleType(String name,
                                   XMLSchemaDescriptorProvider provider,
                                   Validator<T> validator) {
    SimpleTypeDescriptor descriptor =
        (SimpleTypeDescriptor) provider.getTypeDescriptor(name);
    logger.debug("");
    logger.debug("Testing simple type: " + descriptor.getName());
    logger.debug("-------------------------------------");
    Generator<T> generator =
        (Generator<T>) MetaGeneratorFactory.createTypeGenerator(
            descriptor, descriptor.getName(), false,
            Uniqueness.NONE, provider.getContext());
    generator.init(new DefaultBeneratorContext());
    for (int i = 0; i < 10; i++) {
      T object = GeneratorUtil.generateNonNull(generator);
      logger.debug(object.toString());
      assertTrue("Invalid object: " + object, validator.valid(object));
    }
  }

  @SuppressWarnings({"unchecked", "cast"})
  private void checkComplexType(String name,
                                XMLSchemaDescriptorProvider provider,
                                Validator<Entity> validator) {
    ComplexTypeDescriptor descriptor =
        (ComplexTypeDescriptor) provider.getTypeDescriptor(name);
    logger.debug("");
    logger.debug("Testing complex type: " + descriptor.getName());
    logger.debug("-------------------------------------");
    Generator<?> tmp =
        (Generator<Entity>) MetaGeneratorFactory.createTypeGenerator(
            descriptor, "instance", false, Uniqueness.NONE,
            provider.getContext());
    assertEquals(Entity.class, tmp.getGeneratedType());
    Generator<Entity> generator = (Generator<Entity>) tmp;
    generator.init(new DefaultBeneratorContext());
    for (int i = 0; i < 10; i++) {
      Entity entity = GeneratorUtil.generateNonNull(generator);
      if (entity != null) {
        logger.debug(entity.toString());
        assertTrue("Invalid entity: " + entity,
            validator.valid(entity));
      }
    }
  }

}
