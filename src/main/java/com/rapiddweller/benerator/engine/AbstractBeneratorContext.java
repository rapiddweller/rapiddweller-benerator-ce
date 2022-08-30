/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.BeneratorFactory;

/**
 * Abstract implementation of the {@link BeneratorContext} interface.<br/><br/>
 * Created: 16.08.2022 21:40:29
 * @author Volker Bergmann
 * @since 3.0.0
 */
public abstract class AbstractBeneratorContext implements BeneratorContext {

	@Override
	public String getVersion() {
		return BeneratorFactory.getInstance().getVersionInfo(false)[0];
	}

}
