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

package com.rapiddweller.platform.memstore;

import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.benerator.util.FilterExDataSource;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.Context;
import com.rapiddweller.commons.OrderedMap;
import com.rapiddweller.commons.StringUtil;
import com.rapiddweller.commons.collection.OrderedNameMap;
import com.rapiddweller.formats.DataSource;
import com.rapiddweller.formats.script.ScriptUtil;
import com.rapiddweller.formats.util.DataSourceFromIterable;
import com.rapiddweller.formats.util.DataSourceProxy;
import com.rapiddweller.script.Expression;

import java.util.Collection;
import java.util.Map;

/**
 * Simple heap-based implementation of the AbstractStorageSystem interface.<br/><br/>
 * Created: 07.03.2011 14:41:40
 *
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class MemStore extends AbstractStorageSystem {

    static boolean ignoreClose = false; // for testing

    private final String id;
    private final OrderedNameMap<ComplexTypeDescriptor> types;
    private final Map<String, Map<Object, Entity>> typeMap;

    public MemStore(String id, DataModel dataModel) {
        this.setDataModel(dataModel);
        this.types = OrderedNameMap.createCaseInsensitiveMap();
        typeMap = OrderedNameMap.createCaseInsensitiveMap();
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public DataSource<Entity> queryEntities(String entityType, String selector, Context context) {
        Map<?, Entity> idMap = getOrCreateIdMapForType(entityType);
        DataSource<Entity> result = new DataSourceProxy<>(new DataSourceFromIterable<>(idMap.values(), Entity.class));
        if (!StringUtil.isEmpty(selector)) {
            Expression<Boolean> filterEx = new ScriptExpression<>(ScriptUtil.parseScriptText(selector));
            result = new FilterExDataSource<>(result, filterEx, context);
        }
        return result;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public DataSource<?> queryEntityIds(String entityType, String selector, Context context) {
        Map<?, Entity> idMap = getOrCreateIdMapForType(entityType);
        DataSource<?> result = new DataSourceProxy(new DataSourceFromIterable(idMap.keySet(), Object.class));
        if (!StringUtil.isEmpty(selector)) {
            Expression<Boolean> filterEx = new ScriptExpression<>(ScriptUtil.parseScriptText(selector));
            result = new FilterExDataSource(result, filterEx, context);
        }
        return result;
    }

    @Override
    public DataSource<?> query(String selector, boolean simplify, Context context) {
        throw new UnsupportedOperationException(getClass() + " does not support query(String, Context)");
    }

    @Override
    public void store(Entity entity) {
        String entityType = entity.type();
        Map<Object, Entity> idMap = getOrCreateIdMapForType(entityType);
        Object idComponentValues = entity.idComponentValues();
        if (idComponentValues == null)
            idComponentValues = entity.getComponents().values();
        idMap.put(idComponentValues, entity);
        if (!types.containsKey(entityType))
            types.put(entityType, new ComplexTypeDescriptor(entityType, this));
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
    }

    @Override
    public void close() {
        if (!ignoreClose)
            typeMap.clear();
    }

    public void printContent() {
        for (Map.Entry<String, Map<Object, Entity>> typeEntry : typeMap.entrySet()) {
            System.out.println(typeEntry.getKey() + ':');
            for (Map.Entry<Object, Entity> valueEntry : typeEntry.getValue().entrySet()) {
                System.out.println(valueEntry.getKey() + ": " + valueEntry.getValue());
            }
        }
    }

    private Map<Object, Entity> getOrCreateIdMapForType(String entityType) {
        Map<Object, Entity> idMap = typeMap.get(entityType);
        if (idMap == null) {
            idMap = new OrderedMap<>();
            typeMap.put(entityType, idMap);
        }
        return idMap;
    }

    public Collection<Entity> getEntities(String entityType) {
        return typeMap.get(entityType).values();
    }

}
