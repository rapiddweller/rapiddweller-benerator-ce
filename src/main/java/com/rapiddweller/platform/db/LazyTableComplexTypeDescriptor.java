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

package com.rapiddweller.platform.db;

import com.rapiddweller.model.data.*;
import com.rapiddweller.jdbacl.model.DBTable;

import java.util.Collection;
import java.util.List;

/**
 * Lazily initialized {@link ComplexTypeDescriptor} that reads its components from a database table.<br/><br/>
 * Created: 30.11.2010 19:23:33
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class LazyTableComplexTypeDescriptor extends ComplexTypeDescriptor {

    DBTable table;
    DBSystem db;
    boolean loaded;

    public LazyTableComplexTypeDescriptor(DBTable table, DBSystem db) {
        super(table.getName(), db);
        this.table = table;
        this.db = db;
        this.loaded = false;
    }

    @Override
    public List<InstanceDescriptor> getParts() {
        assureLoaded();
        return super.getParts();
    }

    @Override
    public void addComponent(ComponentDescriptor component) {
        assureLoaded();
        super.addComponent(component);
    }

    private void assureLoaded() {
        if (!loaded) {
            loaded = true;
            db.mapTableToComplexTypeDescriptor(table, this);
        }
    }

    @Override
    public void setComponent(ComponentDescriptor component) {
        assureLoaded();
        super.setComponent(component);
    }

    @Override
    public ComponentDescriptor getComponent(String name) {
        assureLoaded();
        return super.getComponent(name);
    }

    @Override
    public List<ComponentDescriptor> getComponents() {
        assureLoaded();
        return super.getComponents();
    }

    @Override
    public Collection<InstanceDescriptor> getDeclaredParts() {
        assureLoaded();
        return super.getDeclaredParts();
    }

    @Override
    public boolean isDeclaredComponent(String componentName) {
        assureLoaded();
        return super.isDeclaredComponent(componentName);
    }

    @Override
    public String[] getIdComponentNames() {
        assureLoaded();
        return super.getIdComponentNames();
    }

    @Override
    public void addPart(InstanceDescriptor part) {
        assureLoaded();
        super.addPart(part);
    }

    @Override
    public List<ReferenceDescriptor> getReferenceComponents() {
        assureLoaded();
        return super.getReferenceComponents();
    }

    @Override
    public void addVariable(VariableDescriptor variable) {
        assureLoaded();
        super.addVariable(variable);
    }

    // construction helper methods -------------------------------------------------------------------------------------

    @Override
    public ComplexTypeDescriptor withComponent(ComponentDescriptor componentDescriptor) {
        assureLoaded();
        addComponent(componentDescriptor);
        return this;
    }

}
