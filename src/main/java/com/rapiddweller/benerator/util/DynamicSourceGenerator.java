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

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.ComplexTypeGeneratorFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.Uniqueness;

public class DynamicSourceGenerator extends AbstractGenerator<Entity> {
    private final Uniqueness uniqueness;
    private final BeneratorContext context;
    private final ComplexTypeDescriptor descriptor;
    private final ComplexTypeGeneratorFactory factory;
    private Generator<Entity> generator;

    public DynamicSourceGenerator(Uniqueness uniqueness, BeneratorContext context, ComplexTypeDescriptor descriptor, ComplexTypeGeneratorFactory factory) {
        this.uniqueness = uniqueness;
        this.context = context;
        this.descriptor = descriptor;
        this.factory = factory;
    }

    public Class<Entity> getGeneratedType() {
        return Entity.class;
    }

    public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
        // Resolve dynamicSource text then init generator
        if (generator == null) {
            generator = factory.resolveDynamicSourceGenerator(descriptor, uniqueness, context);
            generator.init(context);
        }
        var data = generator.generate(wrapper);
        if (data == null) {
            // Close generator when iterator reach the end.
            generator.close();
            // Unset this.generator for next product using
            generator = null;
            return null;
        }
        return wrapper.wrap(data.unwrap());
    }

    @Override
    public boolean isParallelizable() {
        return false;
    }

    @Override
    public boolean isThreadSafe() {
        return false;
    }
}
