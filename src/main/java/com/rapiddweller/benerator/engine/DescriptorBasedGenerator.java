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

package com.rapiddweller.benerator.engine;

import java.io.IOException;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.commons.IOUtil;
import com.rapiddweller.commons.converter.ConverterManager;

/**
 * Provides easy programmatic access to generators defined in an XML descriptor file.<br/><br/>
 * Created: 23.02.2010 12:06:44
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DescriptorBasedGenerator extends GeneratorProxy<Object> {
	
	private DescriptorRunner descriptorRunner;

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public DescriptorBasedGenerator(String uri, String generatorName, BeneratorContext context) throws IOException {
		super(Object.class);
		ConverterManager.getInstance().setContext(context);
		descriptorRunner = new DescriptorRunner(uri, context);
		BeneratorRootStatement rootStatement = descriptorRunner.parseDescriptorFile();
		super.setSource((Generator) rootStatement.getGenerator(generatorName, context));
	}
	
	@Override
	public void close() {
		IOUtil.close(descriptorRunner);
		IOUtil.close(getSource());
	}
	
}
