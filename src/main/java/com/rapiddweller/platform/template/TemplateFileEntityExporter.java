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

package com.rapiddweller.platform.template;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.accessor.FeatureAccessor;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.mutator.AnyMutator;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptException;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.model.data.Entity;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Exports generated data using template files,
 * for example based on the FreeMarker Template Language.<br/><br/>
 * Created: 27.06.2014 16:50:44
 * @author Volker Bergmann
 * @since 0.9.7
 */
public class TemplateFileEntityExporter implements Consumer, ContextAware {

  private static final Logger logger = LoggerFactory.getLogger(TemplateFileEntityExporter.class);

  // attributes ------------------------------------------------------------------------------------------------------

  private String templateUri;
  private String uri;
  private String encoding;
  private Class<? extends TemplateRecord> recordType;

  private TemplateRecord root;
  private Stack<TemplateRecord> stack;

  private Context context;


  // constructors ----------------------------------------------------------------------------------------------------

  public TemplateFileEntityExporter() {
    this.recordType = DefaultTemplateRecord.class;
  }


  // properties ------------------------------------------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  private static void updateFeature(String featureName, TemplateRecord parent, TemplateRecord product) {
    Object previousObject = FeatureAccessor.getValue(parent, featureName, false);
    if (previousObject == null) {
      List<TemplateRecord> list = new ArrayList<>();
      list.add(product);
      AnyMutator.setValue(parent, featureName, list);
      //parentMap.put(product.type(), entityMap);
    } else if (previousObject instanceof List) {
      ((List<TemplateRecord>) previousObject).add(product);
    } else {
      throw BeneratorExceptionFactory.getInstance().programmerUnsupported("Invalid assumption");
    }
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public String getTemplateUri() {
    return templateUri;
  }


  // ContextAware interface implementation ---------------------------------------------------------------------------

  public void setTemplateUri(String templateUri) {
    this.templateUri = templateUri;
  }

  public Class<? extends TemplateRecord> getRecordType() {
    return recordType;
  }

  public void setRecordType(Class<? extends TemplateRecord> recordType) {
    this.recordType = recordType;
  }


  // Consumer interface implementation -------------------------------------------------------------------------------

  @Override
  public void setContext(Context context) {
    this.context = context;
  }

  @Override
  public void startConsuming(ProductWrapper<?> wrapper) {
    if (root == null) {
      init();
    }
    Object object = wrapper.unwrap();
    if (!(object instanceof Entity)) {
      throw ExceptionFactory.getInstance().configurationError(
          getClass() + " can only consume Entities, but was provided with a " + object.getClass());
    }
    Entity product = (Entity) object;
    TemplateRecord productRecord = entityToRecord(product);
    String featureName = product.type();
    TemplateRecord parentRecord = stack.peek();
    updateFeature(featureName, parentRecord, productRecord);
    stack.push(productRecord);
  }

  @Override
  public void finishConsuming(ProductWrapper<?> wrapper) {
    Entity product = (Entity) wrapper.unwrap();
    if (stack.isEmpty()) {
      throw ExceptionFactory.getInstance().configurationError(
          "Trying to pop product from empty stack: '" + product + "'");
    }
    stack.pop();
  }

  @Override
  public void close() {
    if (root != null) {
      logger.debug("Writing file {}", uri);
      try {
        Script template = ScriptUtil.readFile(templateUri);
        mapRootToContext();
        Context subContext = new DefaultContext(context);
        String text = ToStringConverter.convert(template.evaluate(subContext), "");
        String path = uri.replace('/', File.separatorChar);
        File folder = new File(path).getParentFile();
        if (folder != null) {
          folder.mkdirs();
        }
        IOUtil.writeTextFile(uri, text, encoding);
      } catch (ScriptException e) {
        throw ExceptionFactory.getInstance().configurationError(
            "Error evaluating template " + templateUri, e);
      }
    } else {
      logger.error("Unable to write file {}", uri);
    }
  }


  // private helper methods ------------------------------------------------------------------------------------------

  @Override
  public void flush() {
    // nothing to do for this class
  }

  private void init() {
    this.root = BeanUtil.newInstance(recordType);
    this.stack = new Stack<>();
    this.stack.push(root);
  }

  private TemplateRecord entityToRecord(Entity entity) {
    TemplateRecord record = BeanUtil.newInstance(recordType);
    for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
      AnyMutator.setValue(record, entry.getKey(), entry.getValue());
    }
    return record;
  }

  private void mapRootToContext() {
    for (Map.Entry<String, ?> entry : root.entrySet()) {
      context.set(entry.getKey(), entry.getValue());
    }
  }

}
