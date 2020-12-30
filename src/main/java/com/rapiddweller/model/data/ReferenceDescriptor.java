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

import com.rapiddweller.common.operation.FirstArgSelector;

/**
 * Describes a reference to an instance of a complex type (see {@link ComplexTypeDescriptor}).<br/>
 * <br/>
 * Created: 27.02.2008 16:28:22
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class ReferenceDescriptor extends ComponentDescriptor {

    private static final String TARGET_TYPE = "targetType";
    private static final String TARGET_COMPONENT = "targetComponent";

    // constructors ----------------------------------------------------------------------------------------------------

    public ReferenceDescriptor(String name, DescriptorProvider provider, String typeName) {
        this(name, provider, typeName, null, null);
    }

    public ReferenceDescriptor(String name, DescriptorProvider provider, String typeName, String targetType, String targetComponent) {
        // TODO v0.7.2 test non-PK reference
        super(name, provider, typeName);
        addConstraint(TARGET_TYPE, String.class, new FirstArgSelector<>());
        addConstraint(TARGET_COMPONENT, String.class, new FirstArgSelector<>());
        setTargetType(targetType);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getTargetType() {
        return (String) getDetailValue(TARGET_TYPE);
    }

    public void setTargetType(String targetType) {
        setDetailValue(TARGET_TYPE, targetType);
    }

    public String getTargetComponent() {
        return (String) getDetailValue(TARGET_COMPONENT);
    }

    public void setTargetComponent(String targetComponent) {
        setDetailValue(TARGET_COMPONENT, targetComponent);
    }

    // convenience-with-methods for construction -----------------------------------------------------------------------

    public ReferenceDescriptor withTargetType(String targetType) {
        setTargetType(targetType);
        return this;
    }

    public ReferenceDescriptor withTargetComponent(String targetComponent) {
        setTargetComponent(targetComponent);
        return this;
    }

}