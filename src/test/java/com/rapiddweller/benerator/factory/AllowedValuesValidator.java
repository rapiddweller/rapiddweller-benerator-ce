/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.factory;

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.Validator;

import java.util.Set;

/**
 * Accepts a value only if it exists in a predefined list of allowed values.<br/><br/>
 * Created: 02.08.2022 14:17:38
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class AllowedValuesValidator implements Validator<Object> {

	private final Set<Object> expectedValues;
	private boolean verbose;

	public AllowedValuesValidator(Object... expectedValues) {
		this.expectedValues = CollectionUtil.toSet(expectedValues);
		this.verbose = true;
	}

	@SuppressWarnings("unused")
	public boolean isVerbose() {
		return verbose;
	}

	@SuppressWarnings("unused")
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public boolean valid(Object product) {
		if (!expectedValues.contains(product)) {
			if (verbose) {
				String errMsg = "Unexpected product " + product;
				if (product != null) {
					errMsg += " (of " + product.getClass() + ")";
				}
				System.err.println(errMsg);
				return false;
			}
		}
		return true;
	}

}
