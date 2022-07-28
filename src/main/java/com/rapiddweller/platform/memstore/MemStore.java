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

package com.rapiddweller.platform.memstore;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.benerator.util.FilterExDataSource;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.common.exception.IllegalArgumentError;
import com.rapiddweller.common.ui.TextPrinter;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.util.DataSourceFromIterable;
import com.rapiddweller.format.util.DataSourceProxy;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.platform.db.DefaultDBSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simple heap-based implementation of the AbstractStorageSystem interface.<br/><br/>
 * Created: 07.03.2011 14:41:40
 *
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class MemStore extends AbstractStorageSystem {

  private static final Logger logger = LoggerFactory.getLogger(MemStore.class);

  static boolean ignoreClose = false; // for testing

  private final String id;
  private final OrderedNameMap<ComplexTypeDescriptor> types;
  private final Map<String, EntityStore> entitiesByType;

  public MemStore(String id, DataModel dataModel) {
    this.setDataModel(dataModel);
    this.types = OrderedNameMap.createCaseInsensitiveMap();
    this.entitiesByType = OrderedNameMap.createCaseInsensitiveMap();
    this.id = id;
  }

  // interface -------------------------------------------------------------------------------------------------------

  @Override
  public String getId() {
    return id;
  }

  public Consumer inserter() {
    return new MemStoreStorer(this);
  }

  public Consumer inserter(String typeName) {
    return new MemStoreStorer(this, typeName);
  }

  @Override
  public Consumer updater() {
    return new MemStoreStorer(this);
  }

  public Consumer updater(String typeName) {
    return new MemStoreStorer(this, typeName);
  }

  public int totalEntityCount() {
    int result = 0;
    for (EntityStore entityStore : entitiesByType.values()) {
      result += entityStore.size();
    }
    return result;
  }

  public int entityCount(String type) {
    return entitiesByType.get(type).size();
  }

  public void filter(String entityName, String filterColumn, DataSource<Entity> list) {
    ArrayList<Object> filterList = new ArrayList<>();
    DataIterator<Entity> iterator = list.iterator();
    DataContainer<Entity> container = new DataContainer<>();
    while ((container = iterator.next(container)) != null) {
      Entity entity = container.getData();
      filterList.add(entity.get(filterColumn));
    }
    this.getEntities(entityName).removeIf(entity -> !filterList.contains(entity.get(filterColumn)));
  }

  public void removeNotExistingIds(String entityName, String filterColumn, String filterTable, Object db) {
    ArrayList<Object> filterList = new ArrayList<>();
    DataIterator<Entity> iterator = null;
    if (db.getClass() == DefaultDBSystem.class) {
      iterator = ((AbstractStorageSystem) db).queryEntities("1", String.format("SELECT DISTINCT \"%s\" FROM \"%s\"", filterColumn, filterTable), null)
          .iterator();
    } else if (db.getClass() == MemStore.class) {
      iterator = ((MemStore) db).queryEntities(filterTable, "", null).iterator();
    }
    if (iterator != null) {
      DataContainer<Entity> container = new DataContainer<>();
      while ((container = iterator.next(container)) != null) {
        Entity entity = container.getData();
        filterList.add(entity.get(filterColumn));
      }
      this.getEntities(entityName).removeIf(entity -> !filterList.contains(entity.get(filterColumn)));
    }
  }

  public int sumEntityColumn(String type, String column) {
    int result = 0;
    EntityStore e = entitiesByType.get(type);
    if (e != null) {
      for (Entity entity : e.entities()) {
        Object value = entity.get(column);
        if (value != null && value.getClass() == Integer.class) {
          result += (Integer) value;
        } else if (value != null && value.getClass() == Double.class) {
          result += (Double) value;
        }  else if (value != null && value.getClass() == String.class) {
          try {
            result += Integer.parseInt((String) value);
          } catch (NumberFormatException e1) {
            logger.warn("Could not parse {} as integer try to parse as double", value);
            try {
              result += Double.parseDouble((String) value);
            } catch (NumberFormatException e2) {
              logger.warn("Could not parse value {}, not a numeric value", value);
            }
          }
        } else {
          throw new IllegalArgumentError(
              String.format("Entity %s does not have a column with name %s or given column is not a numeric value", type, column));
        }
      }
    } else {
      throw new IllegalArgumentError(String.format("Entity %s does not exist in Memstore", type));
    }
    return result;
  }

  @Override
  public DataSource<Entity> queryEntities(String entityType, String selector, Context context) {
    DataSource<Entity> result = new DataSourceFromIterable<>(entitiesByType.get(entityType), Entity.class);
    if (!StringUtil.isEmpty(selector)) {
      Expression<Boolean> filterEx = new ScriptExpression<>(ScriptUtil.parseScriptText(selector));
      result = new FilterExDataSource<>(result, filterEx, context);
    }
    return result;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public DataSource<?> queryEntityIds(String entityType, String filter, Context context) {
    Map<Object, Entity> idMap = entitiesByType.get(entityType).idMap();
    if (idMap == null) { // if the queried entityType has no id, then return null
      return null;
    }
    DataSource<?> result = new DataSourceProxy(new DataSourceFromIterable(idMap.keySet(), Object.class));
    if (!StringUtil.isEmpty(filter)) {
      Expression<Boolean> filterEx = new ScriptExpression<>(ScriptUtil.parseScriptText(filter));
      result = new FilterExDataSource(result, filterEx, context);
    }
    return result;
  }

  @Override
  public DataSource<?> query(String selector, boolean simplify, Context context) {
    logger.debug("query({})", selector);
    throw BeneratorExceptionFactory.getInstance().programmerUnsupported(getClass() + " does not support query(String, Context)");
  }

  @Override
  public void store(Entity entity) {
    logger.debug("Storing entity {}", entity);
    String entityType = entity.type();
    // store entity
    EntityStore entityStore = entitiesByType.computeIfAbsent(entityType, k -> createEntityStore(entity.descriptor()));
    entityStore.store(entity);
    // store entity descriptor
    types.computeIfAbsent(entityType, k -> new ComplexTypeDescriptor(entityType, this));
  }

  @Override
  public void update(Entity entity) {
    store(entity);
  }

  @Override
  public TypeDescriptor[] getTypeDescriptors() {
    return CollectionUtil.toArray(types.values(), TypeDescriptor.class);
  }

  @Override
  public TypeDescriptor getTypeDescriptor(String typeName) {
    return types.get(typeName);
  }

  @Override
  public void flush() {
    logger.debug("flush()");
    // nothing to do for here for a MemStore
  }

  @Override
  public void close() {
    logger.debug("close()");
    if (!ignoreClose) {
      entitiesByType.clear();
    }
  }

  public void printContent(TextPrinter printer) {
    for (Map.Entry<String, EntityStore> typeEntry : entitiesByType.entrySet()) {
      printer.printStd(typeEntry.getKey() + ':');
      int index = 0;
      for (Entity entity : typeEntry.getValue()) {
        printer.printStd(index++ + ": " + entity);
      }
    }
  }

  public List<Entity> getEntities(String entityType) {
    EntityStore entityStore = entitiesByType.get(entityType);
    return (entityStore != null ? entityStore.entities() : new ArrayList<>());
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private EntityStore createEntityStore(ComplexTypeDescriptor type) {
    String[] idComponentNames = type.getIdComponentNames();
    if (ArrayUtil.isEmpty(idComponentNames)) {
      return new UnidentifiedEntityStore(type);
    } else {
      return new IdEntityStore(type);
    }
  }

}
