/*
 * (c) Copyright 2006-2023 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.*;
import com.rapiddweller.benerator.composite.ComponentTypeConverter;
import com.rapiddweller.benerator.composite.SimpleTypeEntityGenerator;
import com.rapiddweller.benerator.distribution.DistributingGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.TypedEntitySourceAdapter;
import com.rapiddweller.benerator.factory.*;
import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.*;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.fixedwidth.FixedWidthColumnDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthRowTypeDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthUtil;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.util.DataFileUtil;
import com.rapiddweller.format.util.ThreadLocalDataContainer;
import com.rapiddweller.model.data.*;
import com.rapiddweller.platform.csv.CSVEntitySourceProvider;
import com.rapiddweller.platform.dbunit.DbUnitEntitySource;
import com.rapiddweller.platform.fixedwidth.FixedWidthEntitySource;
import com.rapiddweller.platform.xls.PlatformDescriptor;
import com.rapiddweller.platform.xls.XLSEntitySourceProvider;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;

import java.text.ParseException;

public class DynamicSourceGenerator extends ThreadSafeGenerator<Entity> {

  private final Script script;
  private final Uniqueness uniqueness;
  private final BeneratorContext context;
  private final ComplexTypeDescriptor descriptor;
  private final ComplexTypeGeneratorFactory factory;
  private Generator<Entity> source;
  private DataIterator<Entity> iterator;
  private final ThreadLocalDataContainer<Entity> container = new ThreadLocalDataContainer<>();

  

  public DynamicSourceGenerator(Script script,
                                Uniqueness uniqueness,
                                BeneratorContext context,
                                ComplexTypeDescriptor descriptor
          , ComplexTypeGeneratorFactory factory
  ) {
    this.script = script;
    this.uniqueness = uniqueness;
    this.context = context;
    this.descriptor = descriptor;

    this.factory = factory;
  }

  public Class<Entity> getGeneratedType() {
    return Entity.class;
  }

  public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
    source = factory.resolveDynamicSourceGenerator(descriptor, uniqueness, context);
    iterator = ((DataSourceGenerator) ((ConvertingGenerator) source).getSource()).getSource().iterator();
    DataContainer<Entity> tmp = iterator.next(container.get());
    if (tmp == null) {
      IOUtil.close(iterator);
      return null;
    }
    return wrapper.wrap(tmp.getData());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + script + ']';
  }
  
}
