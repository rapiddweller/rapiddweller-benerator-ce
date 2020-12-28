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

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorFactory;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.benerator.factory.ComplexTypeGeneratorFactory;
import com.rapiddweller.benerator.factory.SimpleTypeGeneratorFactory;
import com.rapiddweller.commons.BeanUtil;
import com.rapiddweller.commons.Context;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.StringUtil;
import com.rapiddweller.commons.Validator;
import com.rapiddweller.commons.version.VersionInfo;

/**
 * Abstract factory class for extending Benerator.<br/><br/>
 * Created: 08.09.2010 15:43:11
 * @see DefaultBeneratorFactory
 * @since 0.6.4
 * @author Volker Bergmann
 */
public abstract class BeneratorFactory {

	public static final String BENERATOR_FACTORY_PROPERTY = "benerator.factory";
	private static String XML_SCHEMA_PATH = null;

	private static BeneratorFactory instance;

	public static BeneratorFactory getInstance() {
		if (instance == null) {
			String configuredClass = System.getProperty(BENERATOR_FACTORY_PROPERTY);
			if (StringUtil.isEmpty(configuredClass))
				configuredClass = DefaultBeneratorFactory.class.getName();
			instance = (BeneratorFactory) BeanUtil.newInstance(configuredClass);
		}
		return instance;
	}
	
	public static synchronized String getSchemaPathForCurrentVersion() {
		if (XML_SCHEMA_PATH == null) { 
			String version = VersionInfo.getInfo("benerator").getVersion();
			if (version.endsWith("-SNAPSHOT"))
				version = version.substring(0, version.length() - "-SNAPSHOT".length());
			if (version.endsWith("+jdk-8"))
				version = version.substring(0, version.length() - "+jdk-8".length());
			if (version.endsWith("+jdk-11"))
				version = version.substring(0, version.length() - "+jdk-11".length());
			if (version.endsWith("--project-version--"))
				version = "local";
			XML_SCHEMA_PATH = "com/rapiddweller/benerator/benerator-" + version + ".xsd";
		}
		return XML_SCHEMA_PATH;
	}

    public abstract BeneratorContext createContext(String contextUri);
    public abstract BeneratorParseContext createParseContext(ResourceManager resourceManager);
	public abstract Context createGenerationContext();
    
	public abstract ComplexTypeGeneratorFactory getComplexTypeGeneratorFactory();
	public abstract SimpleTypeGeneratorFactory getSimpleTypeGeneratorFactory();
	
	public abstract <S, T> Converter<S, T> configureConverter(Converter<S, T> converter, BeneratorContext context);
	public abstract <T> Validator<T> configureValidator(Validator<T> validator, BeneratorContext context);
	public abstract Consumer configureConsumer(Consumer consumer, BeneratorContext context);
	
}
