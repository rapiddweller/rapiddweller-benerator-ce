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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;

/**
 * Facade class for the various {@link MetaGeneratorFactory} implementations.<br/><br/>
 * Created: 06.09.2011 07:30:48
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class MetaGeneratorFactory {

    private static ArrayTypeGeneratorFactory arrayTypeGeneratorFactory = new ArrayTypeGeneratorFactory();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Generator<?> createRootGenerator(
    		InstanceDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
		boolean nullable = DescriptorUtil.isNullable(descriptor, context);
		String instanceName = descriptor.getName();
        TypeDescriptor type = descriptor.getTypeDescriptor();
		TypeGeneratorFactory factory = factoryFor(type);
        Generator generator = factory.createRootGenerator(type, instanceName, nullable, uniqueness, context);
        generator = factory.applyOffsetAndCyclic(generator, descriptor.getTypeDescriptor(), instanceName, uniqueness, context);
		return generator;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Generator<?> createTypeGenerator(TypeDescriptor type, String instanceName, 
    		boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
		TypeGeneratorFactory factory = factoryFor(type);
		return factory.createGenerator(type, instanceName, nullable, uniqueness, context);
    }

    protected static TypeGeneratorFactory<?> factoryFor(TypeDescriptor type) {
        if (type instanceof SimpleTypeDescriptor)
			return BeneratorFactory.getInstance().getSimpleTypeGeneratorFactory();
		else if (type instanceof ComplexTypeDescriptor)
    		return BeneratorFactory.getInstance().getComplexTypeGeneratorFactory();
        else if (type instanceof ArrayTypeDescriptor)
    		return arrayTypeGeneratorFactory;
        else
            throw new UnsupportedOperationException("Descriptor type not supported: " + type.getClass());
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Generator<?> createNullGenerator(TypeDescriptor type, BeneratorContext context) {
		Class generatedType;
		if (type != null)
			generatedType = ((TypeGeneratorFactory) factoryFor(type)).getGeneratedType(type);
		else
			generatedType = Object.class;
		return context.getGeneratorFactory().createNullGenerator(generatedType);
	}
    
}
