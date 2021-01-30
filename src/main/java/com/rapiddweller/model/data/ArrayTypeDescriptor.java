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

package com.rapiddweller.model.data;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Describes an array.<br/><br/>
 * Created: 29.04.2010 07:32:52
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class ArrayTypeDescriptor extends TypeDescriptor {

    private final Map<Integer, ArrayElementDescriptor> elements;

    public ArrayTypeDescriptor(String name, DescriptorProvider provider) {
        this(name, provider, null);
    }

    public ArrayTypeDescriptor(String name, DescriptorProvider provider,
                               ArrayTypeDescriptor parent) {
        super(name, provider, parent);
        this.elements = new TreeMap<>();
    }

    // element handling ------------------------------------------------------------------------------------------------

    @Override
    public ArrayTypeDescriptor getParent() {
        return (ArrayTypeDescriptor) super.getParent();
    }

    public void addElement(ArrayElementDescriptor descriptor) {
        elements.put(descriptor.getIndex(), descriptor);
    }

    public ArrayElementDescriptor getElement(int index) {
        return elements.get(index);
    }

    public ArrayElementDescriptor getElement(int index, boolean inherit) {
        ArrayElementDescriptor element = getElement(index);
        if (element != null) {
            return element;
        }
        ArrayTypeDescriptor tmp = getParent();
        while (tmp != null && inherit) {
            ArrayElementDescriptor candidate = tmp.getElement(index);
            if (candidate != null) {
                return candidate;
            }
            tmp = tmp.getParent();
        }
        return null;
    }

    public Collection<ArrayElementDescriptor> getElements() {
        return elements.values();
    }

    public int getElementCount() {
        return (parent != null ?
                ((ArrayTypeDescriptor) parent).getElementCount() :
                elements.size());
    }

    // variable handling -----------------------------------------------------------------------------------------------
    /*
    public void addVariable(VariableDescriptor variable) {
        parts.add(variable);
    }
    */

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        if (elements.size() == 0) {
            return super.toString();
        }
        //return new CompositeFormatter(false, false).render(super.toString() + '{', new CompositeAdapter(), "}");
        return getClass().getSimpleName() + "[name=" + getName() +
                ", elements=" + elements.toString() + ']';
    }

}
