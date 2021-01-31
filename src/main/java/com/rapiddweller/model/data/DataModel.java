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

import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.platform.java.BeanDescriptorProvider;
import com.rapiddweller.script.PrimitiveType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Merges and organizes entity definitions of different systems.<br/><br/>
 * Created: 25.08.2007 20:40:17
 *
 * @author Volker Bergmann
 * @since 0.3
 */
public class DataModel {

    private final Logger logger = LogManager.getLogger(DataModel.class);

    private final Map<String, DescriptorProvider> providers;

    private boolean acceptUnknownPrimitives;

    public DataModel() {
        this.acceptUnknownPrimitives = false;
        this.providers = new HashMap<>();
        new PrimitiveDescriptorProvider(this);
        new BeanDescriptorProvider(this);
    }

    private static TypeDescriptor searchCaseInsensitive(
            DescriptorProvider provider, String name) {
        for (TypeDescriptor type : provider.getTypeDescriptors()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public void setAcceptUnknownPrimitives(boolean acceptUnknownPrimitives) {
        this.acceptUnknownPrimitives = acceptUnknownPrimitives;
    }

    public void addDescriptorProvider(DescriptorProvider provider) {
        addDescriptorProvider(provider, true);
    }

    public void addDescriptorProvider(DescriptorProvider provider,
                                      boolean validate) {
        providers.put(provider.getId(), provider);
        provider.setDataModel(this);
        if (validate) {
            validate();
        }
    }

    public DescriptorProvider getDescriptorProvider(String id) {
        return providers.get(id);
    }

    public void removeDescriptorProvider(String id) {
        providers.remove(id);
    }

    public TypeDescriptor getTypeDescriptor(String typeId) {
        if (typeId == null) {
            return null;
        }
        String namespace = null;
        String name = typeId;
        if (typeId.contains(":")) {
            int i = typeId.indexOf(':');
            namespace = typeId.substring(0, i);
            name = typeId.substring(i + 1);
        }
        return getTypeDescriptor(namespace, name);
    }

    public TypeDescriptor getTypeDescriptor(String namespace, String name) {
        if (name == null) {
            return null;
        }
        if (namespace != null) {
            DescriptorProvider provider = providers.get(namespace);
            if (provider != null) {
                // first, search case-sensitive
                TypeDescriptor typeDescriptor =
                        provider.getTypeDescriptor(name);
                if (typeDescriptor != null) {
                    return typeDescriptor;
                } else {
                    // not found yet, try it case-insensitive
                    return searchCaseInsensitive(provider, name);
                }
            }
        }
        // first, search case-sensitive
        for (DescriptorProvider provider : providers.values()) {
            TypeDescriptor descriptor = provider.getTypeDescriptor(name);
            if (descriptor != null) {
                return descriptor;
            }
        }
        // not found yet, try it case-insensitive
        for (DescriptorProvider provider : providers.values()) {
            TypeDescriptor descriptor = searchCaseInsensitive(provider, name);
            if (descriptor != null) {
                return descriptor;
            }
        }
        return null;
    }

    // private helpers -------------------------------------------------------------------------------------------------

    public void validate() {
        for (DescriptorProvider provider : providers.values()) {
            for (TypeDescriptor desc : provider.getTypeDescriptors()) {
                validate(desc);
            }
        }
    }

    private void validate(TypeDescriptor type) {
        logger.debug("validating " + type);
        if (type instanceof SimpleTypeDescriptor) {
            validate((SimpleTypeDescriptor) type);
        } else if (type instanceof ComplexTypeDescriptor) {
            validate((ComplexTypeDescriptor) type);
        } else if (type instanceof ArrayTypeDescriptor) {
            validate((ArrayTypeDescriptor) type);
        } else {
            throw new UnsupportedOperationException(
                    "Descriptor type not supported: " + type.getClass());
        }
    }

    private void validate(SimpleTypeDescriptor desc) {
        PrimitiveType primitiveType = desc.getPrimitiveType();
        if (primitiveType == null && !acceptUnknownPrimitives) {
            throw new ConfigurationError(
                    "No primitive type defined for simple type: " +
                            desc.getName());
        }
    }

    private void validate(ComplexTypeDescriptor desc) {
        for (ComponentDescriptor component : desc.getComponents()) {
            TypeDescriptor type = component.getTypeDescriptor();
            if (type == null) {
                throw new ConfigurationError(
                        "Type of component is not defined: " + desc.getName());
            } else if (!(type instanceof ComplexTypeDescriptor)) {
                validate(type);
            }
        }
    }

    private void validate(ArrayTypeDescriptor desc) {
        for (ArrayElementDescriptor element : desc.getElements()) {
            TypeDescriptor type = element.getTypeDescriptor();
            if (!(type instanceof ComplexTypeDescriptor)) {
                validate(type);
            }
        }
    }

    public SimpleTypeDescriptor getPrimitiveTypeDescriptor(Class<?> javaType) {
        PrimitiveDescriptorProvider primitiveProvider =
                (PrimitiveDescriptorProvider) providers
                        .get(PrimitiveDescriptorProvider.NAMESPACE);
        return primitiveProvider.getPrimitiveTypeDescriptor(javaType);
    }

    public BeanDescriptorProvider getBeanDescriptorProvider() {
        return (BeanDescriptorProvider) providers
                .get(BeanDescriptorProvider.NAMESPACE);
    }

}
