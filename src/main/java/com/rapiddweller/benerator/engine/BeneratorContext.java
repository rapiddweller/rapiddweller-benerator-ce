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

import java.util.Locale;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.factory.DefaultsProvider;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.script.ScriptContext;

/**
 * A BeneratorContext.<br/><br/>
 * Created at 20.04.2008 06:41:04
 * @since 0.5.2
 * @author Volker Bergmann
 */
public interface BeneratorContext extends GeneratorContext, ScriptContext {
	
	// simple configuration properties ---------------------------------------------------------------------------------
	
	void setDefaultEncoding(String defaultEncoding);
	void setDefaultLineSeparator(String defaultLineSeparator);
	void setDefaultLocale(Locale defaultLocale);
	void setDefaultDataset(String defaultDataset);
    void setDefaultPageSize(long defaultPageSize);
    void setDefaultScript(String defaultScript);
    void setDefaultNull(boolean defaultNull);
	void setDefaultSeparator(char defaultSeparator);
	void setDefaultErrorHandler(String defaultErrorHandler);
	void setContextUri(String contextUri);
	void setValidate(boolean validate);
	void setMaxCount(Long maxCount);
	boolean isDefaultImports();
	void setDefaultImports(boolean defaultImports);
	boolean isDefaultOneToOne();
	void setDefaultOneToOne(boolean defaultOneToOne);
	boolean isAcceptUnknownSimpleTypes();
	void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes);
	
	// import handling -------------------------------------------------------------------------------------------------
	
	@Override
	void importClass(String className);
	void importPackage(String packageName);
	void importDefaults();

	// service provider sharing ----------------------------------------------------------------------------------------
	
	@Override
	GeneratorFactory getGeneratorFactory();
	void setGeneratorFactory(GeneratorFactory generatorFactory);
	
	DataModel getDataModel();
	void setDataModel(DataModel dataModel);
	
	DefaultsProvider getDefaultsProvider();
	void setDefaultsProvider(DefaultsProvider defaultsProvider);
	
	DescriptorProvider getLocalDescriptorProvider();
	void addLocalType(TypeDescriptor type);

	ComponentDescriptor getDefaultComponentConfig(String name);
	void setDefaultComponentConfig(ComponentDescriptor component);

	// data management -------------------------------------------------------------------------------------------------
	
	void setGlobal(String name, Object value);
	
	@Override
	ProductWrapper<?> getCurrentProduct();
	@Override
	void setCurrentProduct(ProductWrapper<?> currentProduct);
	boolean hasProductNameInScope(String productName);
	
	BeneratorContext createSubContext(String productName);

}
