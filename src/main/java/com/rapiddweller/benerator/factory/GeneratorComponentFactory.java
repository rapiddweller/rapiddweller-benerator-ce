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

import com.rapiddweller.benerator.composite.GeneratorComponent;
import com.rapiddweller.benerator.composite.Variable;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.Mode;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.model.data.VariableDescriptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Factory for {@link GeneratorComponent}s.<br/><br/>
 * Created: 08.08.2011 12:04:39
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class GeneratorComponentFactory {
	
	private static final Logger LOGGER = LogManager.getLogger(GeneratorComponentFactory.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static GeneratorComponent<?> createGeneratorComponent(InstanceDescriptor descriptor, Uniqueness ownerUniqueness, BeneratorContext context) {
		if (descriptor.getMode() == Mode.ignored) {
			LOGGER.debug("Ignoring {}", descriptor);
			return null;
		}
		if (descriptor instanceof ComponentDescriptor)
			return ComponentBuilderFactory.createComponentBuilder((ComponentDescriptor) descriptor, ownerUniqueness, context);
		else if (descriptor instanceof VariableDescriptor)
			return new Variable(descriptor.getName(), VariableGeneratorFactory.createGenerator((VariableDescriptor) descriptor, context), descriptor.getTypeDescriptor().getScope());
		else if (descriptor instanceof ArrayElementDescriptor)
			return ComponentBuilderFactory.createComponentBuilder((ArrayElementDescriptor) descriptor, ownerUniqueness, context);
		else
			throw new UnsupportedOperationException("Not a supported generator compnent type: " + descriptor.getClass());
	}
}
