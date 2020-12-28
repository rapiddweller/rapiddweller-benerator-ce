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

import static com.rapiddweller.model.data.TypeDescriptor.PATTERN;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.BeanUtil;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.Validator;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.model.data.VariableDescriptor;

/**
 * Factory class that creates {@link Generator}s for &lt;variable&gt;s.<br/><br/>
 * Created: 18.05.2011 12:01:24
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class VariableGeneratorFactory {

	public static Generator<?> createGenerator(
			VariableDescriptor descriptor, BeneratorContext context) {
		Generator<?> generator = null;
		
		// check if nullQuota == 1
		generator = DescriptorUtil.createNullQuotaOneGenerator(descriptor, context);
		if (generator != null)
			return null;
		
		// check for script
		TypeDescriptor type = descriptor.getTypeDescriptor();
		generator = TypeGeneratorFactory.createScriptGenerator(type);
		if (generator != null) {
	        generator = wrapWithPostprocessors(generator, type, context);
	        generator = wrapWithProxy(generator, type);
		}

		if (generator == null)
			generator = InstanceGeneratorFactory.createSingleInstanceGenerator(descriptor, Uniqueness.NONE, context);
		
		return context.getGeneratorFactory().applyNullSettings(generator, descriptor.isNullable(), descriptor.getNullQuota());
	}

    static Generator<?> wrapWithPostprocessors(Generator<?> generator, TypeDescriptor descriptor, BeneratorContext context) {
		generator = createConvertingGenerator(descriptor, generator, context);
		if (descriptor instanceof SimpleTypeDescriptor) {
			SimpleTypeDescriptor simpleType = (SimpleTypeDescriptor) descriptor;
			generator = TypeGeneratorFactory.createMappingGenerator(simpleType, generator);
			generator = createTypeConvertingGenerator(simpleType, generator);
		}
        generator = createValidatingGenerator(descriptor, generator, context);
		return generator;
	}
    
    @SuppressWarnings("unchecked")
    protected static <T> Generator<T> createValidatingGenerator(
            TypeDescriptor descriptor, Generator<T> generator, BeneratorContext context) {
		Validator<T> validator = DescriptorUtil.getValidator(descriptor.getValidator(), context);
        if (validator != null)
            generator = WrapperFactory.applyValidator(validator, generator);
        return generator;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	static Generator<?> createTypeConvertingGenerator(SimpleTypeDescriptor descriptor, Generator<?> generator) {
        if (descriptor == null || descriptor.getPrimitiveType() == null)
            return generator;
        Converter<?, ?> converter = TypeGeneratorFactory.createConverter(descriptor, generator.getGeneratedType());
        if (converter != null)
        	return WrapperFactory.applyConverter((Generator) generator, converter);
        else
        	return generator;
    }

    public static Generator<?> createConvertingGenerator(TypeDescriptor descriptor, Generator<?> generator, BeneratorContext context) {
        Converter<?,?> converter = DescriptorUtil.getConverter(descriptor.getConverter(), context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            }
            generator = DescriptorUtil.createConvertingGenerator(descriptor, generator, context);
        }
        return generator;
    }

    public static <T> Generator<T> wrapWithProxy(Generator<T> generator, TypeDescriptor descriptor) {
		boolean cyclic = descriptor.isCyclic() != null && descriptor.isCyclic();
		return wrapWithProxy(generator, cyclic);
    }

	public static <T> Generator<T> wrapWithProxy(Generator<T> generator, boolean cyclic) {
		if (cyclic)
			generator = WrapperFactory.applyCycler(generator);
		return generator;
    }

}
